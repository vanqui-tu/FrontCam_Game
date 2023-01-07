package com.example.frontcamgame.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.frontcamgame.*
import com.example.frontcamgame.models.Attempt
import com.example.frontcamgame.models.getAttempt
import com.example.frontcamgame.models.top10score
import com.example.frontcamgame.models.yourscore
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class scoreboard : AppCompatActivity() {
    private inner class viewPager2Adapter(fa: FragmentActivity, i1: Int, i2: Int) : FragmentStateAdapter(fa) {
        var i1: Int = i1
        var i2: Int = i2

        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }

    lateinit var back_btn: ImageButton
    lateinit var refresh_btn: ImageButton
    lateinit var tab_layout: TabLayout
    lateinit var viewPager2: ViewPager2
    lateinit var db: FirebaseFirestore
    lateinit var top10_list: MutableList<Attempt>
    lateinit var standing_list: MutableList<Attempt>
    private val TAB_TEXTS: List<String> = listOf("Your score", "Top 10")
    private var fragments: List<Fragment> = listOf(yourscore(), top10score())
    private var pagerAdapter: viewPager2Adapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        db = Firebase.firestore
        back_btn = findViewById<ImageButton>(R.id.scoreboard_back_btn)
        refresh_btn = findViewById<ImageButton>(R.id.scoreboard_refresh_btn)
        tab_layout = findViewById<TabLayout>(R.id.scoreboard_tl)
        viewPager2 = findViewById<ViewPager2>(R.id.scoreboard_viewpager2)
        top10_list = mutableListOf()
        setupEventsHandler()
        runBlocking {
            launch { refresh() }.join()
            setupViewPager()
        }
    }



    private fun setupViewPager() {
        pagerAdapter = viewPager2Adapter(this, 0, 0)
        viewPager2.adapter = pagerAdapter
        TabLayoutMediator(tab_layout, viewPager2) { tab, position ->
            tab.text = TAB_TEXTS[position]
        }.attach()
    }

    fun setupEventsHandler() {
        back_btn.setOnClickListener {
            finish()
        }
        refresh_btn.setOnClickListener {
            runBlocking {
                refresh()
            }
        }
    }

    private suspend fun refresh() {
//        when (viewPager2.currentItem) {
//            0 -> refresh_yourscore()
//            1 -> refresh_top10()
//        }
        refresh_yourscore()
        refresh_top10()
    }



    private suspend fun getTop10(): MutableList<Attempt> {
        var list: MutableList<Attempt> = mutableListOf()
        var coll = db.collection("high_score")


        var task = coll.whereGreaterThan("score", 0).orderBy("score", Query.Direction.DESCENDING).get()
        task.await()
        var i = 1;
        for (document in task.result) {
            var attempt = getAttempt(document)
            attempt.idx = i;
            list.add(attempt)
            i++;
        }
//        Log.d("getTop10", "returns " + list.size.toString())
        return list
    }

    private suspend fun getMyScore(): Attempt? {
        var coll = db.collection("high_score")
        var attempt: Attempt? = null

        var task = coll.whereEqualTo("uid", Firebase.auth.currentUser?.uid).get()
        task.await()
        if (task.isSuccessful && task.result.size() > 0)
        {
            attempt = getAttempt(task.result.first())
        }

        return attempt
    }

    private suspend fun getMyPos(myEmail: String?, myScore: Long?): Int? {
        if (myEmail == null || myScore == null)
            return null
        var coll = db.collection("high_score")

        var task = coll.whereGreaterThan("score", myScore).get()
        task.await()
        if (task.isSuccessful)
        {
            if (task.result.size() == 0)
                return 1
            else if (task.result.size() > 0)
                return task.result.size() + 1
        }

        return null
    }

    private suspend fun getStanding(): MutableList<Attempt> {
        var result: MutableList<Attempt> = mutableListOf()
        var myAttempt: Attempt? = getMyScore()

        if (myAttempt == null)
            return mutableListOf()

        var myPos: Int? = getMyPos(Firebase.auth.currentUser?.email, myAttempt.score)
        myAttempt.idx = myPos

        var coll = db.collection("high_score")


        var list2: MutableList<Attempt> = mutableListOf()

        var task1 = coll.whereGreaterThan("score", myAttempt.score!!).orderBy("score", Query.Direction.ASCENDING).limit(5).get()
        task1.await()
        if (task1.isSuccessful) {
            for (i in task1.result.size() - 1 downTo 0)
            {
                var attempt = getAttempt(task1.result.elementAt(i))
                if (myPos != null) {
                    attempt.idx = myPos - i - 1
                };
                result.add(attempt)
            }
        }

        var task2 = coll.whereLessThanOrEqualTo("score", myAttempt.score!!).orderBy("score", Query.Direction.DESCENDING).limit(5).get()
        task2.await()
        if (task2.isSuccessful) {
            for (item in task2.result)
            {
                var attempt = getAttempt(item)
                if (myPos != null && attempt.email != Firebase.auth.currentUser?.email) {
                    list2.add(attempt)
                }
            }
        }

        if (myPos != null)
            for (i in 0..list2.size - 1)
                list2[i].idx = myPos + i + 1

        result.add(myAttempt)
        for (item in list2)
            result.add(item)

        Log.d("getStanding", result.size.toString())
        return result
    }


    private suspend fun refresh_yourscore() {
        STANDING_LIST = getStanding()
        Log.d("Refresh yourscore", STANDING_LIST.size.toString())
        pagerAdapter?.let {
            it.i1 += 1
            it.notifyItemChanged(0)
        }
    }

    private suspend fun refresh_top10() {
        TOP10_LIST = getTop10()
        Log.d("Refresh top 10", TOP10_LIST.size.toString())
        pagerAdapter?.let {
            it.i2 += 1
            it.notifyItemChanged(1)
        }
    }
}




















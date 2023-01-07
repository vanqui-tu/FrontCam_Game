package com.example.frontcamgame.models

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontcamgame.models.Attempt
import com.example.frontcamgame.R
import com.squareup.picasso.Picasso

class RVAdapter(private val data: List<Attempt>) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item_idx: TextView
        val item_image: ImageView
        val item_name: TextView
        val item_email: TextView
        val item_score: TextView

        init {
            // Define click listener for the ViewHolder's View.
            item_idx = view.findViewById(R.id.item_idx)
            item_image = view.findViewById(R.id.item_image)
            item_name = view.findViewById(R.id.item_name)
            item_email = view.findViewById(R.id.item_email)
            item_score = view.findViewById(R.id.item_score)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var attempt: Attempt = data[position]
        viewHolder.item_idx.text = attempt.idx?.toString()
        viewHolder.item_name.text = attempt.name
        viewHolder.item_email.text = attempt.email
        viewHolder.item_score.text = attempt.score.toString()
        if (attempt.avatarURL != null)
            if (attempt.avatarURL!!.length > 0)
                    Picasso.get().load(Uri.parse(attempt.avatarURL)).fit().into(viewHolder.item_image)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
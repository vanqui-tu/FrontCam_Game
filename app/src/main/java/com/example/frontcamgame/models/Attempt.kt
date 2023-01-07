package com.example.frontcamgame.models
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot

class Attempt(
    var idx: Int?,
    var uid: String?,
    var name: String?,
    var email: String?,
    var score: Long?,
    var time: Timestamp?,
    var avatarURL: String?
) : java.io.Serializable {
    override fun toString(): String {
        return "Attempt(idx=$idx, uid=$uid, name=$name, email=$email, score=$score, time=$time, avatarURL=$avatarURL)"
    }
}

fun getAttempt(document: QueryDocumentSnapshot) : Attempt {
    var uid = document["uid"]
    var email = document["email"]
    var score = document["score"]
    var name = document["name"]
    var avatarURL = document["avatar"]
    return Attempt(0, uid as String?, name as String?, email as String?, score as Long?, null, avatarURL as String?);
}


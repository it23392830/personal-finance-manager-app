package com.example.financeflow.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun currentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun userDocument() =
        firestore.collection("users")
            .document(currentUserId())

    fun transactionsCollection() =
        userDocument().collection("transactions")

    fun goalsCollection() =
        userDocument().collection("goals")

    fun preferencesDocument() =
        userDocument()
            .collection("preferences")
            .document("user_preferences")
}
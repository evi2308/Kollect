package com.evasanchez.kollect.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
/*
private val coroutineScope = CoroutineScope(Dispatchers.IO)
class KGroupRepository {
    val db = Firebase.firestore
    val usersCollection = db.collection("usuario")

    // Get user profile
    fun getKgroups(userId: String) : List<String>{

        val query = usersCollection.whereEqualTo("userId", userId)

        query.get().addOnSuccessListener { querySnapshot ->

            coroutineScope.launch {

                val userDocRef = querySnapshot.documents.first().reference

                val kgroups = getKgroupsList(userDocRef)



            }

        }

    }

    suspend fun getKgroupsList(userDocRef: DocumentReference): List<String> {
        val kgroupsRef = userDocRef.collection("Kgroups")
        return kgroupsRef.get().await()
            .documents
            .map { doc ->
                val groupName = doc.get("group_name") as String
                groupName
            }

    }
}


*/
package com.evasanchez.kollect.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileScreenViewModel {
    private val auth: FirebaseAuth = Firebase.auth
    val userID = auth.currentUser?.uid
    val db = Firebase.firestore
    private val _kGroup = MutableLiveData<String>()
    val kGroup: LiveData<String> = _kGroup
    private val _idol = MutableLiveData<String>()
    val idol: LiveData<String> = _idol

    private var subColReference: CollectionReference? = null // Subcollection reference

    init {
        if (userID != null) {
            subColReference = getSubcollectionReference(userID)
        }
    }

    fun onKGroupChanged(kGroup: String) {
        _kGroup.value = kGroup
    }

    fun onIdolChanged(idol: String) {
        _idol.value = idol
    }

    fun addKgroupToUser(kGroup: String) {
        if (subColReference != null) {
            val kGroupData = mapOf("group_name" to kGroup)
            subColReference!!.add(kGroupData)
                .addOnSuccessListener {
                    Log.d("Hola", "Se ha creado el grupo")
                }
                .addOnFailureListener {
                    Log.d("Hola", "Algo ha malido sal")
                }
        }
    }

    // Function to get the subcollection reference
    private fun getSubcollectionReference(userId: String): CollectionReference? {
        // Reference to the "users" collection
        val usersCollection = db.collection("usuario")

        // Create a query to find the document with matching "user_id"
        val query = usersCollection.whereEqualTo("user_id", userId)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val documentPath = documentSnapshot.reference.path
                    Log.d("Hola", "El documentPath es: $documentPath")
                    subColReference = db.document(documentPath).collection("Kgroups")
                }
            }
        return subColReference
    }

    // Function to get a list of documents in the subcollection
    fun getKGroupList(callback: (List<String>?) -> Unit) {
        if (subColReference != null) {
            subColReference!!.get()
                .addOnSuccessListener { querySnapshot ->
                    val documentList = mutableListOf<String>()
                    for (document in querySnapshot.documents) {
                        val groupName = document.getString("group_name")
                        if (groupName != null) {
                            documentList.add(groupName)
                        }
                    }
                    callback(documentList)
                }
                .addOnFailureListener { exception ->
                    Log.d("Hola", "Error: $exception")
                    callback(null)
                }
        } else {
            callback(null)
        }
    }
}
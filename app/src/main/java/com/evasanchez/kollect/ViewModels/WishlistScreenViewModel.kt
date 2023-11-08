package com.evasanchez.kollect.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evasanchez.kollect.data.Photocard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WishlistScreenViewModel: ViewModel() {
    private var subColReference: CollectionReference? = null // Subcollection reference
    private val auth: FirebaseAuth = Firebase.auth

    val userID = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val _photocardsWishlistList = MutableLiveData<List<Photocard>>() // Change this to hold a list of Photocard
    val photocardsWishlistList: LiveData<List<Photocard>> = _photocardsWishlistList // Use LiveData to expose the list

    init {
        Log.d("A ver", "Entra en el init de WishlistScreen")
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        viewModelScope.launch {
            getPhotocardsList()  }
    }

    suspend fun getWishlistSubcollectionReference(userId: String): CollectionReference {

        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("user_id", userId)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Wishlist")
        } else {
            throw NoSuchElementException("User not found")
        }
    }


    fun getPhotocardsList() {
        viewModelScope.launch(Dispatchers.IO) {
            val subColReference = userID?.let { getWishlistSubcollectionReference(it) }

            if (subColReference != null) {
                try {
                    val querySnapshot = subColReference.get().await()
                    val photocardObjList = mutableListOf<Photocard>()

                    for (document in querySnapshot.documents) {
                        val photocardObj = document.toObject(Photocard::class.java)
                        if (photocardObj != null) {
                            Log.d("Hostia", "photocard_url")
                            photocardObjList.add(photocardObj)
                        }
                    }

                    _photocardsWishlistList.postValue(photocardObjList)
                } catch (e: Exception) {
                    Log.e("Firestore Query Error", e.message ?: "Unknown error")
                    _photocardsWishlistList.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference is null")
                _photocardsWishlistList.postValue(emptyList())
            }
        }
    }
}
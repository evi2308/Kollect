package com.evasanchez.kollect.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.evasanchez.kollect.data.Photocard
import com.evasanchez.kollect.data.PhotocardRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeScreenViewModel: ViewModel() {
    val repository = PhotocardRepository()
    val collectionPhotocards: LiveData<List<Photocard>> = repository.photocards.map { photocards ->
        photocards.filter { it.status == "Coleccion" }
    }
    private var subColReference: CollectionReference? = null // Subcollection reference
    private val auth: FirebaseAuth = Firebase.auth
    val userID = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val _photocardsList = MutableLiveData<List<String>>() // Change this to hold a list of Photocard
    val photocardsList: LiveData<List<String>> = _photocardsList // Use LiveData to expose the list

    init {
        Log.d("A ver", "Entra en el init de HomeScreen")
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        viewModelScope.launch {
            getPhotocardsList()  }

    }

    suspend fun getColeccionSubcollectionReference(userId: String): CollectionReference {

        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("user_id", userId)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Coleccion")
        } else {
            throw NoSuchElementException("User not found")
        }
    }

    fun getPhotocardsList() {
        viewModelScope.launch(Dispatchers.IO) {
            val subColReference = userID?.let { getColeccionSubcollectionReference(it) }

            if (subColReference != null) {
                try {
                    val querySnapshot = subColReference.get().await()
                    val pcs_ids = mutableListOf<String>()

                    for (document in querySnapshot.documents) {
                        val photocard_id = document.getString("photocard_id")
                        if (photocard_id != null) {
                            Log.d("Hostia", photocard_id)
                            pcs_ids.add(photocard_id)
                        }
                    }

                    _photocardsList.postValue(pcs_ids)
                } catch (e: Exception) {
                    Log.e("Firestore Query Error", e.message ?: "Unknown error")
                    _photocardsList.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference is null")
                _photocardsList.postValue(emptyList())
            }
        }
    }
    }


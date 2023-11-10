package com.evasanchez.kollect.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeScreenViewModel: ViewModel() {
    private var subColReference: CollectionReference? = null
    private val auth: FirebaseAuth = Firebase.auth

    val userID = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val _photocardsList = MutableLiveData<List<Photocard>>()
    val photocardsList: LiveData<List<Photocard>> = _photocardsList // LISTA PARA LAS PHOTOCARDS QUE PERTENECEN A LA COLECCION

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
                    val photocardObjList = mutableListOf<Photocard>()

                    for (document in querySnapshot.documents) {
                        val photocardObj = document.toObject(Photocard::class.java)
                        if (photocardObj != null) {
                            Log.d("Hostia", "photocard_url")
                            photocardObjList.add(photocardObj)
                        }
                    }

                    _photocardsList.postValue(photocardObjList)
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

    private val _selectedPhotocard = MutableLiveData<Photocard>()
    val selectedPhotocard : LiveData<Photocard> = _selectedPhotocard
    var selectedPhotocardDetail by mutableStateOf<Photocard?>(null)
        private set

    fun addPhotocardDetail(photocardDetailed: Photocard){
        selectedPhotocardDetail = photocardDetailed
    }
    fun setPhotocardDetails(photocard: Photocard){
        Log.d("A ver Debug", "Photocard guardada = ${photocard.photocardId}")
        _selectedPhotocard.postValue(photocard)

    }
    }


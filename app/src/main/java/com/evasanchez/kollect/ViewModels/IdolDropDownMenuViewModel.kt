package com.evasanchez.kollect.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class IdolDropDownMenuViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _allIdols = MutableLiveData<List<String>>()
    val allIdols: LiveData<List<String>> = _allIdols
    val userID = auth.currentUser?.uid
    val db = Firebase.firestore
    init {
        Log.d("A ver", "Entra en el init")
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        viewModelScope.launch {
            getIdolListRepository()
            Log.d("PAAAA", allIdols.value.toString())  }

    }

    suspend fun getIdolSubColReference(userId: String): CollectionReference {
        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("user_id", userId)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Idols")
        } else {
            throw NoSuchElementException("User not found")
        }
    }
    suspend fun getIdolListRepository() {
        viewModelScope.launch(Dispatchers.IO) {
            val subColReference = userID?.let { getIdolSubColReference(it) }
            if (subColReference != null) {
                try {
                    val querySnapshot = subColReference.get().await()
                    val groupNames = mutableListOf<String>()

                    for (document in querySnapshot.documents) {
                        val name = document.getString("idol_name")
                        if (name != null) {
                            Log.d("Hostia", name)
                            groupNames.add(name)
                        }
                    }

                    _allIdols.postValue(groupNames)
                } catch (e: Exception) {
                    Log.e("Firestore Query Error", e.message ?: "Unknown error")
                    _allIdols.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference is null")
                _allIdols.postValue(emptyList())
            }
        }
    }



}
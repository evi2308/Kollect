package com.evasanchez.kollect.ViewModels

import android.net.Uri
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

class PhotocardFormViewModel: ViewModel() {
 val db = Firebase.firestore
   private val auth: FirebaseAuth = Firebase.auth
   val userID = auth.currentUser?.uid

    private val _albumName = MutableLiveData<String>()
    val albumName : LiveData<String> = _albumName

    private val _status = MutableLiveData<String>()
    val status : LiveData<String> = _status

    private val _groupName =MutableLiveData<String>()
    val groupName : LiveData<String> = _groupName

    private val _idolName = MutableLiveData<String>()
    val idolName : LiveData<String> = _idolName

    private val _value = MutableLiveData<String>()
    val value : LiveData<String> = _value

    private val _type = MutableLiveData<String>()
    val type : LiveData<String> = _type

    private val _photocardUri = MutableLiveData<Uri>()
    val photocardUri : LiveData<Uri> = _photocardUri

    private val _photocardVersion = MutableLiveData<String>()
    val photocardVersion : LiveData<String> = _photocardVersion

    private val _allGroups = MutableLiveData<List<String>>()
    val allGroups: LiveData<List<String>> = _allGroups

    private val _allIdols = MutableLiveData<List<String>>()
    val allIdols: LiveData<List<String>> = _allIdols

 init {
  Log.d("A ver", "Entra en el init")
  val db = FirebaseFirestore.getInstance()
  db.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
  viewModelScope.launch {
   getKGroupListRepository()
   val selectedGroup = ""
   getIdolsBasedOnKgroup(selectedGroup)
   Log.d("PAAAA", allGroups.value.toString())  }

 }
 fun onPhotocardUriChanged(uri: Uri?) {
  _photocardUri.value = uri
 }
 fun onFormTextFieldChange(albumName: String, value:String, type:String, photocardVersion:String){
  _albumName.value = albumName
  _value.value = value
  _type.value = type
  _photocardVersion.value = photocardVersion
 }
 suspend fun getSubcollectionReference(userId: String): CollectionReference {

  val usersCollection = db.collection("usuario")
  val query = usersCollection.whereEqualTo("user_id", userId)
  val querySnapshot = query.get().await()

  if (!querySnapshot.isEmpty) {
   val documentSnapshot = querySnapshot.documents.first()
   val documentPath = documentSnapshot.reference.path
   return db.document(documentPath).collection("Kgroups")
  } else {
   throw NoSuchElementException("User not found")
  }
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

 // Function to get a list of documents in the subcollection
 suspend fun getKGroupListRepository() {
  viewModelScope.launch(Dispatchers.IO) {
   val subColReference = userID?.let { getSubcollectionReference(it) }

   if (subColReference != null) {
    try {
     val querySnapshot = subColReference.get().await()
     val groupNames = mutableListOf<String>()

     for (document in querySnapshot.documents) {
      val name = document.getString("group_name")
      if (name != null) {
       Log.d("Hostia", name)
       groupNames.add(name)
      }
     }

     _allGroups.postValue(groupNames)
    } catch (e: Exception) {
     Log.e("Firestore Query Error", e.message ?: "Unknown error")
     _allGroups.postValue(emptyList())
    }
   } else {
    Log.d("Else", "subColReference is null")
    _allGroups.postValue(emptyList())
   }
  }
 }

 fun getIdolsBasedOnKgroup(selectedGroup: String) {
  viewModelScope.launch(Dispatchers.IO) {
   val subColReference = userID?.let { getIdolSubColReference(it) }

   if (subColReference != null) {
    try {
     val idolsQuery = subColReference.whereEqualTo("group_name", selectedGroup).get().await()
     val idolsNames = mutableListOf<String>()

     for (document in idolsQuery) {
      val name = document.getString("idol_name") // Assuming idol_name is the field you want to retrieve
      if (name != null) {
       Log.d("Hostia", name)
       idolsNames.add(name)
      }
     }

     _allIdols.postValue(idolsNames)
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




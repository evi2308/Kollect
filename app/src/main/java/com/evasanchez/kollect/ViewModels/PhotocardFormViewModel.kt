package com.evasanchez.kollect.ViewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evasanchez.kollect.data.Photocard
import com.evasanchez.kollect.data.PhotocardRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class PhotocardFormViewModel: ViewModel() {
   val repository = PhotocardRepository()
   val db = Firebase.firestore
   private val auth: FirebaseAuth = Firebase.auth
   val userID = auth.currentUser?.uid
   val storage = FirebaseStorage.getInstance()
   val storageRef = storage.reference

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

 private val _showDialog = MutableLiveData<Boolean>()
 val showDialog: LiveData<Boolean> = _showDialog

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

 fun onStatusChanged(status: String){
  _status.value = status
 }
 fun onIdolSelected(idolName: String) {
  _idolName.value = idolName
 }

 fun onGroupSelected(groupName: String) {
  _groupName.value = groupName
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

 fun createPhotocard(){
  //Darle nombre a la photocard
   var randomNum: String = Random.nextInt().toString()
   var photocardNameStorage: String = "Photocard" + randomNum
   Log.d("Nombre de la foto", "Nombre : ${photocardNameStorage}")
  //Referencia a la photocard
   val photocardRef = storageRef.child("photocardPics/${photocardNameStorage}")
   val uri = photocardUri.value
   if(uri != null){
    val uploadTask = photocardRef.putFile(uri)
    uploadTask.addOnFailureListener {
     Log.d("Error al subir la imagen", "Ha habido algun error")
    }.addOnSuccessListener { taskSnapshot ->
     photocardRef.downloadUrl.addOnSuccessListener { uri ->
      // Sacar la URI del archivo desde el storage
      val downloadUri = uri.toString()
      viewModelScope.launch {
       addPhotocardToDB(downloadUri, photocardNameStorage)

      }
     }
    }.addOnFailureListener { exception ->
     Log.d("Error", "Vuelve a intentarlo mas tarde")
    }

   }else{
    val defaultImageRef = storageRef.child("photocardPics/photocard_default.jpg")
    defaultImageRef.downloadUrl.addOnSuccessListener {downloadUri->
     val defaultImageURL = downloadUri.toString()
     viewModelScope.launch {
      addPhotocardToDB(defaultImageURL, photocardNameStorage)
     }

    }
   }
 }

 suspend fun addPhotocardToDB(photocardUri: String, photocardNameStorage: String){
  val photocard = Photocard(
   photocardId = photocardNameStorage,
   albumName = albumName.value.toString(),
   status = status.value.toString(),
   groupName = groupName.value.toString(),
   idolName = idolName.value.toString(),
   value = value.value.toString(),
   type = type.value.toString(),
   photocardURL = photocardUri,
   photocardVersion = photocardVersion.value.toString()
  )
   val photocardMap=photocard.photocardToMap()
   if(status.value == "Wishlist"){
    val subColRefWl = userID?.let { getWishlistSubcollectionReference(it) }
    if (subColRefWl != null) {
     subColRefWl.add(photocardMap)
      .addOnSuccessListener {
       Log.d("HURRA", "SE HA AÑADIDO LA PHOTOCARD(aparentemente)")
       _showDialog.postValue(true)
       repository.addPhotocardToMasterist(photocard)
      }
      .addOnFailureListener {
       Log.d("Jope", "Algo ha salido mal me voy a matar")
      }
    }
   }else{
    val subColRefColeccion =userID?.let { getColeccionSubcollectionReference(it) }
    if (subColRefColeccion != null){
     subColRefColeccion.add(photocardMap)
      .addOnSuccessListener {
       Log.d("HURRA", "SE HA AÑADIDO LA PHOTOCARD(aparentemente)")
       _showDialog.postValue(true)
       repository.addPhotocardToMasterist(photocard)
      }
      .addOnFailureListener {
       Log.d("Jope", "Algo ha salido mal me voy a matar")
      }
    }
   }
 }

 fun onDismissDialog() {
  _showDialog.value = false
 }
 }




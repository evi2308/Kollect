package com.evasanchez.kollect.ViewModels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class CollectionWishlistViewModel: ViewModel() {
    val db = FirebaseFirestore.getInstance()

    private val _photocardsList = MutableLiveData<List<Photocard>>()
    val photocardsList: LiveData<List<Photocard>> =
        _photocardsList // LISTA PARA LAS PHOTOCARDS QUE PERTENECEN A LA COLECCION

    val usersCollection = db.collection("usuario")

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _photocardsWishlistList = MutableLiveData<List<Photocard>>()
    val photocardsWishlistList: LiveData<List<Photocard>> = _photocardsWishlistList

    private val _dialogText = MutableLiveData<String>()
    val dialogText: LiveData<String> = _dialogText

    private val _allGroups = MutableLiveData<List<String>>()
    val allGroups: LiveData<List<String>> = _allGroups

    private val _allIdols = MutableLiveData<List<String>>()
    val allIdols: LiveData<List<String>> = _allIdols

    private val _groupName = MutableLiveData<String>()
    val groupName: LiveData<String> = _groupName

    val _editMode = MutableLiveData<Boolean>()
    val editMode: LiveData<Boolean> = _editMode

    //Para la edicion de la photocard
    val _newValue = MutableLiveData<String>()
    val newValue: LiveData<String> = _newValue

    val _newPhotocardUri = MutableLiveData<Uri>()
    val newPhotocardUri: LiveData<Uri> = _newPhotocardUri

    val _newType = MutableLiveData<String>()
    val newType: LiveData<String> = _newType

    val _newPhotocardVersion = MutableLiveData<String>()
    val newPhotocardVersion: LiveData<String> = _newPhotocardVersion




    fun onEditModeChanged(editModeChanged: Boolean) {
        _editMode.value = editModeChanged
    }

    fun onNewValueChanged(newValue: String) {
        _newValue.value = newValue
    }

    fun onNewTypeChanged(newType: String) {
        _newType.value = newType
    }

    fun onNewPhotocardVersionChanged(newPhotocardVersion: String) {
        _newPhotocardVersion.value = newPhotocardVersion
    }

    fun onNewPhotocardUriChanged(newPhotocardUri: Uri) {
        _newPhotocardUri.value = newPhotocardUri
    }

    val _editMode = MutableLiveData<Boolean> ()
    val editMode: LiveData<Boolean> = _editMode

    fun onEditModeChanged(editModeChanged: Boolean) {
        _editMode.value = editModeChanged
    }


    init {
        Log.d("Init", "Entra en el init de HomeScreen")
        val db = FirebaseFirestore.getInstance()
        //viewModelScope.launch {
        //  getPhotocardsList()  }

    }

    // Funciones para rellenar los dropdown para el filtro?
    fun showInfoToast(message: String) {
        //_InfoMessage.postValue(message)
    }

    fun onGroupSelected(groupName: String) {
        _groupName.value = groupName
    }

    suspend fun getKGroupListRepository() {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReference = userID?.let { getSubcollectionReference(it) }

            if (subColReference != null) {
                try {
                    val querySnapshot = subColReference.get().await()
                    val groupNames = mutableListOf<String>()

                    for (document in querySnapshot.documents) {
                        val name = document.getString("group_name")
                        if (name != null) {
                            groupNames.add(name)
                        }
                    }

                    _allGroups.postValue(groupNames)
                } catch (e: Exception) {
                    _allGroups.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference is null")
                _allGroups.postValue(emptyList())
            }
        }
    }

    //Sacar lista de idols relacionadas con el grupo seleccionado
    fun getIdolsBasedOnKgroup(selectedGroup: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReference = userID?.let { getIdolSubColReference(it) }

            if (subColReference != null) {
                try {
                    val idolsQuery =
                        subColReference.whereEqualTo("group_name", selectedGroup).get().await()
                    val idolsNames = mutableListOf<String>()

                    for (document in idolsQuery) {
                        val name =
                            document.getString("idol_name") // Assuming idol_name is the field you want to retrieve
                        if (name != null) {
                            Log.d("Nombre del idol", name)
                            idolsNames.add(name)
                        }
                    }

                    _allIdols.postValue(idolsNames)
                } catch (e: Exception) {
                    _allIdols.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference es null")
                _allIdols.postValue(emptyList())
            }
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


    fun clearData() {
        // Volver a poner a null los valores
        _photocardsList.postValue(emptyList())
        _selectedPhotocard.postValue(null)
        Log.d("VALORES BORRADOS?", _photocardsList.value.toString())
    }

    //Sacar referencia a la subcoleccion "Coleccion" del usuario que esta logeado
    suspend fun getColeccionSubcollectionReference(userId: String): CollectionReference {


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

    //Sacar referencia a la subcoleccion "Wishlist" del usuario que esta logeado

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

    //Sacar lista de photocards de la coleccion del usuario logeado
    fun getPhotocardsCollectionList() {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReference = userID?.let { getColeccionSubcollectionReference(it) }

            if (subColReference != null) {
                try {
                    val querySnapshot = subColReference.get().await()
                    val photocardObjList = mutableListOf<Photocard>()

                    for (document in querySnapshot.documents) {
                        val photocardObj = document.toObject(Photocard::class.java)
                        if (photocardObj != null) {
                            Log.d("Photocard encontrada", "")
                            photocardObjList.add(photocardObj)
                        }
                    }

                    _photocardsList.postValue(photocardObjList)
                } catch (e: Exception) {
                    _photocardsList.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference es null")
                _photocardsList.postValue(emptyList())
            }
        }
    }

    //Lista de photocards en la Wishlist del usuario

    fun getPhotocardsWishlistList() {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReference = userID?.let { getWishlistSubcollectionReference(it) }

            if (subColReference != null) {
                try {
                    val querySnapshot = subColReference.get().await()
                    val photocardObjList = mutableListOf<Photocard>()

                    for (document in querySnapshot.documents) {
                        val photocardObj = document.toObject(Photocard::class.java)
                        if (photocardObj != null) {
                            Log.d("Photocard encontrada", "")
                            photocardObjList.add(photocardObj)
                        }
                    }

                    _photocardsWishlistList.postValue(photocardObjList)
                } catch (e: Exception) {
                    _photocardsWishlistList.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference es null")
                _photocardsWishlistList.postValue(emptyList())
            }
        }
    }

    private val _selectedPhotocard = MutableLiveData<Photocard>()
    val selectedPhotocard: LiveData<Photocard> = _selectedPhotocard
    var selectedPhotocardDetail by mutableStateOf<Photocard?>(null)
        private set

    //Sacar detalle de la photocard para la pantalla de detalles
    fun addPhotocardDetail(photocardDetailed: Photocard) {
        selectedPhotocardDetail = photocardDetailed
    }

    //Funcion para borrar las photocards tanto de la wishlist como de la coleccion, con la LastScreenRoute para saber desde que pantalla se ha llamado al metodo
    fun deletePhotocard(lastScreenRoute: String, photocardDetailed: Photocard) {
        viewModelScope.launch(Dispatchers.IO) {
            if (lastScreenRoute == "home_screen") {
                val userID = FirebaseAuth.getInstance().currentUser?.uid
                val subColReference = userID?.let { getColeccionSubcollectionReference(it) }
                if (subColReference != null) {
                    val photocardQuery =
                        subColReference.whereEqualTo("photocard_id", photocardDetailed.photocardId)
                            .get().await()
                    if (!photocardQuery.isEmpty) {
                        val photocardId = photocardQuery.documents.first().id
                        subColReference.document(photocardId).delete().addOnSuccessListener {
                            Log.d("Photocard Borrada", "Photocard Borrada existosamente")
                            _showDialog.postValue(true)
                            _dialogText.postValue("Photocard Borrada existosamente")
                        }.addOnFailureListener {
                            Log.d("Error", "Error al borrar la phtotocard")
                            _dialogText.postValue("Algo ha salido mal, inténtalo de nuevo más tarde")
                        }
                    }
                }
            }
            if (lastScreenRoute == "wishlist_screen") {
                val userID = FirebaseAuth.getInstance().currentUser?.uid
                val subColReference = userID?.let { getWishlistSubcollectionReference(it) }
                if (subColReference != null) {
                    val photocardQuery =
                        subColReference.whereEqualTo("photocard_id", photocardDetailed.photocardId)
                            .get().await()
                    if (!photocardQuery.isEmpty) {
                        val photocardId = photocardQuery.documents.first().id
                        subColReference.document(photocardId).delete().addOnSuccessListener {
                            Log.d("Photocard Borrada", "Photocard Borrada existosamente")
                            _showDialog.postValue(true)
                            _dialogText.postValue("Photocard Borrada existosamente")
                        }.addOnFailureListener {
                            Log.d("Error", "Error al borrar la phtotocard")
                            _dialogText.postValue("Algo ha salido mal, inténtalo de nuevo más tarde")
                        }
                    }
                }
            }

        }

    }

    fun moveFromColtoWishlist(photocardDetailed: Photocard) {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReferenceColeccion = userID?.let { getColeccionSubcollectionReference(it) }
            val subColReferenceWishlist = userID?.let { getWishlistSubcollectionReference(it) }
            if (subColReferenceColeccion != null && subColReferenceWishlist != null) {
                val photocardQuery = subColReferenceColeccion.whereEqualTo(
                    "photocard_id",
                    photocardDetailed.photocardId
                ).get().await()
                if (!photocardQuery.isEmpty) {
                    val photocardData = photocardQuery.documents.first().data
                    val photocardId = photocardQuery.documents.first().id
                    photocardData?.set("status", "Wishlist") //Borrar si no soluciona el tema
                    subColReferenceColeccion.document(photocardId).delete().addOnSuccessListener {
                        Log.d("Photocard Eliminada", "Photocard Eliminada, se moverá")

                        subColReferenceWishlist.add(photocardData!!).addOnSuccessListener {
                            Log.d("Photocard Movida", "Photocard Movida a la Wishlist Exitosamente")
                            _showDialog.postValue(true)
                            _dialogText.postValue("Photocard Movida a la wishlist exitosamente")
                        }
                    }.addOnFailureListener {
                        Log.d("Error", "Error al borrar la phtotocard")
                        _showDialog.postValue(true)
                        _dialogText.postValue("Algo ha salido mal, inténtalo de nuevo más tarde")
                    }


                }
            }

        }
    }

    fun moveFromWishlisttoCol(photocardDetailed: Photocard) {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReferenceColeccion = userID?.let { getColeccionSubcollectionReference(it) }
            val subColReferenceWishlist = userID?.let { getWishlistSubcollectionReference(it) }

            if (subColReferenceColeccion != null && subColReferenceWishlist != null) {
                val photocardQuery = subColReferenceWishlist.whereEqualTo(
                    "photocard_id",
                    photocardDetailed.photocardId
                ).get().await()

                if (!photocardQuery.isEmpty) {
                    val photocardDocument = photocardQuery.documents.first()

                    // Modify the local values
                    val photocardData = photocardDocument.data?.toMutableMap()
                    photocardData?.set("is_otw", false)
                    photocardData?.set("status", "Coleccion") //Borrar si no soluciona el tema
                    // Update the Firestore document
                    subColReferenceWishlist.document(photocardDocument.id).delete()
                        .addOnSuccessListener {
                            Log.d("Photocard Eliminada", "Ole se ha eliminado toca moverla")

                            subColReferenceColeccion.add(photocardData!!).addOnSuccessListener {
                                Log.d(
                                    "Photocard Movida",
                                    "Photocard movida a tu coleccion exitosamente"
                                )
                                _showDialog.postValue(true)
                                _dialogText.postValue("Photocard movida a tu coleccion exitosamente")
                            }.addOnFailureListener {
                                Log.d("Error", "Error al agregar la photocard a la colección")
                                _showDialog.postValue(true)
                                _dialogText.postValue("Algo ha salido mal, inténtalo de nuevo más tarde")
                            }
                        }.addOnFailureListener {
                        Log.d("Error", "Error al borrar la photocard de la wishlist")
                        _showDialog.postValue(true)
                        _dialogText.postValue("Algo ha salido mal, inténtalo de nuevo más tarde")
                    }
                }
            }
        }
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

    fun onDismissDialog() {
        _showDialog.value = false
    }

    //ARREGLAR
    fun updatePrioStatus(photocard: Photocard) {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid

            if (photocard.status == "Wishlist") {
                val subColReference = userID?.let { getWishlistSubcollectionReference(it) }
                if (subColReference != null) {
                    val photocardQuery =
                        subColReference.whereEqualTo("photocard_id", photocard.photocardId).get()
                            .await()

                    val firstDocument = photocardQuery.documents.firstOrNull()
                    if (firstDocument != null) {
                        val photocardData = firstDocument.data
                        if (photocardData != null) {
                            var new_value = false
                            if (photocardData["is_prio"] == true) {
                                new_value = false
                            }
                            if (photocardData["is_prio"] == false) {
                                new_value = true
                            }
                            photocardData["is_prio"] = new_value
                            subColReference.document(firstDocument.id).update(photocardData)
                            if (photocardData["is_prio"] as Boolean) {
                                showToast("La photocard se ha marcado como prioridad ")
                            } else {
                                showToast("La photocard ya no está marcada como prioridad ")
                            }
                        }
                    }
                }
            }

            if (photocard.status == "Coleccion") {
                val subColReference = userID?.let { getColeccionSubcollectionReference(it) }
                if (subColReference != null) {
                    val photocardQuery =
                        subColReference.whereEqualTo("photocard_id", photocard.photocardId).get()
                            .await()

                    val firstDocument = photocardQuery.documents.firstOrNull()
                    if (firstDocument != null) {
                        val photocardData = firstDocument.data
                        if (photocardData != null) {
                            var new_value = false
                            if (photocardData["is_prio"] == true) {
                                new_value = false
                            }
                            if (photocardData["is_prio"] == false) {
                                new_value = true
                            }
                            photocardData["is_prio"] = new_value
                            subColReference.document(firstDocument.id).update(photocardData)
                            if (photocardData["is_prio"] as Boolean) {
                                showToast("La photocard se ha marcado como prioridad ")
                            } else {
                                showToast("La photocard ya no está marcada como prioridad ")
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateOTWStatus(photocard: Photocard) {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            if (photocard.status == "Wishlist") {
                val subColReference = userID?.let { getWishlistSubcollectionReference(it) }
                if (subColReference != null) {
                    val photocardQuery =
                        subColReference.whereEqualTo("photocard_id", photocard.photocardId).get()
                            .await()

                    val document = photocardQuery.documents.firstOrNull()
                    if (document != null) {
                        val photocardData = document.data
                        if (photocardData != null) {
                            var new_value = false
                            if (photocardData["is_otw"] == true) {
                                new_value = false
                            }
                            if (photocardData["is_otw"] == false) {
                                new_value = true
                            }
                            photocardData["is_otw"] = new_value
                            subColReference.document(document.id).update(photocardData)
                            if (photocardData["is_otw"] as Boolean) {
                                showToast("La photocard se ha marcado como OTW ")
                            } else {
                                showToast("La photocard ya no está OTW ")
                            }
                        }
                    }
                }
            }
            if (photocard.status == "Coleccion") {
                val subColReference = userID?.let { getColeccionSubcollectionReference(it) }
                if (subColReference != null) {
                    val photocardQuery =
                        subColReference.whereEqualTo("photocard_id", photocard.photocardId).get()
                            .await()
                    val document = photocardQuery.documents.firstOrNull()
                    if (document != null) {
                        val photocardData = document.data
                        if (photocardData != null) {
                            var new_value = false
                            if (photocardData["is_otw"] == true) {
                                new_value = false
                            }
                            if (photocardData["is_otw"] == false) {
                                new_value = true
                            }
                            photocardData["is_otw"] = new_value
                            subColReference.document(document.id).update(photocardData)
                            if (photocardData["is_otw"] as Boolean) {
                                showToast("La photocard se ha marcado como OTW ")
                            } else {
                                showToast("La photocard ya no está OTW ")
                            }

                        }
                    }

                }
            }
        }
    }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage
    fun showToast(message: String) {
        _toastMessage.postValue(message)
    }
    suspend fun createPhotocard(photocard: Photocard): String {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        var downloadUri = photocard.photocardURL
        //Darle nombre a la photocard
        var randomNum: String = Random.nextInt().toString()
        var photocardNameStorage: String = "Photocard" + randomNum
        Log.d("Nombre de la foto", "Nombre : ${photocardNameStorage}")
        //Referencia a la photocard en Cloud Storage
        val photocardRef = storageRef.child("photocardPics/${photocardNameStorage}")
        val uri = newPhotocardUri.value
        if (uri != null) {
            val uploadTask = photocardRef.putFile(uri)
            uploadTask.addOnFailureListener {
                Log.d("Error al subir la imagen", "Ha habido algun error")
            }.addOnSuccessListener { taskSnapshot ->
                photocardRef.downloadUrl.addOnSuccessListener { uri ->
                    // Sacar la URI del archivo desde el storage
                    _newPhotocardUri.postValue(uri)
                }
            }.addOnFailureListener { exception ->
                Log.d("Error", "Vuelve a intentarlo mas tarde")
            }
        }
        return downloadUri
    }

    fun updatePhotocardValues(photocard: Photocard) {
        Log.d("UPDATE", "Entra en la función ${photocard}")
        Log.d("UBICACION DE LA PHOTOCARD", "${photocard.status}")
        viewModelScope.launch {
            Log.d("Entra en el viewModelScope", "Estoy en el viewModelScope")
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            if (photocard.status == "Wishlist") {
                Log.d("Photocard de Wishlist", "La photocard pertenece a la wishlist")
                val subColReference = userID?.let { getWishlistSubcollectionReference(it) }
                Log.d("REFERENCIA A LA SUBCOL", "${subColReference}")
                if (subColReference != null) {
                    val photocardQuery =
                        subColReference.whereEqualTo("photocard_id", photocard.photocardId).get()
                            .await()

                    val document = photocardQuery.documents.first()
                    if (document != null) {
                        val photocardData = document.data
                        Log.d("PHOTOCARD DATA", "${photocardData}")
                        if (newValue.value!= "") {
                            Log.d("Nuevo valor", "${newValue.value}")
                            photocardData?.set("value", newValue.value.toString())
                        }
                        if (newType.value!="") {
                            Log.d("Nuevo tipo", "${newType.value}")
                            photocardData?.set("type", newType.value.toString())
                        }
                        if (newPhotocardVersion.value!="") {
                            Log.d("Nueva version", "${newPhotocardVersion.value}")
                            photocardData?.set("photocard_version", newPhotocardVersion.value.toString())
                        }
                        if (newPhotocardUri!= Uri.EMPTY) {
                            Log.d("Nueva URI", "${newPhotocardUri.value}")
                            viewModelScope.launch{
                                createPhotocard(photocard)
                                val newPhotocardURL : String = newPhotocardUri.value.toString()
                                photocardData?.set("photocard_url",newPhotocardURL)
                            }


                        }
                        if (photocardData != null) {
                            subColReference.document(document.id).update(photocardData).addOnSuccessListener {
                                showToast("La photocard se ha editado ")
                                getPhotocardsWishlistList()
                            }

                        }
                    }
                }
            }
        }



    }
}



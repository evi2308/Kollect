package com.evasanchez.kollect.ViewModels

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CollectionWishlistViewModel: ViewModel() {
    val db = FirebaseFirestore.getInstance()

    private val _photocardsList = MutableLiveData<List<Photocard>>()
    val photocardsList: LiveData<List<Photocard>> = _photocardsList // LISTA PARA LAS PHOTOCARDS QUE PERTENECEN A LA COLECCION

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

    private val _groupName =MutableLiveData<String>()
    val groupName : LiveData<String> = _groupName


    init {
        Log.d("Init", "Entra en el init de HomeScreen")
        val db = FirebaseFirestore.getInstance()
        //viewModelScope.launch {
          //  getPhotocardsList()  }

    }
    // Funciones para rellenar los dropdown para el filtro?

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

    //Sacar lista de idols relacionadas con el grupo seleccionado
    fun getIdolsBasedOnKgroup(selectedGroup: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReference = userID?.let { getIdolSubColReference(it) }

            if (subColReference != null) {
                try {
                    val idolsQuery = subColReference.whereEqualTo("group_name", selectedGroup).get().await()
                    val idolsNames = mutableListOf<String>()

                    for (document in idolsQuery) {
                        val name = document.getString("idol_name") // Assuming idol_name is the field you want to retrieve
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
    val selectedPhotocard : LiveData<Photocard> = _selectedPhotocard
    var selectedPhotocardDetail by mutableStateOf<Photocard?>(null)
        private set

    //Sacar detalle de la photocard para la pantalla de detalles
    fun addPhotocardDetail(photocardDetailed: Photocard){
        selectedPhotocardDetail = photocardDetailed
    }

    //Funcion para borrar las photocards tanto de la wishlist como de la coleccion, con la LastScreenRoute para saber desde que pantalla se ha llamado al metodo
    fun deletePhotocard (lastScreenRoute: String, photocardDetailed: Photocard){
        viewModelScope.launch(Dispatchers.IO) {
            if(lastScreenRoute == "home_screen"){
                val userID = FirebaseAuth.getInstance().currentUser?.uid
                val subColReference = userID?.let { getColeccionSubcollectionReference(it) }
                if (subColReference != null) {
                    val photocardQuery = subColReference.whereEqualTo("photocard_id", photocardDetailed.photocardId).get().await()
                    if (!photocardQuery.isEmpty) {
                        val photocardId = photocardQuery.documents.first().id
                        subColReference.document(photocardId).delete().addOnSuccessListener {
                            Log.d("Photocard Borrada", "Photocard Borrada existosamente")
                            _showDialog.postValue(true)
                            _dialogText.postValue("Photocard Borrada existosamente")
                        }.addOnFailureListener{
                            Log.d("Error", "Error al borrar la phtotocard")
                            _dialogText.postValue("Algo ha salido mal, inténtalo de nuevo más tarde")
                        }
                    }
                }
            }
            if(lastScreenRoute == "wishlist_screen"){
                val userID = FirebaseAuth.getInstance().currentUser?.uid
                val subColReference = userID?.let { getWishlistSubcollectionReference(it) }
                if (subColReference != null) {
                    val photocardQuery = subColReference.whereEqualTo("photocard_id", photocardDetailed.photocardId).get().await()
                    if (!photocardQuery.isEmpty) {
                        val photocardId = photocardQuery.documents.first().id
                        subColReference.document(photocardId).delete().addOnSuccessListener {
                            Log.d("Photocard Borrada", "Photocard Borrada existosamente")
                            _showDialog.postValue(true)
                            _dialogText.postValue("Photocard Borrada existosamente")
                        }.addOnFailureListener{
                            Log.d("Error", "Error al borrar la phtotocard")
                            _dialogText.postValue("Algo ha salido mal, inténtalo de nuevo más tarde")
                        }
                    }
                }
            }

        }

    }

    fun moveFromColtoWishlist(photocardDetailed: Photocard){
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReferenceColeccion = userID?.let { getColeccionSubcollectionReference(it) }
            val subColReferenceWishlist = userID?.let { getWishlistSubcollectionReference(it) }
            if (subColReferenceColeccion != null && subColReferenceWishlist != null) {
                val photocardQuery = subColReferenceColeccion.whereEqualTo("photocard_id", photocardDetailed.photocardId).get().await()
                if (!photocardQuery.isEmpty) {
                    val photocardData = photocardQuery.documents.first().data
                    val photocardId = photocardQuery.documents.first().id
                    subColReferenceColeccion.document(photocardId).delete().addOnSuccessListener {
                        Log.d("Photocard Eliminada", "Ole se ha eliminado toca moverla")
                        subColReferenceWishlist.add(photocardData!!).addOnSuccessListener {
                            Log.d("Photocard Movida", "Photocard Movida a la Wishlist Exitosamente")
                            _showDialog.postValue(true)
                            _dialogText.postValue("Photocard Movida a la wishlist exitosamente")
                        }
                    }.addOnFailureListener{
                        Log.d("Error", "Error al borrar la phtotocard")
                        _showDialog.postValue(true)
                        _dialogText.postValue("Algo ha salido mal, inténtalo de nuevo más tarde")
                    }


                }
            }

        }
    }
    fun moveFromWishlisttoCol(photocardDetailed: Photocard){
        viewModelScope.launch(Dispatchers.IO) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReferenceColeccion = userID?.let { getColeccionSubcollectionReference(it) }
            val subColReferenceWishlist = userID?.let { getWishlistSubcollectionReference(it) }
            if (subColReferenceColeccion != null && subColReferenceWishlist != null) {
                val photocardQuery = subColReferenceWishlist.whereEqualTo("photocard_id", photocardDetailed.photocardId).get().await()
                if (!photocardQuery.isEmpty) {
                    val photocardData = photocardQuery.documents.first().data
                    val photocardId = photocardQuery.documents.first().id
                    subColReferenceWishlist.document(photocardId).delete().addOnSuccessListener {
                        Log.d("Photocard Eliminada", "Ole se ha eliminado toca moverla")
                        subColReferenceColeccion.add(photocardData!!).addOnSuccessListener {
                            Log.d("Photocard Movida", "Photocard movida a tu coleccion exitosamente")
                            _showDialog.postValue(true)
                            _dialogText.postValue("Photocard movida a tu coleccion exitosamente")
                        }
                    }.addOnFailureListener{
                        Log.d("Error", "Error al borrar la phtotocard")
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

}
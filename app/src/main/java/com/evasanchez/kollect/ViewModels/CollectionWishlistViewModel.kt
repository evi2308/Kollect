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
    private val auth: FirebaseAuth = Firebase.auth

    val userID = auth.currentUser?.uid
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
    init {
        Log.d("A ver", "Entra en el init de HomeScreen")
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        //viewModelScope.launch {
          //  getPhotocardsList()  }

    }

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

    fun getPhotocardsCollectionList() {
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

    fun getPhotocardsWishlistList() {
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

    private val _selectedPhotocard = MutableLiveData<Photocard>()
    val selectedPhotocard : LiveData<Photocard> = _selectedPhotocard
    var selectedPhotocardDetail by mutableStateOf<Photocard?>(null)
        private set

    fun addPhotocardDetail(photocardDetailed: Photocard){
        selectedPhotocardDetail = photocardDetailed
    }

    fun deletePhotocard (lastScreenRoute: String, photocardDetailed: Photocard){
        viewModelScope.launch(Dispatchers.IO) {
            if(lastScreenRoute == "home_screen"){
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
    fun onDismissDialog() {
        _showDialog.value = false
    }

}
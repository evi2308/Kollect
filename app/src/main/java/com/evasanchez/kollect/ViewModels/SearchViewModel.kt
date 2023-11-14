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
import com.evasanchez.kollect.data.Usuario
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel : ViewModel() {
    val db = FirebaseFirestore.getInstance()
    private val _usersList = MutableLiveData<List<Usuario>>()
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _users = MutableStateFlow(listOf<Usuario>())
    val users = searchText.combine(_users){ text, users ->
        if(text.isBlank()){
            users
        }else users.filter{
            it.doesMatchSearchQuery(text)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _users.value
    )

    init{
        viewModelScope.launch {
            Log.d("Init de SearchViewModel", "Init de SearchViewModel")
            getUserList()
        }

    }


    private val _userPhotocardsWishlistList = MutableLiveData<List<Photocard>>()
    val userPhotocardsWishlistList: LiveData<List<Photocard>> = _userPhotocardsWishlistList

    private val _selectedPhotocard = MutableLiveData<Photocard>()
    val selectedPhotocard : LiveData<Photocard> = _selectedPhotocard

    fun getUserList() {
        viewModelScope.launch {
            try{
                val userList = FirebaseFirestore.getInstance().collection("usuario").get().await().toObjects(Usuario::class.java)

                _users.value = userList
            }catch (e : Exception){
                Log.e("Error en la busqueda", "Algo ha malido sal")
            }
        }
    }
    var selectedUserDetail by mutableStateOf<Usuario?>(null)
        private set
    fun selectedUser(userDetail: Usuario){
        selectedUserDetail = userDetail
    }

    suspend fun getWishlistSubcollectionReference(username: String): CollectionReference {

        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("username", username)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Wishlist")
        } else {
            throw NoSuchElementException("User not found")
        }
    }

    fun getUserPhotocardsWishlistList(searchedUserUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val subColReference = searchedUserUsername.let { getWishlistSubcollectionReference(it) }

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

                    _userPhotocardsWishlistList.postValue(photocardObjList)
                } catch (e: Exception) {
                    Log.e("Firestore Query Error", e.message ?: "Unknown error")
                    _userPhotocardsWishlistList.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference is null")
                _userPhotocardsWishlistList.postValue(emptyList())
            }
        }
    }

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }
}


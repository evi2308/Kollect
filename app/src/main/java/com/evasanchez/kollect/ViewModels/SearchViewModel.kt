package com.evasanchez.kollect.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evasanchez.kollect.data.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel : ViewModel() {


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
        getUserList()
    }

    private fun getUserList() {
        viewModelScope.launch {
            try{
                val userList = FirebaseFirestore.getInstance().collection("usuario").get().await().toObjects(Usuario::class.java)

                _users.value = userList
            }catch (e : Exception){
                Log.e("Error en la busqueda", "Algo ha malido sal")
            }
        }
    }

    fun onSearchTextChanged(searchText: String) {
        _searchText.value = searchText
    }
}


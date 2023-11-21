package com.evasanchez.kollect.ViewModels

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evasanchez.kollect.data.Usuario
import com.evasanchez.kollect.uiclasses.LoginScreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterScreenViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnabled = MutableLiveData<Boolean>()
    val loginEnabled: LiveData<Boolean> = _loginEnabled

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    fun onRegisterChanged(email: String, password: String, username: String) {
        _email.value = email
        _password.value = password
        _username.value = username
        viewModelScope.launch {
            val usernameExists = doesUsernameExist(username)

            isValidEmail(email) { isEmailValid ->
                _loginEnabled.value = isEmailValid && isValidPassword(password) && !usernameExists
            }
        }

    }


    private fun isValidPassword(password: String): Boolean = password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\\$%^&+=!?¿¡*])(?=\\S+\$).{8,}\$".toRegex())


    private fun isValidEmail(email: String, onResult: (Boolean) -> Unit) {
        // Consulta para buscar en fireStore usuarios y sus emails. Si el email ya existe, no se habilita el botón
        val emailQuery = FirebaseFirestore.getInstance().collection("usuario")
            .whereEqualTo("email", email)
            .limit(1)  // Solo puede haber 1 user con 1 email

        emailQuery.get()
            .addOnSuccessListener { querySnapshot ->
                // Si se encuentra al menos un documento, significa que ya existe un usuario con ese correo electrónico
                val isEmailValid = querySnapshot.isEmpty
                onResult(isEmailValid)
            }
            .addOnFailureListener { e ->
                //Ante cualquier error, se envia igualmente un false
                Log.d("Error", "Ha ocurrido un error : $e")
                onResult(false)
            }
    }
    suspend fun doesUsernameExist(username: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("usuario")

        val query = usersCollection.whereEqualTo("username", username)
        val querySnapshot = query.get().await()

        return !querySnapshot.isEmpty
    }


    fun createUserEmailPassword(email: String, password: String, loginScreen: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { task ->

                        Log.d("Kollect", "Cuenta creada....")
                        createUser()
                        loginScreen()

                }.addOnFailureListener{
                    Log.d("Error", "Error al crear cuenta")
                    cancel()
                }
            } catch (ex: Exception) {
                Log.d("Kollect", "Error ${ex.message}")
            }
        }

    fun createUser(){
        val currentuserid = auth.currentUser?.uid
        val user = Usuario(
            email = email.value.toString(),
            username = username.value.toString(),
            userId = currentuserid.toString(),
            pfpURL = "https://firebasestorage.googleapis.com/v0/b/k-ollect-2bf8d.appspot.com/o/profilePics%2Fpfp_default.jpg?alt=media&token=5383fb77-e3fa-4e67-bae5-9415344fb777"
        ).userToMap()
        FirebaseFirestore.getInstance().collection("usuario").add(user)
            .addOnSuccessListener {
            Log.d("Usuario añadido a Firebase", "Usuario añadido a Firebase")
        }.addOnFailureListener{
            Log.d("Error", "No se ha añadido el usuario a Firebase")
        }
    }

}
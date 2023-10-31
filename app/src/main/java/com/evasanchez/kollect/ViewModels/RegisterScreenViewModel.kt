package com.evasanchez.kollect.ViewModels

import android.util.Log
import android.util.Patterns
import android.widget.Toast
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
        isValidEmail(email) { isEmailValid ->
            _loginEnabled.value = isEmailValid && isValidPassword(password)
        }

    }


    private fun isValidPassword(password: String): Boolean = password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\\$%^&+=!?¿¡*])(?=\\S+\$).{8,}\$".toRegex())
    //private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

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
            .addOnFailureListener { ex ->
                //Ante cualquier error, se envia igualmente un false
                Log.d("Error", "Ha ocurrido un error : $ex")
                onResult(false)
            }
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
            userId = currentuserid.toString()
        ).userToMap()
        FirebaseFirestore.getInstance().collection("usuario").add(user)
            .addOnSuccessListener {
            Log.d("AÑADIDO", "ESTO HA FUNCADO")
        }.addOnFailureListener{
            Log.d("NO AÑADIDO", "Esto es una mierda")
        }
    }
    private fun addUserNameToDB(){
        val userid = auth.currentUser?.uid
        val userDataRegister = Usuario(
            email = email.value.toString(),
            username = username.value.toString(),
            userId = userid.toString()

        ).userToMap()

        FirebaseFirestore.getInstance().collection("usuario").add(userDataRegister)
            .addOnSuccessListener {
                Log.d("Kollect", "Usuario añadido a la base de datos")
            }
            .addOnFailureListener{
                Log.d("Kollect", "Algo ha salido mal")
            }
    }

}
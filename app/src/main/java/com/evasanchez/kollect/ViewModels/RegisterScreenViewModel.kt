package com.evasanchez.kollect.ViewModels

import android.util.Log
import android.util.Patterns
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
import kotlinx.coroutines.launch

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
        _loginEnabled.value = isValidEmail(email) && isValidPassword(password)

    }


    private fun isValidPassword(password: String): Boolean = password.length > 8
    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun createUserEmailPassword(email: String, password: String, loginScreen: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Kollect", "Cuenta creada e Iniciando sesion correctamente....")
                        addUserNameToDB()
                        loginScreen()
                    } else {
                        Log.d("Kollect", "${task.result}")
                    }
                }
            } catch (ex: Exception) {
                Log.d("Kollect", "Error ${ex.message}")
            }
        }

    fun addUserNameToDB(){
        val userDataRegister = Usuario(
            email = email.value.toString(),
            username = username.value.toString()

        ).userToMap()

        FirebaseFirestore.getInstance().collection("usuario").add(userDataRegister)
            .addOnSuccessListener {
                Log.d("Kollect", "Usuario creado correctamente")
            }
            .addOnFailureListener{
                Log.d("Kollect", "Algo ha salido mal")
            }
    }

}
package com.evasanchez.kollect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.evasanchez.kollect.ViewModels.LoginScreenViewModel
import com.evasanchez.kollect.navigation.AppNavigation
import com.evasanchez.kollect.navigation.AppScreens
import com.evasanchez.kollect.ui.theme.KollectTheme
import com.evasanchez.kollect.uiclasses.LoginPreview
import com.evasanchez.kollect.uiclasses.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth
    var startDestination = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KollectTheme {
                //Con esto, si el usuario tiene su sesión iniciada, no podrá echar para atrás hacia el login y además la app se le abrirá desde la página principal
                val user = auth.currentUser
                if (user != null) {
                    startDestination = AppScreens.HomeScreen.route
                }
                if (user == null){
                    startDestination = AppScreens.LoginScreen.route

                }
                AppNavigation(startDestination)
            }
            }
        }
    }




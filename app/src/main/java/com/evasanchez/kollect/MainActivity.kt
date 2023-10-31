package com.evasanchez.kollect

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.evasanchez.kollect.ViewModels.LoginScreenViewModel
import com.evasanchez.kollect.ViewModels.PhotocardFormViewModel
import com.evasanchez.kollect.ViewModels.ProfileScreenViewModel
import com.evasanchez.kollect.ViewModels.RegisterScreenViewModel
import com.evasanchez.kollect.navigation.AppNavigation
import com.evasanchez.kollect.navigation.AppScreens
import com.evasanchez.kollect.ui.theme.KollectTheme
import com.evasanchez.kollect.uiclasses.AddKGroup
import com.evasanchez.kollect.uiclasses.BottomNavigationBar
import com.evasanchez.kollect.uiclasses.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.evasanchez.kollect.navigation.AppNavigation
import com.evasanchez.kollect.uiclasses.HomeScreen
import com.evasanchez.kollect.uiclasses.MyProfileScreen
import com.evasanchez.kollect.uiclasses.PhotocardForm
import com.evasanchez.kollect.uiclasses.RegisterScreen
import com.evasanchez.kollect.uiclasses.collectionScreen
import com.google.firebase.FirebaseApp

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth
    var startDestination = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            KollectTheme {
                val navController = rememberNavController()
                //Con esto, si el usuario tiene su sesión iniciada, no podrá echar para atrás hacia el login y además la app se le abrirá desde la página principal

                if (auth.getCurrentUser() != null){
                    Log.d("Autenticacion", "El usuario es: ${auth.currentUser?.email}")
                    startDestination = AppScreens.HomeScreen.route
                }else{
                    Log.d("Autenticacion", "No hay usuario")
                    startDestination = AppScreens.LoginScreen.route
                }

                var showBottomBar by rememberSaveable { mutableStateOf(true) }
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                showBottomBar = when (navBackStackEntry?.destination?.route) {
                    "login_screen" -> false // on this screen bottom bar should be hidden
                    "register_screen" -> false // here too
                    else -> true // in all other cases show bottom bar
                }
                Scaffold(
                    content = { paddingValues ->
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                        ) {
                            AppNavigation(startDestination,navController)
                        }
                    },

                    bottomBar = { if (showBottomBar) BottomNavigationBar(navController = navController) }
                )
            }
        }
    }

    suspend fun initApp(){
        val user = auth.currentUser
        if (user != null) {
            startDestination = AppScreens.HomeScreen.route
        }
        if (user == null){
            startDestination = AppScreens.LoginScreen.route

        }

    }
}
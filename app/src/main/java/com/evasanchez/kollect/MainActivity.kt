package com.evasanchez.kollect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.evasanchez.kollect.ViewModels.CollectionWishlistViewModel
import com.evasanchez.kollect.navigation.AppNavigation
import com.evasanchez.kollect.navigation.AppScreens
import com.evasanchez.kollect.ui.theme.KollectTheme
import com.evasanchez.kollect.uiclasses.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.FirebaseApp

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    val viewModel: CollectionWishlistViewModel by viewModels()
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
                    viewModel.clearData()
                    startDestination = AppScreens.LoginScreen.route
                }
                var showFAB by rememberSaveable {mutableStateOf(true) }
                var showBottomBar by rememberSaveable { mutableStateOf(true) }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                showFAB = when (navBackStackEntry?.destination?.route){
                    "home_screen" -> true
                    "wishlist_screen" -> true
                    else -> false
                }
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
                            AppNavigation(startDestination,navController, viewModel)
                        }
                    },
                    floatingActionButton = {if(showFAB) FloatingActionButton(
                        onClick = {
                            navController.navigate(AppScreens.PhotocardForm.route)
                        },


                        ) {

                        Icon(Icons.Filled.Add, "Añadir photocard")
                    }},
                    bottomBar = { if (showBottomBar) BottomNavigationBar(navController = navController) }

                )
            }
        }
    }


    }


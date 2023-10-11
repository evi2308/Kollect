package com.evasanchez.kollect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evasanchez.kollect.ViewModels.LoginScreenViewModel
import com.evasanchez.kollect.uiclasses.HomeScreen
import com.evasanchez.kollect.uiclasses.LoginScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route ){
        composable(route = AppScreens.LoginScreen.route){
            LoginScreen(navController,viewModel = LoginScreenViewModel())
        }
        composable(route = AppScreens.HomeScreen.route){
            HomeScreen(navController)
        }
    }
}
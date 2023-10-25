package com.evasanchez.kollect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evasanchez.kollect.ViewModels.LoginScreenViewModel
import com.evasanchez.kollect.ViewModels.PhotocardFormViewModel
import com.evasanchez.kollect.ViewModels.RegisterScreenViewModel
import com.evasanchez.kollect.uiclasses.HomeScreen
import com.evasanchez.kollect.uiclasses.LoginScreen
import com.evasanchez.kollect.uiclasses.PhotocardForm
import com.evasanchez.kollect.uiclasses.RegisterScreen

@Composable
fun AppNavigation(startDestination : String){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination ){
        composable(route = AppScreens.LoginScreen.route){
            LoginScreen(navController,viewModel = LoginScreenViewModel())
        }
        composable(route = AppScreens.HomeScreen.route){
            HomeScreen(navController)
        }
        composable(route = AppScreens.RegisterScreen.route){
            RegisterScreen(navController, viewModel = RegisterScreenViewModel())
        }
        composable(route = AppScreens.PhotocardForm.route){
            PhotocardForm(navController, viewModel = PhotocardFormViewModel())
        }
    }
}
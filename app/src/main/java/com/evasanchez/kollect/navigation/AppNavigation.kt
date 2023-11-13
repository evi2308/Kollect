package com.evasanchez.kollect.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.evasanchez.kollect.ViewModels.CollectionWishlistViewModel
import com.evasanchez.kollect.ViewModels.HomeScreenViewModel
import com.evasanchez.kollect.ViewModels.LoginScreenViewModel
import com.evasanchez.kollect.ViewModels.PhotocardDetailViewModel
import com.evasanchez.kollect.ViewModels.PhotocardFormViewModel
import com.evasanchez.kollect.ViewModels.ProfileScreenViewModel
import com.evasanchez.kollect.ViewModels.RegisterScreenViewModel
import com.evasanchez.kollect.ViewModels.SearchViewModel
import com.evasanchez.kollect.ViewModels.WishlistScreenViewModel
import com.evasanchez.kollect.data.Photocard
import com.evasanchez.kollect.uiclasses.HomeScreen
import com.evasanchez.kollect.uiclasses.LoginScreen
import com.evasanchez.kollect.uiclasses.MyProfileScreen
import com.evasanchez.kollect.uiclasses.PhotocardDetail
import com.evasanchez.kollect.uiclasses.PhotocardForm
import com.evasanchez.kollect.uiclasses.RegisterScreen
import com.evasanchez.kollect.uiclasses.SearchScreen
import com.evasanchez.kollect.uiclasses.WishlistScreen

@Composable
fun AppNavigation(startDestination : String, navController: NavHostController){

    val sharedViewModel: CollectionWishlistViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination ){
        composable(route = AppScreens.LoginScreen.route){
            LoginScreen(navController,viewModel = LoginScreenViewModel())
        }
        composable(route = AppScreens.HomeScreen.route){
            HomeScreen(navController, sharedViewModel)
        }
        composable(route = AppScreens.RegisterScreen.route){
            RegisterScreen(navController, viewModel = RegisterScreenViewModel())
        }
        composable(route = AppScreens.PhotocardForm.route){
            PhotocardForm(navController, viewModel = PhotocardFormViewModel())
        }
        composable(route = AppScreens.ProfileScreen.route){
            MyProfileScreen(navController, viewModel = ProfileScreenViewModel())
        }

        composable(route = AppScreens.PhotocardDetail.route){
            PhotocardDetail(navController = navController, sharedViewModel)
        }

        composable(route = AppScreens.WishlistScreen.route){
            WishlistScreen(navController, viewModel = sharedViewModel)
        }

        composable(route = AppScreens.SearchScreen.route){
            SearchScreen(navController, viewModel = SearchViewModel())
        }

    }
}
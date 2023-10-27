package com.evasanchez.kollect.navigation

sealed class AppScreens(val route: String){
    object LoginScreen: AppScreens("login_screen")
    object HomeScreen: AppScreens("home_screen")

    object RegisterScreen: AppScreens("register_screen")

    object PhotocardForm: AppScreens("photocard_form")

    object ProfileScreen: AppScreens("profile_screen")

}

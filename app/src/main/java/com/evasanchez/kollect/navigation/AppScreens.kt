package com.evasanchez.kollect.navigation

sealed class AppScreens(val route: String){
    object LoginScreen: AppScreens("login_screen")
    object HomeScreen: AppScreens("home_screen")

    object RegisterScreen: AppScreens("register_screen")

    object PhotocardForm: AppScreens("photocard_form")

    object ProfileScreen: AppScreens("profile_screen")
    object PhotocardDetail: AppScreens("photocard_details/{photocard_id}")

    object WishlistScreen: AppScreens("wishlist_screen")

    object SearchScreen : AppScreens("search_screen")

    object WishlistSearchScreen : AppScreens("wishlist_search_screen/{username}")

}

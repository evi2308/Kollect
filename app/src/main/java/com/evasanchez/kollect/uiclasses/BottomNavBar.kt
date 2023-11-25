package com.evasanchez.kollect.uiclasses

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController


data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val navRoute : String

)
@Composable
fun BottomNavigationBar(navController : NavController) {
    var selectedItemIndex by rememberSaveable{
        mutableIntStateOf(0)
    }
    val items = listOf(
        BottomNavigationItem(
            title = "Coleccion",
            selectedIcon =  Icons.Filled.Home,
            unselectedIcon =  Icons.Outlined.Home,
            navRoute = "home_screen"
        ),
        BottomNavigationItem(
            title = "Wishlist",
            selectedIcon =  Icons.Filled.Favorite,
            unselectedIcon =  Icons.Outlined.FavoriteBorder,
            navRoute = "wishlist_screen"
        ),
        BottomNavigationItem(
            title = "Buscar",
            selectedIcon =  Icons.Filled.Search,
            unselectedIcon =  Icons.Outlined.Search,
            navRoute = "search_screen"
        ),
        BottomNavigationItem(
            title = "Perfil",
            selectedIcon =  Icons.Filled.AccountCircle,
            unselectedIcon =  Icons.Outlined.AccountCircle,
            navRoute = "profile_screen"
        )
    )
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navController.navigate(item.navRoute)
                },
                label = { Text(item.title)},
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) {
                            item.selectedIcon
                        }else item.unselectedIcon,
                        contentDescription = "Nav Icon"
                    )
                })
        }
    }
}
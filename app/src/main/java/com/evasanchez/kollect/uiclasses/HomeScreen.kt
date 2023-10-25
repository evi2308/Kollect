package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.evasanchez.kollect.navigation.AppScreens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(AppScreens.PhotocardForm.route)
            }) {
                Icon(Icons.Filled.Add, "Añadir photocard")
            }
        },
        bottomBar = {
            BottomAppBar { /* Bottom app bar content */ }
        },
        // Defaults to FabPosition.End
        floatingActionButtonPosition = FabPosition.End
    ) {
        collectionScreen()
    }
}
@Composable
fun collectionScreen() {
    Text(text = "PANTALLA PRINCIPAL")
}

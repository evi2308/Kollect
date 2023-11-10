package com.evasanchez.kollect.uiclasses

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.ViewModels.HomeScreenViewModel
import com.evasanchez.kollect.ViewModels.PhotocardDetailViewModel
import com.evasanchez.kollect.navigation.AppScreens

@Composable
fun PhotocardDetail(navController: NavController, viewModel: HomeScreenViewModel) {

    val selectedPhotocard = viewModel.selectedPhotocardDetail

    LaunchedEffect(selectedPhotocard) {
        if (selectedPhotocard != null ){
            Log.d("LaunchedEffect PhotocardDetail", "${selectedPhotocard.photocardId}")
        }

    }


    Text(text = "DETALLES")

    Column {
        // Only display details when selectedPhotocard is not null
        selectedPhotocard?.let { photocard ->
            Text("Album Name: ${photocard.albumName}")
            AsyncImage(model = photocard.photocardURL, contentDescription = "Photocard" )
            // Other details...
        }
    }
}
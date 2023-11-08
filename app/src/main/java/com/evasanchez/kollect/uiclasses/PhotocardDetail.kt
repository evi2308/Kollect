package com.evasanchez.kollect.uiclasses

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.evasanchez.kollect.ViewModels.PhotocardDetailViewModel
import com.evasanchez.kollect.data.Photocard

@Composable
fun PhotocardDetail(navController: NavController, viewModel: PhotocardDetailViewModel, photocard: Photocard){
    val viewModel = PhotocardDetailViewModel()
    Text(text= photocard.photocardId)
    LaunchedEffect(viewModel) {
        viewModel.getPhotocardDetails(photocard.photocardId)
    }
}
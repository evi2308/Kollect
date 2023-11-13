package com.evasanchez.kollect.uiclasses

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.CollectionWishlistViewModel

@Composable
fun PhotocardDetail(navController: NavController, viewModel: CollectionWishlistViewModel) {
    // Observa si se debe mostrar el AlertDialog
    val showErrorDialog = viewModel.showDialog.observeAsState(false)
    val dialogText = viewModel.dialogText.observeAsState("").value
    if (showErrorDialog.value) {
        AlertDialogPhotocardDetail(navController,viewModel,dialogText)
    }
    val selectedPhotocard = viewModel.selectedPhotocardDetail

    LaunchedEffect(selectedPhotocard) {
        if (selectedPhotocard != null ){
            Log.d("LaunchedEffect PhotocardDetail", "${selectedPhotocard.photocardId}")
        }

    }


    Text(text = "DETALLES")
    Box(modifier = Modifier.fillMaxWidth()) {}
    Column() {
        selectedPhotocard?.let { photocard ->
            AsyncImage(model = photocard.photocardURL, contentDescription = "Photocard" )
            // Other details...
        }
    }
    Column {
        Text(text = "Titulo: ${selectedPhotocard?.albumName}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.deletePhotocard(selectedPhotocard!!)
        }) {
            Text(text = "ELIMINAR PHOTOCARD")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.moveFromColtoWishlist(selectedPhotocard!!)
        }) {
            Text(text = "MOVER PHOTOCARD")
        }
    }
    

}


@Composable
fun AlertDialogPhotocardDetail(
    navController: NavController,
    viewModel: CollectionWishlistViewModel,
    dialogText: String
){

    AlertDialog(
        onDismissRequest = {viewModel.onDismissDialog()},
        confirmButton = {
            TextButton(onClick = {
                viewModel.onDismissDialog()
                navController.popBackStack() }) {
                Text(text = stringResource(id = R.string.ok_message))
            }
        },
        title= { Text(text = "PhotocardDetail", color = MaterialTheme.colorScheme.onPrimaryContainer)},
        text = {Text(text = dialogText, color = MaterialTheme.colorScheme.onPrimaryContainer)}
    )

}






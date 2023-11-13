package com.evasanchez.kollect.uiclasses

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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
    val lastScreen = navController.backQueue.elementAtOrNull(navController.backQueue.size - 2)?.destination?.route
    LaunchedEffect(selectedPhotocard) {
        if (selectedPhotocard != null ){
            Log.d("LaunchedEffect PhotocardDetail", "${selectedPhotocard.photocardId}")
        }

    }
    Column{
        Row(verticalAlignment = Alignment.CenterVertically){
            AsyncImage(model = selectedPhotocard?.photocardURL, contentDescription = "Photocard seleccionada")
            Column {
                selectedPhotocard?.albumName?.let { Text(it) }
                selectedPhotocard?.groupName?.let { Text(it) }
                selectedPhotocard?.idolName?.let { Text(it) }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        Row() {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (lastScreen != null) {
                    viewModel.deletePhotocard(lastScreen, selectedPhotocard!!)
                }
            }) {
                Text(text = "ELIMINAR PHOTOCARD")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.moveFromColtoWishlist(selectedPhotocard!!)
            }, enabled = isLastScreenCollecion(lastScreen)) {
                Text(text = "MOVER PHOTOCARD A WISHLIST")
            }
        }
    }
}

fun isLastScreenCollecion(lastScreen: String?): Boolean {
    Log.d("BOTON", "ULTIMA PANTALLA: $lastScreen")
    return lastScreen == "home_screen"
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






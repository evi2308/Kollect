package com.evasanchez.kollect.uiclasses

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.CollectionWishlistViewModel

@Composable
fun PhotocardDetail(navController: NavController, viewModel: CollectionWishlistViewModel) {
    var editMode = viewModel.editMode.observeAsState(false)
    val toastMessage: String? by viewModel.toastMessage.observeAsState()
    // Observa si se debe mostrar el AlertDialog
    val showErrorDialog = viewModel.showDialog.observeAsState(false)
    val dialogText = viewModel.dialogText.observeAsState("").value
    if (showErrorDialog.value) {
        AlertDialogPhotocardDetail(navController,viewModel,dialogText)
    }
    val selectedPhotocard = viewModel.selectedPhotocardDetail
    val lastScreen = navController.backQueue.elementAtOrNull(navController.backQueue.size - 2)?.destination?.route
    LaunchedEffect(selectedPhotocard) {
        Log.d("LauchedEffect PhotocardDetail", "${selectedPhotocard?.photocardId}")
        if (selectedPhotocard != null ){
            Log.d("LaunchedEffect PhotocardDetail", "${selectedPhotocard.photocardId}")
        }


    }
    if(editMode.value == false){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AsyncImage(
                    model = selectedPhotocard?.photocardURL,
                    contentDescription = "Photocard seleccionada",
                    modifier = Modifier
                        .size(500.dp)
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    if (selectedPhotocard != null) {
                        viewModel.updatePrioStatus(selectedPhotocard)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.estrella),
                        contentDescription = "Prio",
                        tint = Color.Unspecified
                    )
                }

                IconButton(onClick = {
                    if (selectedPhotocard != null) {
                        viewModel.updateOTWStatus(selectedPhotocard) }}) {
                    Icon(
                        painter = painterResource(id = R.drawable.carta),
                        contentDescription = "OTW",
                        tint = Color.Unspecified
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "DATOS DE LA PHOTOCARD",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(text = "GRUPO: ${selectedPhotocard?.groupName}")
                    Text(text = "IDOL: ${selectedPhotocard?.idolName}")
                    Text(text = "ALBUM: ${selectedPhotocard?.albumName}")
                    Text(text = "TIPO: ${selectedPhotocard?.type}")
                    Text(text = "VERSION: ${selectedPhotocard?.photocardVersion}")
                    Text(text = "PRECIO: ${selectedPhotocard?.value}")
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedButton(onClick = { viewModel.onEditModeChanged(true) }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "ACTIVAR MODO EDICIÓN")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ElevatedButton(
                    onClick = {
                        if (lastScreen != null) {
                            viewModel.deletePhotocard(lastScreen, selectedPhotocard!!)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "BORRAR")
                }
                if (isLastScreenCollecion(lastScreen)){
                    Button(
                        onClick = {
                            viewModel.moveFromColtoWishlist(selectedPhotocard!!)
                        },
                        enabled = isLastScreenCollecion(lastScreen)
                    ) {
                        Text(text = "MOVER A WISHLIST")
                    }
                }
                if (!isLastScreenCollecion(lastScreen)){
                    Button(
                        onClick = {
                            viewModel.moveFromWishlisttoCol(selectedPhotocard!!)
                        },
                        enabled = !isLastScreenCollecion(lastScreen)
                    ) {
                        Text(text = "MOVER A COLECCION")
                    }
                }
            }


        }
    }
    if (editMode.value == true){
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)){
            Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                ElevatedButton(onClick = { viewModel.onEditModeChanged(false)
                    navController.navigate("home_screen") }) {
                    Text(text = "Cancelar")
                }
                ElevatedButton(onClick = { viewModel.onEditModeChanged(false)
                navController.navigate("home_screen")}) {
                    Text(text = "Aplicar cambios")
                }
            }
        }
    }

    toastMessage?.let { showDetailToast(message = it) }
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

@Composable
fun showDetailToast(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_LONG).show()
}






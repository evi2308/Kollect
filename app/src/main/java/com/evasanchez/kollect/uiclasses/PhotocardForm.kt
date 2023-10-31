package com.evasanchez.kollect.uiclasses

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.evasanchez.kollect.ViewModels.PhotocardFormViewModel

@Composable
fun PhotocardForm(navController: NavController, viewModel: PhotocardFormViewModel){
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        photocards(Modifier.align(Alignment.Center), viewModel, navController)
    }
}
@Composable
fun photocards(modifier: Modifier, viewModel: PhotocardFormViewModel, navController: NavController){
    AlbumNameTextField(modifier)
    SelectPhotocardImage(modifier)
}

@Composable
fun SelectPhotocardImage(modifier : Modifier) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null)}
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        selectedImageUri = uri
    }
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround) {
        Button(onClick = {
            launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(text = "Elegir foto")
        }
        Button(onClick = { 
            
        }) {
            Text(text = "Sacar foto")
        }
        
        
        
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumNameTextField(modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "¿A qué álbum pertenece ésta photocard?",
            textAlign = TextAlign.Left,
            color = Color(0xFF7D5260),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = "albumName",
            onValueChange = {  },
            placeholder = { Text(text = "album") },
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            maxLines = 1,
            label = { Text(text= "username") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color(0xFFFFFFFF),
                placeholderColor = Color(0xFFFFFFFF),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(15.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValueTextField(modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "¿Cuál es el valor de la photocard?",
            textAlign = TextAlign.Left,
            color = Color(0xFF7D5260),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = "value",
            onValueChange = { },
            placeholder = { Text(text = "value") },
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            maxLines = 1,
            label = { Text(text = "username") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color(0xFFFFFFFF),
                placeholderColor = Color(0xFFFFFFFF),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(15.dp)
        )
    }
}
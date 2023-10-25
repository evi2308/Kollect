package com.evasanchez.kollect.uiclasses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    AlbumName(modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumName(modifier: Modifier) {
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
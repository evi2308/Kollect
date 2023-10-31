package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.google.android.gms.auth.api.phone.SmsCodeAutofillClient.PermissionState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotocardForm(navController: NavController, viewModel: PhotocardFormViewModel){
    val idolsList : List<String> by viewModel.allIdols.observeAsState(initial = listOf())
    val kGroups : List<String> by viewModel.allGroups.observeAsState(initial = listOf())
    var selectedKGroup by remember { mutableStateOf(if (kGroups.isNotEmpty()) kGroups[0] else "") }
    var selectedIdol by remember { mutableStateOf(if (idolsList.isNotEmpty()) idolsList[0] else "") }
    LaunchedEffect(viewModel) {
        viewModel.getKGroupListRepository()
        viewModel.getIdolsBasedOnKgroup(selectedKGroup)
    }
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(Modifier.align(Alignment.Center)) {
            AlbumNameTextField()
            SelectPhotocardImage()

            KgroupExposedDropdownMenuBoxRelatedToIdol(kGroups){ selectedText ->
                selectedKGroup = selectedText
                viewModel.getIdolsBasedOnKgroup(selectedText)
            }
            IdolExposedDropdownMenuBox(idolsList){
                    selectedText ->
                    selectedIdol = selectedText
            }
            }
            ValueTextField()
        }

    }

@Composable
fun SelectPhotocardImage() {
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
            
        },
            enabled = false) {
            Text(text = "Sacar foto")

        }
        AsyncImage(model = selectedImageUri, contentDescription = null, Modifier.size(100.dp))
        
        
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumNameTextField() {
    Column() {
        Text(
            text = "¿A qué álbum pertenece ésta photocard?",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.primary,
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
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValueTextField() {
    Column() {
        Text(
            text = "¿Cuál es el valor de la photocard?",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = "value",
            onValueChange = { },
            placeholder = { Text(text = "value") },
            textStyle = TextStyle(MaterialTheme.colorScheme.onSecondaryContainer),
            singleLine = true,
            maxLines = 1,
            label = { Text(text = "username") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color(0xFFFFFFFF),
                placeholderColor = Color(0xFFFFFFFF),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KgroupExposedDropdownMenuBoxRelatedToIdol(kGroups: List<String>, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember {  mutableStateOf(if (kGroups.isNotEmpty()) kGroups[0] else "") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                label= { Text(text = "Selecciona un grupo")},
                placeholder = {Text(text = "Selecciona un grupo")},
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                kGroups.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
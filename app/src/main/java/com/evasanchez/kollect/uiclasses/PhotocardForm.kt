package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.PhotocardFormViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotocardForm(navController: NavController, viewModel: PhotocardFormViewModel){
    //Variables de los distintos campos, asociados al ViewModel
    val idolsList : List<String> by viewModel.allIdols.observeAsState(initial = listOf())
    val kGroups : List<String> by viewModel.allGroups.observeAsState(initial = listOf())
    var selectedKGroup by remember { mutableStateOf(if (kGroups.isNotEmpty()) kGroups[0] else "") }
    var selectedIdol by remember { mutableStateOf(if (idolsList.isNotEmpty()) idolsList[0] else "") }
    val scrollState = rememberScrollState()
    val albumName: String by viewModel.albumName.observeAsState(initial = "")
    val value: String by viewModel.value.observeAsState(initial = "")
    val type: String by viewModel.type.observeAsState(initial = "")
    val photocardVersion by viewModel.photocardVersion.observeAsState(initial = "")
    val isPrio by viewModel.isPrio.observeAsState(initial = false)
    val isOtw by viewModel.isOtw.observeAsState(initial = false)

    // Observa si se debe mostrar el AlertDialog
    val showErrorDialog = viewModel.showDialog.observeAsState(false).value

    if (showErrorDialog) {
        AlertDialogPhotocardForm(navController,viewModel)
    }
    //En cuanto se abre la pantalla, se rellenan los componentes para seleccionar idol y grupo
    LaunchedEffect(viewModel) {
        viewModel.getKGroupListRepository()
        viewModel.getIdolsBasedOnKgroup(selectedKGroup)
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .verticalScroll(scrollState)) {
            AlbumNameTextField(albumName,{viewModel.onFormTextFieldChange(it,value, type, photocardVersion )} )
            Spacer(modifier = Modifier.size(8.dp))
            ValueTextField(value,{viewModel.onFormTextFieldChange(albumName,it, type, photocardVersion )} )
            Spacer(modifier = Modifier.size(8.dp))
            SelectPhotocardImage(viewModel)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Selecciona el grupo al que pertenece el idol", textAlign = TextAlign.Left, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
            KgroupExposedDropdownMenuBoxRelatedToIdol(kGroups){ selectedText ->
                selectedKGroup = selectedText
                viewModel.getIdolsBasedOnKgroup(selectedText)
                viewModel.onGroupSelected(selectedKGroup)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Selecciona el idol al que pertenece esta photocard", textAlign = TextAlign.Left, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
            IdolExposedDropdownMenuBox(idolsList){ selectedText ->
                    selectedIdol = selectedText
                    viewModel.onIdolSelected(selectedIdol)
            }
            ColOrWlRadioButton(viewModel){ selectedStatus ->
                viewModel.onStatusChanged(selectedStatus)
            }
            PhotocardVersionTextField(photocardVersion,{viewModel.onFormTextFieldChange(albumName,value, type, it )} )
            PhotocardTypeTextField(type,{viewModel.onFormTextFieldChange(albumName,value, it, photocardVersion )})
            PrioOtwCeckBoxes(isPrio, isOtw, { viewModel.isPrioChanged(it)} ) {viewModel.isOtwChanged(it) }
            Spacer(modifier = Modifier.size(8.dp))
            ConfirmFormButton(navController){
                viewModel.createPhotocard()
            }
            }


        }

    }

@Composable
fun PrioOtwCeckBoxes(isPrio: Boolean, isOtw: Boolean, isPrioChanged: (Boolean) -> Unit, isOtwChanged: (Boolean) ->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Checkbox for isPrio
        Checkbox(
            checked = isPrio,
            onCheckedChange = { isChecked ->
                isPrioChanged(isChecked)
            },
            modifier = Modifier.weight(1f)
        )
        Text(text = "Prio")

        // Checkbox for isOtw
        Checkbox(
            checked = isOtw,
            onCheckedChange = { isChecked ->
                isOtwChanged(isChecked)
            },
            modifier = Modifier.weight(1f)
        )
        Text(text = "OTW")
    }
}


@Composable
fun ConfirmFormButton(navController: NavController,createPhotocard: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        ElevatedButton(
            onClick = {
                Log.d("Hola", "El boton de confirmar hace click")
                createPhotocard()
            },
            modifier = Modifier
                .height(48.dp),
        ) {
            Text(text = "Confirmar")

        }
        ElevatedButton(
            onClick = {
            Log.d("Hola", "El boton de cancelar hace click")
                navController.popBackStack()
        },
            modifier = Modifier.height(48.dp)) {
            Text(text = "Cancelar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotocardTypeTextField(type: String, onFormTextFieldChange: (String) -> Unit) {
    Column() {
        Text(
            text = "¿Qué tipo de photocard es?",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        OutlinedTextField(
            value = type,
            onValueChange = { onFormTextFieldChange(it) },
            placeholder = { Text(text = "Album, POB, Merch, Concierto...") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            maxLines = 1,
            label = { Text("Album, POB, Merch, Concierto...") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color(0xFFFFFFFF),
                placeholderColor = Color(0xFFFFFFFF),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(15.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotocardVersionTextField(photocardVersion: String, onFormTextFieldChange: (String) -> Unit) {
    Column() {
        Text(
            text = "¿A qué álbum pertenece ésta photocard?",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        OutlinedTextField(
            value = photocardVersion,
            onValueChange = { onFormTextFieldChange(it) },
            placeholder = { Text(text = "Versión de la photocard") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            maxLines = 1,
            label = { Text("Versión de la photocard") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color(0xFFFFFFFF),
                placeholderColor = Color(0xFFFFFFFF),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,

                //focusedIndicatorColor = Color.Transparent,
                //unfocusedIndicatorColor = Color.Transparent,
                //disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(15.dp),
        )
    }
}

@Composable
fun SelectPhotocardImage(viewModel: PhotocardFormViewModel) {
    val selectedImageUri by viewModel.photocardUri.observeAsState(initial = "https://firebasestorage.googleapis.com/v0/b/k-ollect-2bf8d.appspot.com/o/photocardPics%2Fphotocard_default.jpg?alt=media&token=51ca32dd-e06f-4d93-a73f-eea866e0a5f9")
    Text(
        text = "Elige la foto para esta photocard",
        textAlign = TextAlign.Left,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
    AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier
        .size(200.dp)
        .border(2.dp, MaterialTheme.colorScheme.secondary))

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModel.onPhotocardUriChanged(uri)
    }
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround) {
        ElevatedButton(onClick = {
            launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(text = "Elegir foto")
        }
        ElevatedButton(onClick = {
            
        },
            enabled = false) {
            Text(text = "Sacar foto")

        }

        
        
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumNameTextField(albumName: String, onFormTextFieldChange: (String) -> Unit) {
    Column() {
        Text(
            text = "¿A qué álbum pertenece ésta photocard?",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        OutlinedTextField(
            value = albumName,
            onValueChange = { onFormTextFieldChange(it) },
            placeholder = { Text(text = "Nombre del álbum") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            maxLines = 1,
            label = { Text("Nombre del Album") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color(0xFFFFFFFF),
                placeholderColor = Color(0xFFFFFFFF),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,

                //focusedIndicatorColor = Color.Transparent,
                //unfocusedIndicatorColor = Color.Transparent,
                //disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(15.dp),
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValueTextField(value: String, onFormTextFieldChange: (String) -> Unit) {
    Column() {
        Text(
            text = "¿Cuál es el valor de la photocard?",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { onFormTextFieldChange(it)},
            placeholder = { Text(text = "value") },
            textStyle = TextStyle(MaterialTheme.colorScheme.onSecondaryContainer),
            singleLine = true,
            maxLines = 1,
            label = { Text(text = "Introduce el valor sin ningún simbolo") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color(0xFFFFFFFF),
                placeholderColor = Color(0xFFFFFFFF),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
@Composable
fun ColOrWlRadioButton(viewModel: PhotocardFormViewModel, onStatusSelected: (String) -> Unit) {
    Text(
        text = "¿Dónde quieres añadir la photocard?",
        textAlign = TextAlign.Left,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
    val status: String by viewModel.status.observeAsState(initial = "Wishlist")

    Spacer(modifier = Modifier.size(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            RadioButton(selected = status == "Wishlist",
                onClick = {onStatusSelected("Wishlist")})
            Text(text = "Wishlist")
            RadioButton(selected = status == "Coleccion",
                onClick = {onStatusSelected("Coleccion")})
            Text(text = "Coleccion")

        }

}

@Composable
fun AlertDialogPhotocardForm(
    navController: NavController,
    viewModel: PhotocardFormViewModel
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
        title= { Text(text = "Se ha creado la photocard correctamente", color = MaterialTheme.colorScheme.onPrimaryContainer)},
        text = {Text(text = ":) Photocard creada", color = MaterialTheme.colorScheme.onPrimaryContainer)}
    )

}


@file:OptIn(ExperimentalMaterial3Api::class)

package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.ProfileScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyProfileScreen(navController: NavHostController, viewModel : ProfileScreenViewModel) {

    val kGroup: String by viewModel.kGroup.observeAsState(initial = "")
    val idol: String by viewModel.idol.observeAsState(initial = "")
    val kGroups : List<String> by viewModel.allGroups.observeAsState(initial = listOf())
    var selectedKGroup by remember { mutableStateOf(if (kGroups.isNotEmpty()) kGroups[0] else "") }
    val successMessage: String? by viewModel.successMessage.observeAsState()
    val profilePicture: String by viewModel.profilePicture.observeAsState(initial = "") // Esto deberia de poder quitarlo
    val username: String by viewModel.username.observeAsState(initial = "")
    val usernameDef = username
    val showErrorDialog = viewModel.showDialog.observeAsState(false)
    val dialogText = viewModel.dialogText.observeAsState("").value
    if (showErrorDialog.value) {
        AlertDialogProfileScreen(navController, viewModel ,dialogText)
    }
    val context = LocalContext.current as Activity
    LaunchedEffect(viewModel) {
        viewModel.getKGroupListRepository()
        viewModel.getProfilePicture()
        viewModel.getUsername()
    }
    val scrollState = rememberScrollState()
    var expandedDropDown by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MI PERFIL",
                        style = TextStyle(
                            MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.logout(context)
                        navController.navigate("login_screen")}) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)){
            Spacer(modifier = Modifier.height(56.dp))
            ProfileImage(profilePicture,modifier = Modifier
                .height(150.dp)
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape
                ),
                viewModel
            )
            showUsername(usernameDef)
            Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp, modifier = Modifier.padding(16.dp))
            Text(text = "Haz click en los botones para ver las estadísticas de tu colección", textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp))
            Row(modifier = Modifier.fillMaxWidth()){
                ElevatedButton(onClick = { viewModel.getTotalValuePcs() }, modifier = Modifier.padding(8.dp)) {
                    Text(text = "Mostrar valor total \n de la colección")
                }
                ElevatedButton(onClick = { viewModel.getTotalPcsInCollection() }, modifier = Modifier.padding(8.dp)) {
                    Text(text = "Mostrar total de photocards en la colección")
                }
            }
            Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp, modifier = Modifier.padding(16.dp))
            GroupText()
            AddKGroup(viewModel, kGroup,{ viewModel.onKGroupChanged(it) })  {viewModel.addKgroupToUser(kGroup)}
            Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp, modifier = Modifier.padding(16.dp))
            Text(text = "Añade un Idol a tu colección",
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp))
            KgroupExposedDropdownMenuBox(kGroups){ selectedText ->
                selectedKGroup = selectedText
            }
            addIdolTextField (viewModel,idol, {viewModel.onIdolChanged(it)}, selectedKGroup) {selectedKGroup, idolText ->
                viewModel.addIdolToUser(selectedKGroup, idolText)
            }
            successMessage?.let { showToast(message = it) }

        }
    }
    }

@Composable
fun showUsername(username_def: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = username_def, style = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 30.sp))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addIdolTextField(
    viewModel: ProfileScreenViewModel,
    idol: String,
    onIdolChanged: (String) -> Unit,
    selectedKGroup: String,
    addIdolToUser: (String, String) -> Unit
) {
    var isButtonEnabled by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = idol,
        onValueChange = { onIdolChanged(it)
            isButtonEnabled = it.isNotBlank()},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Idol") },
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        label = { Text("Añade un idol a tu coleción ") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color(0xFFFFFFFF),
            placeholderColor = Color(0xFFFFFFFF),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(15.dp)
    )
    Spacer(modifier = Modifier.padding(16.dp))
    ElevatedButton(
        onClick = {
            Log.d("Hola", "El boton hace click")
            addIdolToUser(selectedKGroup,idol)

        },
        modifier = Modifier
            .height(40.dp),
        enabled = isButtonEnabled
    ) {
        Text(text = "Añadir")
    }
}
@Composable
fun showToast(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}
@Composable
fun GroupText(){
    Text(
        text = "Añade un grupo a tu colección",
        textAlign = TextAlign.Left,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun ProfileImage(profilePicture: String, modifier: Modifier, viewModel: ProfileScreenViewModel) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.changeProfilePicture(uri)
        }
    }
    Box(modifier = Modifier.fillMaxSize().padding(8.dp)){
        AsyncImage(model = profilePicture, contentDescription = "ProfilePic", modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
            .then(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ),
            contentScale = ContentScale.Crop, alignment = Alignment.Center)

    }
    ElevatedButton(onClick = { launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
        Text(text = "Cambiar foto de perfil")
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKGroup(viewModel: ProfileScreenViewModel, kGroup: String, onKgroupChanged: (String) -> Unit, addKgroupToUser: () -> Unit) {
    var isButtonEnabled by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = kGroup,
        onValueChange = {
            onKgroupChanged(it)
            isButtonEnabled = it.isNotBlank()
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Kgroup") },
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer),
        singleLine = true,
        maxLines = 1,
        label = { Text("Añade un grupo a tu colección") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color(0xFFFFFFFF),
            placeholderColor = Color(0xFFFFFFFF),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(15.dp)
    )

    Spacer(modifier = Modifier.padding(16.dp))

    LaunchedEffect(kGroup) {
        isButtonEnabled = kGroup.isNotBlank()
    }

    ElevatedButton(
        onClick = {
            Thread.sleep(3000)
            addKgroupToUser()

        },
        modifier = Modifier
            .height(40.dp),
        enabled = isButtonEnabled
    ) {
        Text(text = "Añadir")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KgroupExposedDropdownMenuBox(kGroups: List<String>, onItemSelected: (String) -> Unit) {
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
fun AlertDialogProfileScreen(
    navController: NavController,
    viewModel: ProfileScreenViewModel,
    dialogText: String
){

    AlertDialog(
        onDismissRequest = {viewModel.onDismissDialog()},
        confirmButton = {
            TextButton(onClick = {
                viewModel.onDismissDialog() }) {
                Text(text = stringResource(id = R.string.ok_message))
            }
        },
        title= { Text(text = "Valor total de photocards", color = MaterialTheme.colorScheme.onPrimaryContainer)},
        text = {Text(text = dialogText, color = MaterialTheme.colorScheme.onPrimaryContainer)}
    )

}

@Composable
fun ChangeProfilePicture(viewModel: ProfileScreenViewModel, activateChangePfp : Boolean) {
    var pfpAlertDialogExpanded by remember { mutableStateOf(false) }
    if (pfpAlertDialogExpanded){
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            pfpAlertDialogExpanded = true
        }
    }
    launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    var activateChangePfp = false

}







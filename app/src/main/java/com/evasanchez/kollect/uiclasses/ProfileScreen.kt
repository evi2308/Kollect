@file:OptIn(ExperimentalMaterial3Api::class)

package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.evasanchez.kollect.ViewModels.ProfileScreenViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(navController: NavHostController, viewModel : ProfileScreenViewModel) {
    val kGroup: String by viewModel.kGroup.observeAsState(initial = "")
    val idol: String by viewModel.idol.observeAsState(initial = "")
    val kGroups : List<String> by viewModel.allGroups.observeAsState(initial = listOf())
    var selectedKGroup by remember { mutableStateOf(if (kGroups.isNotEmpty()) kGroups[0] else "") }
    LaunchedEffect(viewModel) {
        viewModel.addKgroupToUser(kGroup)
    }
        Column{
            Text(text = "PERFIL DE USUARIO")
            AddKGroup(viewModel, kGroup,{ viewModel.onKGroupChanged(it) })  {viewModel.addKgroupToUser(kGroup)}
            Spacer(Modifier.padding(16.dp))
            Text(text = "Añade un Idol a tu colección",
                modifier = Modifier.fillMaxWidth())
            KgroupExposedDropdownMenuBox(kGroups){ selectedText ->
                selectedKGroup = selectedText
            }
            addIdolTextField (viewModel,idol, {viewModel.onIdolChanged(it)}, selectedKGroup) {selectedKGroup, idolText ->
                viewModel.addIdolToUser(selectedKGroup, idolText)
            }
            


        }
    }
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
        label = { Text("Añade un grupo a tu coleción ") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKGroup(viewModel: ProfileScreenViewModel, kGroup: String, onKgroupChanged: (String) -> Unit, addKgroupToUser:()-> Unit) {
    var isButtonEnabled by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = kGroup,
        onValueChange = { onKgroupChanged(it)
            isButtonEnabled = it.isNotBlank()},

        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Kgorup") },
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        label = { Text("Añade un grupo a tu coleción ") },
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



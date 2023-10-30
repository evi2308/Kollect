@file:OptIn(ExperimentalMaterial3Api::class)

package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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

        Column{
            Text(text = "PERFIL DE USUARIO")
            AddKGroup(kGroup,{ viewModel.onKGroupChanged(it) })  {viewModel.addKgroupToUser(kGroup)}
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKGroup(kGroup: String, onKgroupChanged: (String) -> Unit, addKgroupToUser:()-> Unit) {
    OutlinedTextField(
        value = kGroup,
        onValueChange = { onKgroupChanged(it)},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Kgorup") },
        textStyle = TextStyle(color = Color.Black),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        label = { Text("Añade un grupo a tu coleción ") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color(0xFFFFFFFF),
            placeholderColor = Color(0xFFFFFFFF),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,

            //focusedIndicatorColor = Color.Transparent,
            //unfocusedIndicatorColor = Color.Transparent,
            //disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(15.dp)
    )
    ElevatedButton(
        onClick = {
            Log.d("Hola", "El boton hace click")
            addKgroupToUser()
    },
        modifier = Modifier
            .height(40.dp),
        ) {
            Text(text = "Añadir")
    }
}

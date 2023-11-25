package com.evasanchez.kollect.uiclasses

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.RegisterScreenViewModel
import com.evasanchez.kollect.navigation.AppScreens
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterScreenViewModel) {

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Register(Modifier.align(Alignment.Center), navController, viewModel)
    }

}

@Composable
fun Register(modifier: Modifier, navController: NavController, viewModel: RegisterScreenViewModel) {

    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val isPassVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val loginEnabled: Boolean by viewModel.loginEnabled.observeAsState(initial = false)
    val username: String by viewModel.username.observeAsState(initial = "")
    Column(modifier = modifier) {
        HeaderRegister(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(8.dp))
        emailRegister(modifier, email) { viewModel.onRegisterChanged(it, password, username) }
        Spacer(modifier = Modifier.padding(4.dp))
        usernameRegister(modifier, username) { viewModel.onRegisterChanged(email, password, it) }
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordRegister(modifier, password, { viewModel.onRegisterChanged(email, it, username) }, isPassVisible
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "El botón no se activará si no se cumplen las condiciones",fontSize = 15.sp, style = TextStyle(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        RegistrarButton(loginEnabled) {
            viewModel.createUserEmailPassword(email, password) {
                navController.navigate(AppScreens.LoginScreen.route)
                if (navController.previousBackStackEntry?.destination?.route == AppScreens.RegisterScreen.route) {
                    navController.popBackStack(AppScreens.RegisterScreen.route, inclusive = true)
                }
            }
        }

    }

}

@Composable
fun RegistrarButton(loginEnabled: Boolean, createUserEmailPassword: () -> Unit) {
    ElevatedButton(
        onClick = {
            if (loginEnabled) {
                createUserEmailPassword()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        enabled = loginEnabled
    ) {
        Text(text = "Registrarse")
    }
}

@Composable
fun HeaderRegister(modifier: Modifier) {
    Image(
        painterResource(id = R.drawable.logo),
        contentDescription = "Logo de Kollect",
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun emailRegister(modifier: Modifier, email: String, onTextFieldChanged: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(
            text = "Introduce tu E-Mail",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { onTextFieldChanged(it) },
            placeholder = { Text("ejemplo@ejemplo.com") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            maxLines = 1,
            label = { Text("E-mail") },
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
        Text(text = "Introduce un E-mail válido que no esté en uso", fontSize = 15.sp, style = TextStyle(color = MaterialTheme.colorScheme.tertiary))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun usernameRegister(modifier: Modifier, username: String, onTextFieldChanged: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(
            text = "Introduce tu nombre de usuario",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = { onTextFieldChanged(it) },
            placeholder = { Text(text = "username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1,
            label = { Text("username") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color(0xFFFFFFFF),
                placeholderColor = Color(0xFFFFFFFF),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
                //focusedIndicatorColor = Color.Transparent,
                //unfocusedIndicatorColor = Color.Transparent,
                //disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(15.dp)
        )

        Text(text = "Introduce un nombre de usuario válido que no esté en uso", fontSize = 15.sp, style = TextStyle(color = MaterialTheme.colorScheme.tertiary))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRegister(modifier: Modifier,password: String, onTextFieldChanged: (String) -> Unit, isPassVisible: MutableState<Boolean>) {
    Text(
        text = "Introduce tu contraseña",
        textAlign = TextAlign.Left,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
    val visualTransformation = if (isPassVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(value = password,
        onValueChange = {onTextFieldChanged(it)},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        label = { Text("Contraseña") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color(0xFFFFFFFF),
            placeholderColor = Color(0xFFFFFFFF),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(15.dp),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (password != "") {
                IsPassVisibleIcon(isPassVisible)
            }
        }
    )
    Spacer(modifier = Modifier.size(16.dp))
    Text(text = "La contraseña debe contener al menos 8 caracteres, una minúscula, una mayúscula, un número y un caracter especial", fontSize = 15.sp, style = TextStyle(color = MaterialTheme.colorScheme.tertiary))

}





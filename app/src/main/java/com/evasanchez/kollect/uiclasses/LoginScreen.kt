package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.LoginScreenViewModel
import com.evasanchez.kollect.navigation.AppScreens


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginScreenViewModel) {

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                Login(Modifier.align(Alignment.Center),viewModel, navController)}
            }

        })

}

@Composable
fun Login(modifier: Modifier, viewModel: LoginScreenViewModel, navController: NavController) {
    //Variables para guardar los estados de los TextView
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val isPassVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val loginEnabled: Boolean by viewModel.loginEnabled.observeAsState(initial = false)

    //Variables para guardar estado de los errores para mostrar los AlertDialog
    val errorMessage = viewModel.errorMessage.observeAsState("").value
    // Observa si se debe mostrar el AlertDialog
    val showErrorDialog = viewModel.showErrorDialog.observeAsState(false).value

    if (showErrorDialog) {
        AlertDialogLoginError(viewModel,errorMessage)
    }


    Column(modifier = modifier) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        TextEmail()
        EmailField(email, { viewModel.onLoginChanged(it, password) })
        Spacer(modifier = Modifier.padding(4.dp))
        TextPassword()
        PasswordField(password, { viewModel.onLoginChanged(email, it) }, isPassVisible)
        Spacer(modifier = Modifier.padding(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            RegisterButton(navController)
            LoginButton(loginEnabled){
                viewModel.singInEmailPassword(email, password) {
                    navController.navigate(AppScreens.ProfileScreen.route)
                }
            }

        }
    }

}

@Composable
fun TextEmail() {
    Text(
        text = "Introduce tu E-Mail",
        textAlign = TextAlign.Left,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp))

}

@Composable
fun TextPassword() {
    Text(
        text = "Introduce tu contraseña",
        textAlign = TextAlign.Left,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp))

}

@Composable
fun RegisterButton(navController: NavController) {
    ElevatedButton(
        onClick = { navController.navigate(AppScreens.RegisterScreen.route) },
        modifier = Modifier
            .height(48.dp)
    ) {
        Text(text = "Registrarse")
    }
}

@Composable
fun LoginButton(loginEnabled: Boolean, singInEmailPassword: () -> Unit) {
    ElevatedButton(
        onClick = {
            if (loginEnabled) {
                singInEmailPassword()

            }
        },
        modifier = Modifier
            .height(48.dp),
        enabled = loginEnabled
    ) {
        Text(text = "Iniciar Sesión")

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit, isPassVisible: MutableState<Boolean>) {

    val visualTransformation = if (isPassVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        placeholder = { Text(text = "*********") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        label = { Text("Contraseña") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color(0xFFFFFFFF),
            placeholderColor = Color(0xFFFFFFFF),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,


        ),
        shape = RoundedCornerShape(15.dp),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (password != "") {
                IsPassVisibleIcon(isPassVisible)
            }
        }
    )

}

@Composable
fun IsPassVisibleIcon(isPassVisible: MutableState<Boolean>) {
    val icon =
        if (isPassVisible.value)
            Icons.Default.VisibilityOff
        else
            Icons.Default.Visibility
    IconButton(onClick = { isPassVisible.value = !isPassVisible.value }) {
        Icon(imageVector = icon, contentDescription = "")

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier.fillMaxWidth(),//Lo que se escriba en el textField se guardara
        placeholder = { Text("ejemplo@ejemplo.com") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        label = { Text("E-mail") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color(0xFFFFFFFF),
            placeholderColor = Color(0xFFFFFFFF),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,

        ),
        shape = RoundedCornerShape(15.dp)
    )
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painterResource(id = R.drawable.logo),
        contentDescription = "Logo de Kollect",
        modifier = modifier
    )
}

@Composable
fun AlertDialogLoginError(viewModel: LoginScreenViewModel, errorMessage: String){

    AlertDialog(
        onDismissRequest = {viewModel.onDismissErrorDialog()},
        confirmButton = {
        TextButton(onClick = { viewModel.onDismissErrorDialog() }) {
            Text(text = stringResource(id = R.string.ok_message))
        }                
                        },
        title= { Text(text = "Error de inicio de sesión", color = MaterialTheme.colorScheme.error)},
        text = {Text(text = errorMessage, color = MaterialTheme.colorScheme.error)}
        )

}
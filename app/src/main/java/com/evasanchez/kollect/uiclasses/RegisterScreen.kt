package com.evasanchez.kollect.uiclasses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun RegisterScreen(){
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        Register(Modifier.align(Alignment.Center))
    }

}

@Composable
fun Register(modifier: Modifier) {
    Column(modifier = modifier){
        emailRegister(modifier)
        //emailRegisterText()
        //emailRegisterTextField()
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordRegisterText()
        PasswordRegisterTextField()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun emailRegister(modifier: Modifier) {
    Column(modifier = modifier){
        Text(
            text = "Introduce tu E-Mail",
            textAlign = TextAlign.Left,
            color = Color(0xFF7D5260),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = "", onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "ejemplo@ejemplo.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(0xFFFFFFFF),
                containerColor = Color(0xFFF1D5DB),
                placeholderColor = Color(0xFFFFFFFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(15.dp)
        )

    }

}

@Composable
fun emailRegisterText() {
    Text(
        text = "Introduce tu E-Mail",
        textAlign = TextAlign.Left,
        color = Color(0xFF7D5260),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun PasswordRegisterText() {
    Text(
        text = "Introduce tu contraseña",
        textAlign = TextAlign.Left,
        color = Color(0xFF7D5260),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun emailRegisterTextField() {
    TextField(value = "", onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "ejemplo@ejemplo.com") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFFFFFFF),
            containerColor = Color(0xFFF1D5DB),
            placeholderColor = Color(0xFFFFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(15.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRegisterTextField() {
    TextField(value = "", onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "*********") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFFFFFFF),
            containerColor = Color(0xFFF1D5DB),
            placeholderColor = Color(0xFFFFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(15.dp)
    )
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RegisterPreview(){
    RegisterScreen()
}
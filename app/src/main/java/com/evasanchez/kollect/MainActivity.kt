package com.evasanchez.kollect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.evasanchez.kollect.ViewModels.LoginScreenViewModel
import com.evasanchez.kollect.navigation.AppNavigation
import com.evasanchez.kollect.ui.theme.KollectTheme
import com.evasanchez.kollect.uiclasses.LoginPreview
import com.evasanchez.kollect.uiclasses.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
            }
        }
    }




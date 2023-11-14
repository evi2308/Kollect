package com.evasanchez.kollect.uiclasses

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.ViewModels.SearchViewModel
import com.evasanchez.kollect.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel){
    val searchText by viewModel.searchText.collectAsState()
    val users by viewModel.users.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = searchText, onValueChange = viewModel::onSearchTextChanged,modifier=Modifier.fillMaxWidth(), placeholder = { Text(text = "Buscar")}, shape = RoundedCornerShape(16.dp), leadingIcon = {Icon(imageVector = Icons.Outlined.Search, contentDescription = null)})
        Spacer(modifier = Modifier.height(16.dp))
        if(searchText.isNotBlank()){
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)){
                items(users){ user ->
                    Box{
                        Row{
                            Card(modifier = Modifier
                                .height(96.dp)
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        Log.d("Busqueda", "User clickado: ${user.username}")
                                        viewModel.selectedUser(user)
                                        Log.d("Busqueda", "User ${viewModel.selectedUserDetail?.username}")
                                        if (viewModel.selectedUserDetail != null) {
                                            navController.navigate(AppScreens.WishlistSearchScreen.route)
                                        }
                                    },
                                ),
                                shape = RoundedCornerShape(15.dp),
                                elevation = CardDefaults.cardElevation(5.dp)
                            ) {
                                Box(modifier = Modifier.height(200.dp)){
                                    AsyncImage(
                                        model = user.pfpURL,
                                        contentDescription = "Translated description of what the image contains",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black
                                                ), startY = 300f
                                            )
                                        ))
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp), contentAlignment = Alignment.BottomStart) {
                                        Column {
                                            Text(text = user.username,
                                                style = TextStyle(
                                                    fontWeight = FontWeight.Bold))

                                        }
                                    }

                                }
                            }  //Hasta aqui llega el LazyColumn
        }


                    }

                }

            }
        }
    }
}


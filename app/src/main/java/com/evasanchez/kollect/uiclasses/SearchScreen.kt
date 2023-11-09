package com.evasanchez.kollect.uiclasses

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.ViewModels.SearchViewModel

@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel){
    val searchText by viewModel.searchText.collectAsState("")
    val users by viewModel.users.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        //SearchBar(searchText){viewModel.onSearchTextChanged(searchText)}
        SearchBarComponent(searchText){viewModel.onSearchTextChanged(searchText)}
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)){
            items(users){ user ->
                Box{
                    Row{
                        AsyncImage(model = user.pfpURL, contentDescription = "")
                        Text( text= "${user.username}", modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp), style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }

                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
    OutlinedTextField(value = searchText, onValueChange ={ onSearchTextChanged(it) }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(
        text = "Búsqueda"
    )})
}

@Composable
fun SearchBarComponent(searchText: String, onSearchTextChanged: (String) -> Unit){
    SearchBar(searchText = searchText , onSearchTextChanged = {onSearchTextChanged(it)})
}

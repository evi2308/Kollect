package com.evasanchez.kollect.uiclasses

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.evasanchez.kollect.ViewModels.PhotocardDetailViewModel

@Composable
fun PhotocardDetail(navController: NavController, viewModel: PhotocardDetailViewModel){
    val viewModel = PhotocardDetailViewModel()
    val albumName by viewModel.albumName.observeAsState("")
    val status by viewModel.status.observeAsState("")
    val groupName by viewModel.groupName.observeAsState("")
    val idolName by viewModel.idolName.observeAsState("")
    val value by viewModel.value.observeAsState("")
    val type by viewModel.type.observeAsState("")
    val photocardURL by viewModel.photocardURL.observeAsState("")
    val photocardVersion by viewModel.photocardVersion.observeAsState("")
    Text(text= "DETALLES")


    Column {
        Text("Album Name: $albumName")
        Text("Status: $status")
        Text("Group Name: $groupName")
        Text("Idol Name: $idolName")
        Text("Value: $value")
        Text("Type: $type")
        Text("Photocard URL: $photocardURL")
        Text("Photocard Version: $photocardVersion")
    }
}
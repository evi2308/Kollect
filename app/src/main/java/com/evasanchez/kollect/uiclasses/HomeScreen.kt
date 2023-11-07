package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.HomeScreenViewModel
import com.evasanchez.kollect.data.Photocard


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel) {
    val collectionPhotocards by viewModel.photocardsList.observeAsState(emptyList())
    LaunchedEffect(viewModel) {
        viewModel.getPhotocardsList()
    }
    LazyColumn {
        items(collectionPhotocards) { photocard ->
            Text("A: ${photocard.photocardVersion}")
            photocardCardComponent(photocard)
        }

    }
}





@Composable
fun photocardCardComponent(photocard: Photocard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
    ) {
        Box{
            AsyncImage(
                model = photocard.photocardURL,
                contentDescription = "Translated description of what the image contains"
            )
            Text(text = "IDOL: " + photocard.idolName)
            Text(text = "ALBUM: " + photocard.albumName)
        }
        

    }

}
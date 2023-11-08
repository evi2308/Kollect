package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.HomeScreenViewModel
import com.evasanchez.kollect.data.Photocard
import com.evasanchez.kollect.navigation.AppScreens


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel) {
    val collectionPhotocards by viewModel.photocardsList.observeAsState(emptyList())
    LaunchedEffect(viewModel) {
        viewModel.getPhotocardsList()
    }
    /*LazyColumn {
        items(collectionPhotocards) { photocard ->
            Text("A: ${photocard.photocardVersion}")
            photocardCardComponent(photocard)
        }

    }*/
    //val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(topBar ={TopAppBar(title = { Text(text = "MI COLECCIÓN",
        style = TextStyle(MaterialTheme.colorScheme.onSecondaryContainer,
        fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        ) ) }, colors = TopAppBarDefaults.smallTopAppBarColors(MaterialTheme.colorScheme.secondaryContainer)) }) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),

            // content padding
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 80.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(collectionPhotocards.size) { index ->
                    photocardCardComponent(navController,photocard = collectionPhotocards[index])
                }
            }
        )
    }

}





@Composable
fun photocardCardComponent(navController: NavController, photocard: Photocard, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    Log.d("Coleccion", "Photocard clickada ${photocard.photocardId} ${photocard.status}")
                    navController.navigate(AppScreens.PhotocardDetail.route + photocard.photocardId)
                }

            ),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(modifier = Modifier.height(200.dp)){
            AsyncImage(
                model = photocard.photocardURL,
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
                    Text(text = photocard.idolName,
                        style = TextStyle(MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold))
                    Text(text =photocard.albumName + " - " + photocard.type,
                        style = TextStyle(MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

        }
    }
}
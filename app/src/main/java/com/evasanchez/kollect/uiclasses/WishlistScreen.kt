package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.CollectionWishlistViewModel
import com.evasanchez.kollect.data.Photocard
import com.evasanchez.kollect.navigation.AppScreens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(navController: NavHostController, viewModel: CollectionWishlistViewModel) {
    val toastMessage: String? by viewModel.toastMessage.observeAsState()
    val collectionPhotocards by viewModel.photocardsWishlistList.observeAsState(emptyList())
    LaunchedEffect(viewModel) {
        viewModel.getPhotocardsWishlistList()
    }
    val selectedPhotocard by viewModel.selectedPhotocard.observeAsState() //Photocard que quiero enviar a la pantalla de detalle
    Scaffold(topBar ={
        TopAppBar(title = { Text(text = "MI WISHLIST",
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
                    val photocard = collectionPhotocards[index]
                    photocardCardWishlistComponent(selectedPhotocard,viewModel,navController, photocard = collectionPhotocards[index]){viewModel.addPhotocardDetail(photocard)}
                }
            }
        )
    }
    toastMessage?.let { showDetailToast(message = it) }

}

@Composable
fun photocardCardWishlistComponent(
    selectedPhotocard: Photocard?,
    viewModel: CollectionWishlistViewModel,
    navController: NavController,
    photocard: Photocard,
    addPhotocardDetail: (Photocard) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    Log.d("Coleccion", "Photocard clickada ${photocard.photocardId}")
                    addPhotocardDetail(photocard)
                    navController.navigate(AppScreens.PhotocardDetail.route)
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
                        style = TextStyle(
                            MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold)
                    )
                    Text(text =photocard.albumName + " - " + photocard.type,
                        style = TextStyle(
                            MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            if (photocard.isPrio) {
                Icon(
                    painter = painterResource(id = R.drawable.estrella),
                    contentDescription = "Priority Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .align(Alignment.TopStart),
                    tint = Color.Unspecified
                )
            }

            if (photocard.isOtw) {
                Icon(
                    painter = painterResource(id = R.drawable.carta),
                    contentDescription = "Priority Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .align(Alignment.TopEnd),
                    tint = Color.Unspecified
                )
            }


        }
    }
}
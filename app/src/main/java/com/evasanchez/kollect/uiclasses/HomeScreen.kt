package com.evasanchez.kollect.uiclasses

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.evasanchez.kollect.data.Photocard
import com.evasanchez.kollect.navigation.AppScreens
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.evasanchez.kollect.R
import com.evasanchez.kollect.ViewModels.CollectionWishlistViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: CollectionWishlistViewModel) {
    val collectionPhotocards by viewModel.photocardsList.observeAsState(emptyList())
    val kGroups : List<String> by viewModel.allGroups.observeAsState(initial = listOf())
    var selectedKGroup by remember { mutableStateOf(if (kGroups.isNotEmpty()) kGroups[0] else "") }
    LaunchedEffect(viewModel) {
        viewModel.getPhotocardsCollectionList()
        viewModel.getKGroupListRepository()

    }
    val selectedPhotocard by viewModel.selectedPhotocard.observeAsState() //Photocard que quiero enviar a la pantalla de detalle
    Scaffold(topBar ={TopAppBar(title = { Text(text = "MI COLECCIÓN",
        style = TextStyle(MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        ) ) }, colors = TopAppBarDefaults.smallTopAppBarColors(MaterialTheme.colorScheme.secondaryContainer)) }) {
        Column(modifier = Modifier
            .fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp)) {
                KgroupExposedDropdownMenuBoxFilter(kGroups){ selectedText ->
                    selectedKGroup = selectedText
                    viewModel.getIdolsBasedOnKgroup(selectedText)
                    viewModel.onGroupSelected(selectedKGroup)
                }

                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Filtrar")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Borrar filtro")
                }

            }

        }
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
                    photocardCardComponent(selectedPhotocard,viewModel,navController, photocard = collectionPhotocards[index]){viewModel.addPhotocardDetail(photocard)}
                }
            }
        )
    }

}




@Composable
fun photocardCardComponent(
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
                    navController.navigate(AppScreens.PhotocardDetail.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
            ),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(modifier = Modifier.height(200.dp)){
            AsyncImage(
                model = photocard.photocardURL,
                contentDescription = null,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KgroupExposedDropdownMenuBoxFilter(kGroups: List<String>, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember {  mutableStateOf(if (kGroups.isNotEmpty()) kGroups[0] else "") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 56.dp, bottom = 16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                label= { Text(text = "Selecciona un grupo")},
                placeholder = {Text(text = "Selecciona un grupo")},
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                kGroups.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}




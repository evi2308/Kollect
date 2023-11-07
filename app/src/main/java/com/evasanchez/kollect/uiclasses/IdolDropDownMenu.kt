package com.evasanchez.kollect.uiclasses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evasanchez.kollect.ViewModels.IdolDropDownMenuViewModel
import com.evasanchez.kollect.ViewModels.PhotocardFormViewModel

@Composable
fun showListIdols(){
    var viewModel =IdolDropDownMenuViewModel()
    var photocardViewModel = PhotocardFormViewModel()
    val idols : List<String> by viewModel.allIdols.observeAsState(initial = listOf())
    var expanded by remember { mutableStateOf(false) }
    var selectedIdol by remember {  mutableStateOf(if (idols.isNotEmpty()) idols[0] else "") }
    IdolExposedDropdownMenuBox(idols, { selectedText ->
        selectedIdol = selectedText
        photocardViewModel.onIdolSelected(selectedIdol) //SI ALGO FALLA QUITAR ESTO
    })


}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdolExposedDropdownMenuBox(idols: List<String>, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember {  mutableStateOf(if (idols.isNotEmpty()) idols[0] else "") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded

            }
        ) {
            TextField(
                value = selectedText,
                label= { Text(text = "Selecciona un idol") },
                placeholder = { Text(text = "Selecciona un idol") },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                idols.forEach { item ->
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
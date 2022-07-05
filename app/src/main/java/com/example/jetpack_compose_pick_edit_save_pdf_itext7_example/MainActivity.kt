package com.example.jetpack_compose_pick_edit_save_pdf_itext7_example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpack_compose_pick_edit_save_pdf_itext7_example.state.HomeState
import com.example.jetpack_compose_pick_edit_save_pdf_itext7_example.ui.theme.Jetpack_Compose_Pick_Edit_Save_PDF_itext7_ExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Jetpack_Compose_Pick_Edit_Save_PDF_itext7_ExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val homeState = remember {
                        HomeState()
                    }
                    homeState.Init()
                    Home(
                        onPickPDF = { homeState.onPickPDF() },
                        onEditPDF = { homeState.onEditPDF() },
                        onSavePDF = { homeState.onSavePDF() },
                        pickedUriList = homeState.pickedUriList,
                        isFilePickedSuccessfully = homeState.isFilePickedSuccessfully.value,
                        isEditOperationSuccessful = homeState.isEditOperationSuccessful.value
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onPickPDF: () -> Unit,
    onEditPDF: () -> Unit,
    onSavePDF: () -> Unit,
    pickedUriList: MutableList<Uri>,
    isFilePickedSuccessfully: Boolean,
    isEditOperationSuccessful: Boolean
) {
    Scaffold() { contentPadding ->
        LazyColumn(modifier = Modifier.padding(contentPadding)) {
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    PickedUrisCard(cardTitle = "Read-Write to Uris", cardActionContent = {
                        pickedUriList.forEachIndexed { index, uri ->
                            Text(uri.toString(), style = MaterialTheme.typography.bodyMedium)
                            if (pickedUriList.size != index + 1) {
                                Divider(
                                    modifier = Modifier.padding(
                                        horizontal = 28.dp,
                                        vertical = 16.dp
                                    )
                                )
                            }
                        }
                    })
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = onPickPDF) {
                        Text(text = "Pick PDF")
                    }
                    if (isFilePickedSuccessfully) {
                        Button(onClick = onEditPDF) {
                            Text(text = "Edit PDF")
                        }
                    }
                    if (isEditOperationSuccessful) {
                        Button(onClick = onSavePDF) {
                            Text(text = "Save PDF")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickedUrisCard(
    cardColors: CardColors = CardDefaults.cardColors(),
    cardTitle: String = "",
    cardBody: String = "",
    cardActionContent: @Composable () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = cardColors,
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                cardTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 16.dp, bottom = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            if (cardBody != "") {
                Text(
                    cardBody,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(8.dp))
            cardActionContent()
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Jetpack_Compose_Pick_Edit_Save_PDF_itext7_ExampleTheme {
        Home(
            onPickPDF = { /*TODO*/ },
            onEditPDF = { /*TODO*/ },
            onSavePDF = { /*TODO*/ },
            pickedUriList = mutableListOf(),
            isFilePickedSuccessfully = true,
            isEditOperationSuccessful = true
        )
    }
}
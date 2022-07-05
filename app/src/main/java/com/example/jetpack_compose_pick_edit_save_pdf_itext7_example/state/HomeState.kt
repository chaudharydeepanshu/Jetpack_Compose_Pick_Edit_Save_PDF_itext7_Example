package com.example.jetpack_compose_pick_edit_save_pdf_itext7_example.state

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.jetpack_compose_pick_edit_save_pdf_itext7_example.editDocument
import com.example.jetpack_compose_pick_edit_save_pdf_itext7_example.utils.UtilityFunctionsClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class HomeState {
    private lateinit var composableScope: CoroutineScope
    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    lateinit var pickedUriList: MutableList<Uri>

    @Composable
    fun Init() {
        composableScope = rememberCoroutineScope()
        context = LocalContext.current
        contentResolver = context.contentResolver
        pickedUriList = remember {
            // mutableStateListOf<Uri>( )
            contentResolver.persistedUriPermissions.map { it.uri }.toMutableStateList()
        }
        InitFilePickingVars()
        InitFileSavingVars()
    }

    private fun updatePickedUriList() {
        pickedUriList.clear()
        pickedUriList.addAll(contentResolver.persistedUriPermissions.map { permission -> permission.uri })
    }

    private var resultFile: File = File.createTempFile("resultFile", ".pdf")

    //---------For Buttons Status---------
    var isEditOperationSuccessful: MutableState<Boolean> = mutableStateOf(false)
    var isFilePickedSuccessfully: MutableState<Boolean> = mutableStateOf(false)
    //---------For Buttons Status---------


    //---------For Picking file---------
    private var filePickedUri: Uri? = null
    private lateinit var intentForPickingFile: Intent
    private lateinit var launcherForPickingFile: ManagedActivityResultLauncher<Intent, ActivityResult>

    @Composable
    fun InitFilePickingVars() {
        intentForPickingFile =
            UtilityFunctionsClass().openFileIntentProvider(pickerInitialUri = Uri.parse("/"))
        launcherForPickingFile =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    filePickedUri = it.data?.data
                    if (filePickedUri != null) {
                        UtilityFunctionsClass().storeAccessForUri(
                            uri = filePickedUri!!,
                            contentResolver = contentResolver
                        )
                        updatePickedUriList()
                        UtilityFunctionsClass().dumpImageMetaData(
                            uri = filePickedUri!!,
                            contentResolver = contentResolver
                        )
                        isFilePickedSuccessfully.value = true
                    }
                }
            }
    }
    //---------For Picking file---------

    //---------For Saving file---------
    private var locationPickedUri: Uri? = null
    private lateinit var intentForSavingFile: Intent
    private lateinit var launcherForSavingFile: ManagedActivityResultLauncher<Intent, ActivityResult>

    @Composable
    fun InitFileSavingVars() {
        intentForSavingFile =
            UtilityFunctionsClass().createFileIntentProvider(pickerInitialUri = Uri.parse("/"))
        launcherForSavingFile =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    locationPickedUri = it.data?.data
                    if (locationPickedUri != null) {
                        UtilityFunctionsClass().storeAccessForUri(
                            uri = locationPickedUri!!,
                            contentResolver = contentResolver
                        )
                        UtilityFunctionsClass().copyDataFromSourceToDestDocument(
                            sourceFileUri = resultFile.toUri(),
                            destinationFileUri = locationPickedUri!!,
                            contentResolver = contentResolver
                        )
                        UtilityFunctionsClass().deleteTempFiles(listOfTempFiles = listOf(resultFile))
                        UtilityFunctionsClass().dumpImageMetaData(
                            uri = locationPickedUri!!,
                            contentResolver = contentResolver
                        )
                    }
                }
            }
    }

    //---------For Saving file---------

    fun onPickPDF() {
        isEditOperationSuccessful.value = false
        isFilePickedSuccessfully.value = false
        composableScope.launch {
            launcherForPickingFile.launch(intentForPickingFile)
        }
    }

    fun onEditPDF() {
        composableScope.launch {
            isEditOperationSuccessful.value = editDocument(
                sourceFileUri = filePickedUri!!,
                resultFile = resultFile,
                context = context
            )

        }
    }

    fun onSavePDF() {
        composableScope.launch {
            launcherForSavingFile.launch(intentForSavingFile)
        }
    }
}
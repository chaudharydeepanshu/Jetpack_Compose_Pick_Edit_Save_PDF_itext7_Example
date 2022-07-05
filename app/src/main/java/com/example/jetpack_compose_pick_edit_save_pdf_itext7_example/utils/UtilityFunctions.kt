package com.example.jetpack_compose_pick_edit_save_pdf_itext7_example.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class UtilityFunctionsClass {

    fun copyDataFromSourceToDestDocument(
        sourceFileUri: Uri,
        destinationFileUri: Uri,
        contentResolver: ContentResolver
    ) {
        try {
            contentResolver.openInputStream(sourceFileUri).use { inputStream ->
                contentResolver.openOutputStream(destinationFileUri).use { outputStream ->
                    if (inputStream != null && outputStream != null) {
                        inputStream.copyTo(outputStream)
                        println("Data successfully copied from one file to another")
                    } else {
                        println("Either inputStream or outputStream has null value")
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun clearWholeCache(context: Context) {
        context.cacheDir.deleteRecursively()
        println("cache cleared")
        if (!context.cacheDir.exists()) {
            context.cacheDir?.mkdirs();
        }
    }


    fun deleteTempFiles(listOfTempFiles: List<File>) {
        listOfTempFiles.forEach() { tempFile ->
            tempFile.delete()
        }
    }

    fun storeAccessForUri(uri: Uri, contentResolver: ContentResolver) {
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        // Check for the freshest data.
        contentResolver.takePersistableUriPermission(uri, takeFlags)
    }

    fun dumpImageMetaData(uri: Uri, contentResolver: ContentResolver) {
        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        val cursor: Cursor? = contentResolver.query(
            uri, null, null, null, null, null
        )
        cursor?.use {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (it.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                val column = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (column >= 0) {
                    val displayName: String =
                        it.getString(column)
                    Log.i(ContentValues.TAG, "Display Name: $displayName")
                }
                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                // If the size is unknown, the value stored is null. But because an
                // int can't be null, the behavior is implementation-specific,
                // and unpredictable. So as
                // a rule, check if it's null before assigning to an int. This will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                val size: String = if (!it.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                Log.i(ContentValues.TAG, "Size: $size")
            }
        }
    }

    fun createFileIntentProvider(pickerInitialUri: Uri): Intent {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        return intent
    }


    fun openFileIntentProvider(pickerInitialUri: Uri): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        return intent
    }

    fun mToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}
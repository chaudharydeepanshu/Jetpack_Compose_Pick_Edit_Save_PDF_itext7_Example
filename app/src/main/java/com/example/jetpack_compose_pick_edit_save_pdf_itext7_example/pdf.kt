package com.example.jetpack_compose_pick_edit_save_pdf_itext7_example

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.jetpack_compose_pick_edit_save_pdf_itext7_example.utils.UtilityFunctionsClass
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import java.io.File

fun editDocument(sourceFileUri: Uri, resultFile: File, context: Context): Boolean {

    val contentResolver: ContentResolver = context.contentResolver

    val readerTempFile: File = File.createTempFile("readerFile", ".pdf")

    val writerTempFile: File = File.createTempFile("writerFile", ".pdf")

    UtilityFunctionsClass().copyDataFromSourceToDestDocument(
        sourceFileUri = sourceFileUri,
        destinationFileUri = readerTempFile.toUri(),
        contentResolver = contentResolver
    )

    UtilityFunctionsClass().copyDataFromSourceToDestDocument(
        sourceFileUri = sourceFileUri,
        destinationFileUri = writerTempFile.toUri(),
        contentResolver = contentResolver
    )

//    println(writerTempFile.toUri())
//
//    println(writerTempFile.length())

    try {
        val pdfReader = PdfReader(readerTempFile)
        val pdfWriter = PdfWriter(writerTempFile)

        val pdfDoc = PdfDocument(
            pdfReader,
            pdfWriter
        )

        val ann = PdfTextAnnotation(Rectangle(400F, 795F, 0F, 0F)).setTitle(PdfString("iText"))
            .setContents("Please, fill out the form.")

        pdfDoc.firstPage.addAnnotation(ann)

        val canvas = PdfCanvas(pdfDoc.firstPage);
        canvas.beginText().setFontAndSize(
            PdfFontFactory.createFont(), 12F
        )
            .moveText(265.0, 597.0)
            .showText("I agree to the terms and conditions.")
            .endText();

        pdfDoc.close()

        UtilityFunctionsClass().copyDataFromSourceToDestDocument(
            sourceFileUri = writerTempFile.toUri(),
            destinationFileUri = resultFile.toUri(),
            contentResolver = contentResolver
        )
        println("Result File Size: ${writerTempFile.length()}")

        UtilityFunctionsClass().mToast(context = context, "PDF Edited Successfully")

        UtilityFunctionsClass().deleteTempFiles(
            listOfTempFiles = listOf(
                readerTempFile,
                writerTempFile
            )
        )

//    val pdf =  PdfDocument(pdfWriter);
//    val document = Document(pdf);
//    document.add(Paragraph("Hello World!"));
//    document.close();

        return true
    } catch (e: Exception) {
        UtilityFunctionsClass().mToast(context = context, e.toString())
        println(e)
        UtilityFunctionsClass().deleteTempFiles(
            listOfTempFiles = listOf(
                readerTempFile,
                writerTempFile
            )
        )
        return false
    }
}
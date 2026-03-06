package com.example.askanyone.utils


import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri): File {
    val input = context.contentResolver.openInputStream(uri)!!
    val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
    val output = FileOutputStream(file)
    input.copyTo(output)
    input.close()
    output.close()
    return file
}
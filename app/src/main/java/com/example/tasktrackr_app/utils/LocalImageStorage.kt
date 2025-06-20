package com.example.tasktrackr_app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object LocalImageStorage {
    private const val STORAGE_DIR = "storage"
    private const val PROFILE_DIR = "profile"
    private const val TEAM_DIR = "team"

    fun init(context: Context) {
        try {
            val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val storageDir = File(baseDir, STORAGE_DIR).apply {
                if (!exists()) mkdirs()
            }
            // Create profile directory
            File(storageDir, PROFILE_DIR).apply {
                if (!exists()) mkdirs()
            }
            // Create team directory
            File(storageDir, TEAM_DIR).apply {
                if (!exists()) mkdirs()
            }
        } catch (e: Exception) {
            Log.e("LocalImageStorage", "Error creating directories", e)
        }
    }

    private fun generateShortUUID(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }

    private fun saveImage(context: Context, imageUri: Uri, directory: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            if (bitmap == null) {
                Log.e("LocalImageStorage", "Failed to decode bitmap from input stream")
                return null
            }

            val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val targetDir = File(File(baseDir, STORAGE_DIR), directory).apply {
                if (!exists()) mkdirs()
            }

            val fileName = "${generateShortUUID()}.jpg"
            val file = File(targetDir, fileName)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }

            bitmap.recycle()
            inputStream?.close()

            "$directory/$fileName"
        } catch (e: Exception) {
            Log.e("LocalImageStorage", "Error saving image to $directory", e)
            null
        }
    }

    fun saveProfileImage(context: Context, imageUri: Uri): String? {
        return saveImage(context, imageUri, PROFILE_DIR)
    }

    fun saveTeamLogo(context: Context, imageUri: Uri): String? {
        return saveImage(context, imageUri, TEAM_DIR)
    }

    fun getImageFile(context: Context, relativePath: String?): File? {
        if (relativePath == null) return null

        val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(File(baseDir, STORAGE_DIR), relativePath)
        return file.takeIf { it.exists() }
    }
}


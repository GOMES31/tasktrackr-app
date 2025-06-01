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

    fun init(context: Context) {
        try {
            val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            Log.d("LocalImageStorage", "Base directory: ${baseDir?.absolutePath}")

            val storageDir = File(baseDir, STORAGE_DIR).apply {
                if (!exists()) {
                    val created = mkdirs()
                    Log.d("LocalImageStorage", "Storage directory created: $created at $absolutePath")
                }
            }

            val profileDir = File(storageDir, PROFILE_DIR).apply {
                if (!exists()) {
                    val created = mkdirs()
                    Log.d("LocalImageStorage", "Profile directory created: $created at $absolutePath")
                }
            }
        } catch (e: Exception) {
            Log.e("LocalImageStorage", "Error creating directories", e)
        }
    }

    private fun generateShortUUID(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }

    fun saveProfileImage(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val profileDir = File(File(baseDir, STORAGE_DIR), PROFILE_DIR)
            val fileName = "${generateShortUUID()}.jpg"
            val imageFile = File(profileDir, fileName)

            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            "$PROFILE_DIR/$fileName"
        } catch (e: Exception) {
            Log.e("LocalImageStorage", "Error saving image", e)
            null
        }
    }

    fun getImageFile(context: Context, relativePath: String?): File? {
        if (relativePath == null) {
            Log.d("LocalImageStorage", "Relative path is null")
            return null
        }

        val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(File(baseDir, STORAGE_DIR), relativePath)
        Log.d("LocalImageStorage", "Getting image file at: ${file.absolutePath}, exists: ${file.exists()}")
        return file.takeIf { it.exists() }
    }
}

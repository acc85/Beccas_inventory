package com.raymond.beccasinventory.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

/**
 * A robust Image Picker launcher that:
 * 1. Requests necessary CAMERA & Storage Permissions reliably.
 * 2. Launches a unified native System Chooser (Camera + Gallery).
 * 3. Safely copies the resulting URI to local app storage to avoid all persistable URI / Google Photos crashes.
 */
@Composable
fun rememberImagePickerLauncher(onImageSelected: (Uri) -> Unit): () -> Unit {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    
    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            if (data != null) {
                // Copy selected gallery image to internal storage to avoid permission expiration
                val localUri = copyToLocalStorage(context, data)
                if (localUri != null) onImageSelected(localUri)
            } else if (tempUri != null) {
                // Copy camera image
                val localUri = copyToLocalStorage(context, tempUri!!)
                if (localUri != null) onImageSelected(localUri)
            }
        }
    }

    val launchPickerLogic = {
        val imagesDir = File(context.cacheDir, "images")
        imagesDir.mkdirs()
        val tempFile = File.createTempFile("camera_image_", ".jpg", imagesDir)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
        tempUri = uri

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val chooserIntent = Intent.createChooser(pickIntent, "Select Image").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
        }
        intentLauncher.launch(chooserIntent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // We launch the picker even if some are denied, because the system gallery selector 
        // on newer Android versions doesn't actually need READ_MEDIA_IMAGES. 
        // But doing this suppresses the dreaded "Can't load some photos" bug from Google Photos.
        launchPickerLogic()
    }

    return {
        val permissionsToRequest = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        val allGranted = permissionsToRequest.all { 
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED 
        }
        
        if (allGranted) {
            launchPickerLogic()
        } else {
           permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}

private fun copyToLocalStorage(context: Context, sourceUri: Uri): Uri? {
    return try {
        val imagesDir = File(context.filesDir, "saved_images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val destFile = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")
        
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Uri.fromFile(destFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


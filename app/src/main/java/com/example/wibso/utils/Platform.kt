package com.example.wibso.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.wibso.models.GalleryImage
import com.example.wibso.models.GalleryVideo

class Platform(private var context: Context, private var activity: Activity) {
    val name: String = "Android ${Build.VERSION.SDK_INT}"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun fetchImagesFromGallery(): List<GalleryImage> {
        val photos = mutableListOf<GalleryImage>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }


        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"


        context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val contentUri = cursor.getString(data)
                photos += GalleryImage(contentUri, name)
            }
        }
        println(photos)
        return photos
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkImagePermission(): Boolean {
        if (context.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1001
            )
            return false
        } else {
            return true
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkVideoPermission(): Boolean {
        if (context.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.READ_MEDIA_VIDEO), 1002
            )
            return false
        } else {
            return true
        }
    }

    fun fetchVideosFromGallery(): List<GalleryVideo> {
        val videoList = mutableListOf<GalleryVideo>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"
        val query = context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val contentUri = cursor.getString(dataColumn)
                videoList += GalleryVideo(contentUri, name, duration, size)
            }
        }
        println(videoList)
        return videoList
    }

    @Composable
    fun screenHeight(): Dp = LocalConfiguration.current.screenHeightDp.dp
}
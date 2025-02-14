package com.example.wibso.screens.chat

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import com.example.wibso.models.GalleryContent
import com.example.wibso.models.GalleryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ActionToolsViewModel : ViewModel() {

    private val _data = MutableStateFlow<List<GalleryContent>>(emptyList())
    val data: StateFlow<List<GalleryContent>> = _data.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var exoPlayer = MutableStateFlow<ExoPlayer?>(null)

    val player:  StateFlow<ExoPlayer?> = exoPlayer.asStateFlow()

    private var cacheAudio: File? = null

    val audio: File? get() = cacheAudio

    fun checkCameraPermission(
        context: Context,
        permission: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ): Boolean {
        val cameraXPermissions = arrayOf(
            CAMERA, RECORD_AUDIO
        )
        val hasRequiredPermissions = cameraXPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PERMISSION_GRANTED
        }
        if (hasRequiredPermissions) {
            return true
        } else {
            permission.launch(cameraXPermissions)
            return false
        }
    }

    fun checkAudioPermission(
        context: Context,
        permission: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ): Boolean {
        val audioPermissions = arrayOf(
            RECORD_AUDIO
        )
        val hasRequiredPermissions = audioPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PERMISSION_GRANTED
        }
        if (hasRequiredPermissions) {
            return true
        } else {
            permission.launch(audioPermissions)
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initRecorder(context: Context): MediaRecorder {
        return mediaRecorder ?: MediaRecorder(context)
    }

    private fun initPlayer(context: Context): ExoPlayer {
        exoPlayer.value = ExoPlayer.Builder(context).build()
        exoPlayer.value!!.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        println("play")

                        // Active playback.
                    } else {
                        println("stop")

                        // Not playing because playback is paused, ended, suppressed, or the player
                        // is buffering, stopped or failed. Check player.playWhenReady,
                        // player.playbackState, player.playbackSuppressionReason and
                        // player.playerError for details.
                    }
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    println(timeline.periodCount)
                    super.onTimelineChanged(timeline, reason)
                }

                override fun onTracksChanged(tracks: Tracks) {
                    println(tracks.toString())

                    super.onTracksChanged(tracks)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        println(exoPlayer.value!!.duration)
                    }
                    super.onPlaybackStateChanged(playbackState)
                }

                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {

                    super.onPlayWhenReadyChanged(playWhenReady, reason)
                }
            }
        )
        return exoPlayer.value as ExoPlayer
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun recordAudio(context: Context) {
        cacheAudio = File(context.cacheDir, "audio.acc")
        initRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setOutputFile(FileOutputStream(cacheAudio).fd)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
            start()
        }
    }

    fun stopRecord() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        println(cacheAudio?.path)
    }

    fun play(context: Context, uri: Uri) {
        println("Play: $uri")

        try {
            if(exoPlayer.value != null && exoPlayer.value?.isPlaying == true) {
                exoPlayer.value?.apply {
                    stop()
                    release()
                }
                exoPlayer.value = null
            }
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                val media = MediaItem.Builder().setMimeType(MimeTypes.AUDIO_AAC).setUri(uri).build()
                initPlayer(context)
                exoPlayer.value?.apply {
                    setMediaItem(media)
                    prepare()
                    play()
                }
            }
        } catch (e: IOException) {
            println("Play error: $e")
        }
    }


    @SuppressLint("RestrictedApi", "CoroutineCreationDuringComposition")
    fun checkAlbumPermission(
        context: Context,
        permission: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(
                context, READ_MEDIA_IMAGES
            ) == PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, READ_MEDIA_VIDEO
            ) == PERMISSION_GRANTED)
        ) {
            // Full access on Android 13 (API level 33) or higher
            CoroutineScope(Dispatchers.Main).launch {
                _data.value = listOf()
                fetchVideosFromGallery(context)
                fetchImagesFromGallery(context)
            }
            return true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
                context, READ_MEDIA_VISUAL_USER_SELECTED
            ) == PERMISSION_GRANTED
        ) {
            // Partial access on Android 14 (API level 34) or higher
            CoroutineScope(Dispatchers.Main).launch {
                _data.value = listOf()
                fetchVideosFromGallery(context)
                fetchImagesFromGallery(context)
            }
            return true
        } else if (ContextCompat.checkSelfPermission(
                context, READ_EXTERNAL_STORAGE
            ) == PERMISSION_GRANTED
        ) {
            // Full access up to Android 12 (API level 32)
            CoroutineScope(Dispatchers.Main).launch {
                _data.value = listOf()
                fetchVideosFromGallery(context)
                fetchImagesFromGallery(context)
            }
            return true
        } else {
            // Access denied
            _data.value = listOf()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permission.launch(
                    arrayOf(
                        READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED
                    )
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permission.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
            } else {
                permission.launch(arrayOf(READ_EXTERNAL_STORAGE))
            }
            return false
        }
    }

    suspend fun fetchImagesFromGallery(context: Context) = withContext(Dispatchers.IO) {
        val photos = mutableListOf<GalleryContent>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }


        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ALBUM,
            MediaStore.Images.Media.DATE_ADDED,
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"


        context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )?.use { cursor ->
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColum = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val albumColum = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ALBUM)
            val createdColum = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameColumn)
                val contentUri = cursor.getString(dataColum)
                val alb = cursor.getString(albumColum)
                val created = cursor.getString(createdColum)


                photos += GalleryContent(
                    uri = contentUri,
                    name = name,
                    album = alb,
                    type = GalleryType.IMAGE,
                    createdAt = Instant.fromEpochSeconds(created.toLong())
                )
            }
        }
        _data.value += photos
        _data.value = _data.value.sortedByDescending { it.createdAt }
    }

    suspend fun fetchVideosFromGallery(context: Context) = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<GalleryContent>()
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
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.DATE_ADDED,
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"
        val query = context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )
        query?.use { cursor ->
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val albColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)
            val createdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val contentUri = cursor.getString(dataColumn)
                val alb = cursor.getString(albColumn)
                val created = cursor.getString(createdColumn)

                videoList += GalleryContent(
                    uri = contentUri,
                    name = name,
                    duration = duration,
                    size = size,
                    album = alb,
                    createdAt = Instant.fromEpochSeconds(created.toLong()),
                    type = GalleryType.VIDEO
                )
            }
        }
        _data.value += videoList
        _data.value = _data.value.sortedByDescending { it.createdAt }
    }
}
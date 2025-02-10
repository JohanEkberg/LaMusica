package se.westpay.lamusica.repositories

import android.graphics.Bitmap
import android.net.Uri

data class AudioFileMetaData(
    val songUri: Uri?,
    val title: String = "",
    val album: String = "",
    val artist: String = "",
    val genre: String = "",
    val year: String = "",
    val format: String = "",
    val duration: String = "",
    val resolution: String = "",
    val size: Long = 0,
    val bitmap: Bitmap?,
    val albumUri: Uri?
)
package se.westpay.lamusica.models

import android.net.Uri
import androidx.room.*
import java.util.*


data class ArtistModel(
    val artistId: Long = 0,
    val artistName: String = "",
    val nbrOfAlbums: Long = 0
    )

data class AlbumModel(
    val albumId: Long = 0,
    val albumName: String?,
    val dirPath: Uri?,
    val year: String?,
    val nbrOfSongs: Int,
    val albumArt: String?,
    )

data class SongModel(
    val songId: Long = 0,
    val songName: String?,
    val songArtistId: Long,
    val songAlbumId: Long,
    val filePath: Uri?,
    val format: String?,
    val duration: String?,
    val resolution: String?,
    val size: Long?,
    var timestampDate: Date?
    )

data class AlbumSongs(
    val songList: List<SongModel>,
    val album: AlbumModel
    )

data class ArtistAlbums(
    val artist: ArtistModel,
    val albumList: List<AlbumSongs>
    )

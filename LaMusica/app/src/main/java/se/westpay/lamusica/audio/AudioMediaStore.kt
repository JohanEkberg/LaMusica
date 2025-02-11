//package se.westpay.lamusica.audio
//
//import android.content.ContentUris
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.ImageDecoder
//import android.net.Uri
//import android.os.Build
//import android.provider.BaseColumns
//import android.provider.MediaStore
//import android.util.Log
//import androidx.annotation.RequiresApi
//import se.westpay.lamusica.TAG
//import se.westpay.lamusica.models.AlbumModel
//import se.westpay.lamusica.models.AlbumSongs
//import se.westpay.lamusica.models.ArtistModel
//import se.westpay.lamusica.models.SongModel
//import java.io.FileNotFoundException
//import java.util.Date
//
//
//class AudioMediaStore(private val _context: Context) {
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    fun getAlbumArtWork(uri: Uri) : Bitmap? {
//        var bitmap: Bitmap? = null
//        try {
//            bitmap = if (Build.VERSION.SDK_INT <= 28) {
////                MediaStore.Images.Media.getBitmap(_context.contentResolver, uri)
////                val metaRetriever = MediaMetadataRetriever()
////                metaRetriever.setDataSource(_context, uri)
////                val imageData = metaRetriever.embeddedPicture
////                val artWork = imageData?.let { data -> BitmapFactory.decodeByteArray(data, 0, data.size) }
////                artWork
//                null
//            } else {
//                val source = ImageDecoder.createSource(_context.contentResolver, uri)
//                ImageDecoder.decodeBitmap(source)
//            }
//
//        } catch (e: FileNotFoundException) {
//            Log.w(TAG, "No artwork available!")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return bitmap
//    }
//
//    fun getArtist(artistId: Long) : ArtistModel {
//        var artistModel: ArtistModel = ArtistModel()
//        try {
////            var selection = "is_music != 0"
////
////            if (artistId > 0) {
////                selection = "$selection and artist_id = $artistId"
////            }
//
//            var selection = if (artistId > 0) {
//                "_id = $artistId"
//            } else {
//                ""
//            }
//
//            val collection =
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    MediaStore.Audio.Artists.getContentUri(
//                        MediaStore.VOLUME_EXTERNAL
//                    )
//                } else {
//                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
//                }
//
//            val projection = arrayOf(
//                MediaStore.Audio.Artists._ID,
//                MediaStore.Audio.Artists.ARTIST,
//                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
//            )
//
//            val sortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"
//
//            val query =  _context.contentResolver.query(
//                collection,
//                projection,
//                selection,
//                null,
//                sortOrder
//
//            )
//
//
//            query?.use { cursor ->
//                // Cache column indices.
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
//                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
//                val nbrOfAlbumsColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
//
//                while (cursor.moveToNext()) {
//                    // Get values of columns for a given video.
//                    val id = cursor.getLong(idColumn)
//                    val artist = cursor.getString(artistColumn)
//                    val nbrOfAlbums = cursor.getLong(nbrOfAlbumsColumn)
//
//                    val contentUri: Uri = ContentUris.withAppendedId(
//                        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
//                        id
//                    )
//
//                    artistModel = ArtistModel(artistId = id,
//                        artistName = artist,
//                        nbrOfAlbums = nbrOfAlbums
//                    )
//                }
//            }
//        } catch(e: Exception) {
//            Log.e(TAG, "Exception: Failed to get artist, ${e.message}")
//        }
//
//        return artistModel
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getPagedAlbumSongs(page: Int, size: Int) : List<AlbumSongs> {
//        val collection =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                MediaStore.Audio.Albums.getContentUri(
//                    MediaStore.VOLUME_EXTERNAL
//                )
//            } else {
//                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
//            }
//
//        val projection = arrayOf(
//            MediaStore.Audio.Albums._ID,
//            MediaStore.Audio.Albums.ALBUM,
//            MediaStore.Audio.Albums.ARTIST,
//            MediaStore.Audio.Albums.ALBUM_ART,
//            MediaStore.Audio.Albums.FIRST_YEAR,
//            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
//        )
//
////        val bundle = Bundle().apply {
////            putInt(ContentResolver.QUERY_ARG_LIMIT, size)
////            putInt(ContentResolver.QUERY_ARG_OFFSET, page)
////        }
////
////        val query =  _context.contentResolver.query(
////            collection,
////            projection,
////            bundle,
////            null
////        )
//
//        val sortOrder = String.format("%s limit %s offset %s", BaseColumns._ID, size.toString(), page.toString())
//
//        val query =  _context.contentResolver.query(
//            collection,
//            projection,
//            null,
//            null,
//            sortOrder
//        )
//
//        val albumSongList = mutableListOf<AlbumSongs>()
//
//        query?.use { cursor ->
//            // Cache column indices.
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
//            val albumColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
//            val albumArtColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)
//            val yearColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)
//            val nbrOfSongsColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
//
//            while (cursor.moveToNext()) {
//                // Get values of columns for a given video.
//                val id = cursor.getLong(idColumn)
//                val albumName = cursor.getString(albumColumn)
//                val albumArt = cursor.getString(albumArtColumn)
//                val year = cursor.getString(yearColumn)
//                val nbrOfSongs = cursor.getInt(nbrOfSongsColumn)
//
//                val contentUri: Uri = ContentUris.withAppendedId(
//                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                    id
//                )
//
//                val album = AlbumModel(albumId = id, albumName = albumName, dirPath = contentUri, year = year, nbrOfSongs = nbrOfSongs, albumArt=albumArt)
//                val songs = getSongs(id)
//                albumSongList.add(AlbumSongs(album = album, songList = songs))
//
//                Log.d(TAG, "AlbumId=${album.albumId} Album=${album.albumName} DirPath=${album.dirPath} Year=${album.year} AlbumArt=${album.albumArt} NbrOfSongs=${album.nbrOfSongs} SongsSize=${songs.size}")
//            }
//        }
//        return albumSongList
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getSongs(albumId: Long) : MutableList<SongModel> {
//        val songs  = mutableListOf<SongModel>()
//        var selection = "is_music != 0"
//
//        if (albumId > 0) {
//            selection = "$selection and album_id = $albumId"
//        }
//
//        val collection =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                MediaStore.Audio.Media.getContentUri(
//                    MediaStore.VOLUME_EXTERNAL
//                )
//            } else {
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//            }
//
//        val projection = arrayOf(
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.TITLE,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.ARTIST_ID,
//            MediaStore.Audio.Media.DISPLAY_NAME,
//            MediaStore.Audio.Media.DURATION,
//            MediaStore.Audio.Media.ALBUM,
//            MediaStore.Audio.Media.ALBUM_ID
//        )
//        val sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC"
//
//        val query =  _context.contentResolver.query(
//            collection,
//            projection,
//            selection,
//            null,
//            sortOrder
//
//        )
//
//        query?.use { cursor ->
//            // Cache column indices.
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
//            val albumIdColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
//            val artistIdColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
//            val durationColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//
//
//
//            while (cursor.moveToNext()) {
//                // Get values of columns for a given video.
//                val id = cursor.getLong(idColumn)
//                val albumId = cursor.getLong(albumIdColumn)
//                val artistId = cursor.getLong(artistIdColumn)
//                val title = cursor.getString(titleColumn)
//                val playTime = cursor.getString(durationColumn)
//
//                val contentUri: Uri = ContentUris.withAppendedId(
//                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                    id
//                )
//
//                val songModel = SongModel(songId = id,
//                    songName = title,
//                    filePath = contentUri,
//                    songAlbumId = albumId,
//                    songArtistId = artistId,
//                    duration = playTime,
//                    format = "",
//                    resolution = "",
//                    size = 0,
//                    timestampDate = Date()
//                )
//
//                songs.add(songModel)
//            }
//        }
//        return songs
//    }
//
////    fun getAllAudioFiles() : List<String>? {
////
//////        val path: String = "",
//////        val title: String = "",
//////        val album: String = "",
//////        val artist: String = "",
//////        val genre: String = "",
//////        val year: String = "",
//////        val format: String = "",
//////        val duration: String = "",
//////        val resolution: String = "",
//////        val size: Long = 0,
//////        val bitmap: Bitmap?
////
////        val songs  = mutableListOf<SongModel>()
////        var selection = "is_music != 0"
////
////        if (albumId > 0) {
////            selection = "$selection and album_id = $albumId"
////        }
////
////        val collection =
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////                MediaStore.Audio.Media.getContentUri(
////                    MediaStore.VOLUME_EXTERNAL
////                )
////            } else {
////                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
////            }
////
////        val projection = arrayOf(
////            MediaStore.Audio.Media._ID,
////            MediaStore.Audio.Media.TITLE,
////            MediaStore.Audio.Media.ARTIST,
////            MediaStore.Audio.Media.ARTIST_ID,
////            MediaStore.Audio.Media.GENRE,
////            MediaStore.Audio.Media.DISPLAY_NAME,
////            MediaStore.Audio.Media.DURATION,
////            MediaStore.Audio.Media.RESOLUTION,
////            MediaStore.Audio.Media.SIZE,
////            MediaStore.Audio.Media.ALBUM,
////            MediaStore.Audio.Media.ALBUM_ID,
////            MediaStore.Audio.Media.YEAR,
////            MediaStore.Audio.Media.
////        )
////        val sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC"
////
////        val query =  _context.contentResolver.query(
////            collection,
////            projection,
////            selection,
////            null,
////            sortOrder
////
////        )
////
////        query?.use { cursor ->
////            // Cache column indices.
////            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
////            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
////            val albumIdColumn =
////                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
////            val artistIdColumn =
////                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
////            val durationColumn =
////                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
////
////
////
////            while (cursor.moveToNext()) {
////                // Get values of columns for a given video.
////                val id = cursor.getLong(idColumn)
////                val albumId = cursor.getLong(albumIdColumn)
////                val artistId = cursor.getLong(artistIdColumn)
////                val title = cursor.getString(titleColumn)
////                val playTime = cursor.getString(durationColumn)
////
////                val contentUri: Uri = ContentUris.withAppendedId(
////                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
////                    id
////                )
////
////                val songModel = SongModel(songId = id,
////                    songName = title,
////                    filePath = contentUri,
////                    songAlbumId = albumId,
////                    songArtistId = artistId,
////                    duration = playTime,
////                    format = "",
////                    resolution = "",
////                    size = 0,
////                    timestampDate = Date()
////                )
////
////                songs.add(songModel)
////            }
////        }
////    }
//}
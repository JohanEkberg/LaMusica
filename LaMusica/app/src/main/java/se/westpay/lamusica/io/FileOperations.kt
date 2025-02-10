//package se.westpay.lamusica.io
//
//import android.graphics.BitmapFactory
//import android.media.MediaMetadataRetriever
//import android.net.Uri
//import android.util.Log
//import se.westpay.lamusica.TAG
//import se.westpay.lamusica.repositories.AudioFileMetaData
//import java.io.File
//import java.io.FileInputStream
//import java.io.IOException
//import java.nio.file.Files
//import java.nio.file.Path
//import java.util.stream.Collectors
//
//
//object FileOperations {
//
//    @Throws(IOException::class)
//    fun findFiles(path: Path, vararg fileExtension: String): List<String>? {
//        var result: List<String>? = null
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            require(Files.isDirectory(path)) { "Path must be a directory!" }
//            require(fileExtension.isNotEmpty()) { "No file extension provided!" }
//
//            Files.walk(path).use { walk ->
//                result = walk
//                    // this is a path, not string,
//                    // this only test if path end with a certain path
//                    .filter { p: Path? ->  !Files.isDirectory(p)  }
//                    // convert path to string first
////                    .map { p: Path -> p.toString().toLowerCase(Locale.getDefault()) }
//                    .map { p: Path -> p.toString() }
//                    .filter { f: String ->
//                        // Max two extensions are handled.
//                        if (fileExtension.size > 1) {
//                            f.endsWith(fileExtension[0]) || f.endsWith(fileExtension[1])
//                        } else {
//                            f.endsWith(fileExtension[0])
//                        }
//                    }
//                    .collect(Collectors.toList<Any>()) as List<String>
//            }
//        }
//        return result
//    }
//
//    fun filesInDirectory(path: Path) : Long {
//        var result: Long = 0
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            Files.walk(path).use { walk ->
//                result = walk
//                        .filter { p: Path? ->  !Files.isDirectory(p)  }
//                        .count()
//            }
//        }
//        return result
//    }
//
//    @Throws(IOException::class)
//    fun getAudioFileMetaData(filePath: String) : AudioFileMetaData? {
//        require(!filePath.isNullOrEmpty()) { "Path must be a file!" }
//        var metaRetriever: MediaMetadataRetriever? = null
//        try {
//            //Log.d(TAG, "File path: ${filePath}")
//            val fis = FileInputStream(File(filePath))
//            val fd = fis.fd
//            metaRetriever = MediaMetadataRetriever()
//            metaRetriever.setDataSource(fd)
//
//            val duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//            //Log.d(TAG, "Time: ${duration}")
//            val dur = duration?.toLong() ?: 0
//            val seconds = (dur % 60000 / 1000).toString()
//            val minutes = (dur / 60000).toString()
//            //Log.d(TAG, "Minutes: ${seconds} Seconds: ${minutes}")
//
//            val title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
//            val album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
//            val artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
//            val genre = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""
//            val year = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: ""
//            val bitsPerSample = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE) ?: ""
//            val size = File(filePath).length()
//            val imageData = metaRetriever.embeddedPicture
//            val artWork = imageData?.let { data -> BitmapFactory.decodeByteArray(data, 0, data.size) }
//            metaRetriever.release()
//            return AudioFileMetaData(
//                    songUri = filePath,
//                    title = title,
//                    album = album,
//                    artist = artist,
//                    genre = genre,
//                    year = year,
//                    format = File(filePath).extension,
//                    duration = duration ?: "",
//                    resolution = bitsPerSample,
//                    size = size,
//                    bitmap = artWork,
//                    albumUri =  Uri.parse("")  // Not used
//            )
//        } catch (e: Exception) {
//            Log.e(TAG, "Exceptrion: ${e.message}")
//            metaRetriever?.release()
//        }
//        return null
//    }
//
//    fun createDirectoryPaths(filePaths: List<String>) : MutableList<String> {
//        val dirPaths = mutableListOf<String>()
//        filePaths.forEach { path ->
//            val position = path.lastIndexOf(File.separator)
//            dirPaths.add(if(position > -1) {
//                path.substring(0, position)
//            } else {
//                path
//            })
//        }
//        return dirPaths
//    }
//
//    fun createDirectoryPath(path: String) : String {
//        val position = path.lastIndexOf(File.separator)
//        return if(position > -1) {
//            path.substring(0, position)
//        } else {
//            path
//        }
//    }
//}
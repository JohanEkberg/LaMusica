//package se.westpay.lamusica.io
//
//import android.os.Build
//import android.os.FileObserver
//import android.util.Log
//import androidx.annotation.RequiresApi
//import se.westpay.lamusica.TAG
//import java.io.File
//
//@RequiresApi(Build.VERSION_CODES.Q)
//class AudioFileObserver(val dataDir: File, mask: Int) : FileObserver(dataDir, mask) {
//    override fun onEvent(event: Int, file: String?) {
//        Log.d(TAG, "Directory Changed $file, event: $event")
//
//        // TODO: Check if audio file (mp3 or flac) extension
//
//        // TODO: Get audio meta data
//
//        // TODO: Store in data base
//
//    }
//}
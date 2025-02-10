package se.westpay.lamusica.ui.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.westpay.lamusica.TAG
import se.westpay.lamusica.audio.AudioScanner
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess

class SettingsViewModel : ViewModel() {
    private val _scanDone = MutableLiveData<Boolean>()
    val scanDone: LiveData<Boolean> = _scanDone

    fun doScan(context: Context) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val success = withContext(Dispatchers.IO) {
//                Log.i(TAG, "Starting scan...")
//                //AudioDataDatabaseAccess.clearAllTables(context)
//                AudioDataDatabaseAccess.deleteAllDataInDatabase(context)
//                val audioScanner = AudioScanner()
//                audioScanner.scanAudioFiles(context)
//            }
//            Log.i(TAG, "Scan finished, success = $success")
//            _scanDone.postValue(success)
//        }
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                Log.i(TAG, "Starting scan...")
                //AudioDataDatabaseAccess.clearAllTables(context)
                if (AudioDataDatabaseAccess.deleteAllDataInDatabase(context)) {
                    val audioScanner = AudioScanner()
                    audioScanner.scanAudioFiles(context)
                } else {
                    false
                }
            }
            Log.i(TAG, "Scan finished, success = $success")
            _scanDone.postValue(success)
        }
    }
}
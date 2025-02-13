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
    private val _artistAdded = MutableLiveData<String>()
    val artistAdded: LiveData<String> = _artistAdded

    fun doScan(context: Context) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                Log.i(TAG, "Starting scan...")
                //AudioDataDatabaseAccess.clearAllTables(context)
                if (AudioDataDatabaseAccess.deleteAllDataInDatabase(context)) {
                    val audioScanner = AudioScanner() { it ->
                        _artistAdded.postValue(it)
                    }
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
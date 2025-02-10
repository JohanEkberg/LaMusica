package se.westpay.lamusica.ui.startup

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess

class StartupViewModel : ViewModel() {

    private val _requireScan = MutableLiveData<Boolean>()
    val requireScan: LiveData<Boolean> = _requireScan

    fun startApplication(context: Context) {
        if (AudioDataDatabaseAccess.existDataBase(context)) {
            _requireScan.postValue(false)
        } else {
            _requireScan.postValue(true)
        }

//        if (shouldClearAllTables && AudioDataDatabaseAccess.existDataBase(context)) {
//            viewModelScope.launch(Dispatchers.Default) {
//                val shouldLoadFragment = withContext(Dispatchers.IO) {
//                    AudioDataDatabaseAccess.clearAllTables(context)
//                    val audioScanner = AudioScanner()
//                    audioScanner.scanAudioFiles(context)
//                }
//                if (shouldLoadFragment) {
//                    _requireScan.postValue(true)
//                }
//            }
//        } else if (!AudioDataDatabaseAccess.existDataBase(context)) { // If a database exist we don't auto scan for audio files.
//            viewModelScope.launch(Dispatchers.Default) {
//                val shouldLoadFragment = withContext(Dispatchers.IO) {
//                    AudioDataDatabaseAccess.clearAllTables(context)
//                    val audioScanner = AudioScanner()
//                    audioScanner.scanAudioFiles(context)
//                }
//                if (shouldLoadFragment) {
//                    _requireScan.postValue(true)
//                }
//
//            }
//        } else if (forceScan) {
//            viewModelScope.launch(Dispatchers.Default) {
//                val shouldLoadFragment = withContext(Dispatchers.IO) {
//                    val audioScanner = AudioScanner()
//                    audioScanner.scanAudioFiles(context)
//                }
//                if (shouldLoadFragment) {
//                    _requireScan.postValue(true)
//                }
//            }
//        }
    }
}
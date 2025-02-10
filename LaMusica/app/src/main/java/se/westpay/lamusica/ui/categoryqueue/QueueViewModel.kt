package se.westpay.lamusica.ui.categoryqueue

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import se.westpay.lamusica.TAG
import se.westpay.lamusica.audio.MusicPlayer
import se.westpay.lamusica.repositories.AudioFileMetaData

class QueueViewModel : ViewModel() {

    private val _audioFileMetaData = MutableLiveData<List<AudioFileMetaData>>()
    val audioFileMetaData: LiveData<List<AudioFileMetaData>> = _audioFileMetaData

    private val TIME_INTERVAL = 1000L
    private lateinit var _checkForQueueChangeJob: Job

    private var _currentQueueSize: Int = 0

    /**
     * Request update of songs list in the recycler adapter.
     * If the list needs update return true, otherwise false.
     */
    fun updateSongs() {
        // Only update the live data if the queue has changed
        if (MusicPlayer.queueSize() != _currentQueueSize) {
            _currentQueueSize = MusicPlayer.queueSize()
            _audioFileMetaData.value = MusicPlayer.getPlayerQueue()
        }
    }

    fun removeSong(audioFile: AudioFileMetaData) {
        Log.d(TAG, "removeSong:  ${audioFile.title}")
        MusicPlayer.removeFromPlayerQueue(audioFile)
    }

    fun startCheckForQueueChange() {
        _checkForQueueChangeJob = startRepeatingJob(TIME_INTERVAL)
    }

    suspend fun stopCheckForQueueChange() {
        _checkForQueueChangeJob.cancelAndJoin()
    }

    private fun startRepeatingJob(timeInterval: Long): Job {
        return viewModelScope.launch {
            while (isActive) {
                if (MusicPlayer.queueSize() != _currentQueueSize) {
                    _currentQueueSize = MusicPlayer.queueSize()
                    _audioFileMetaData.postValue(MusicPlayer.getPlayerQueue())
                }
                delay(timeInterval)
            }
        }
    }
}
package se.westpay.lamusica.audio

import android.content.Context
import se.westpay.lamusica.repositories.AudioFileMetaData

interface IMusicPlayer {
    fun play(context: Context)
    fun pause()
    fun skip(context: Context)
    fun stop()
    fun addToPlayerQueue(song: AudioFileMetaData)
    fun removeFromPlayerQueue(song: AudioFileMetaData)
    fun getCurrentPlayingSong() : AudioFileMetaData?
    fun getPlayerQueue() : List<AudioFileMetaData>
    fun addSongChangeCallback(songChangeCallback: (AudioFileMetaData?) -> Unit)
    fun addProgressCallback(progressCallback: (Int) -> Unit)
}
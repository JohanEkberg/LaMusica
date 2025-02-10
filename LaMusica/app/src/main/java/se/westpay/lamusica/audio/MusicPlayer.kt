package se.westpay.lamusica.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import se.westpay.lamusica.TAG
import se.westpay.lamusica.repositories.AudioFileMetaData


object MusicPlayer : IMusicPlayer {
    private var _mediaPlayer: MediaPlayer? = null
    private val _handler = Handler(Looper.getMainLooper())
    private var _songQueue: SongQueue = SongQueue()
    private lateinit var _songChangeCallback: (AudioFileMetaData?) -> Unit
    private lateinit var _progressCallback: (Int) -> Unit
    private var _currentSong: AudioFileMetaData? = null
    private var _initDone = false

    @Synchronized
    private fun getMediaPlayerInstance() : MediaPlayer? {
        return if (_mediaPlayer != null) {
            _mediaPlayer
        } else {
            _mediaPlayer = MediaPlayer()
            _mediaPlayer
        }
    }

    private fun clearMediaPlayer() {
        _initDone = false
        getMediaPlayerInstance()?.release()
        _mediaPlayer = null
        _currentSong = null
        _songChangeCallback(null)
    }

    @Synchronized
    fun isActive() : Boolean = _mediaPlayer != null

    override fun addSongChangeCallback(songChangeCallback: (AudioFileMetaData?) -> Unit) {
        _songChangeCallback = songChangeCallback
    }

    override fun addProgressCallback(progressCallback: (Int) -> Unit) {
        _progressCallback = progressCallback
    }

    @Synchronized
    override fun getPlayerQueue() : List<AudioFileMetaData> {
        Log.d(TAG, "MusicPlayer getPlayerQueue")
        return _songQueue.getQueueItems()
    }

    override fun getCurrentSong()  = _currentSong

    @Synchronized
    override fun addToPlayerQueue(song: AudioFileMetaData) {
        Log.d(TAG, "MusicPlayer addToPlayerQueue ${song.title}")
        // If queue is empty, we start by updating the UI
        if (_songQueue.isEmpty() && _currentSong == null) {
            _songChangeCallback(song)
        }
        _songQueue.addQueueItem(song)
    }

    @Synchronized
    override fun removeFromPlayerQueue(song: AudioFileMetaData) {
        Log.d(TAG, "MusicPlayer removeFromPlayerQueue ${song.title}")
        _songQueue.removeQueueItem(song)
    }

    @Synchronized
    fun queueSize() : Int = _songQueue.queueSize()

    private fun getItemFromQueue() : AudioFileMetaData? = _songQueue.getQueueItem()

    private fun setupAudioErrorCallback() {
        try {
            getMediaPlayerInstance()?.setOnErrorListener { _, _, _ ->
                Log.i(TAG, "Player error callback")
                return@setOnErrorListener true
            }
        } catch(e: Exception) {
            Log.e(TAG, "Completion failed, exception: ${e.message}")
        }
    }

    private fun setupAudioCompletionCallback(context: Context) {
        try {
            // Song has completed callback
            getMediaPlayerInstance()?.setOnCompletionListener {
                Log.i(TAG, "Song finished callback")
                _currentSong = null
                _progressCallback(100)
                playSongFromQueue(context)
            }
        } catch(e: Exception) {
            Log.e(TAG, "Completion failed, exception: ${e.message}")
        }
    }

    private fun playSongFromQueue(context: Context) {
        try {
            if (!_songQueue.isEmpty()) {
                Log.i(TAG, "Play from queue")
                _currentSong = getItemFromQueue()
                Log.i(TAG, "New song to play: ${_currentSong?.artist} - ${_currentSong?.title}")
                _currentSong?.songUri?.let { songNotNull ->
                    if (playSong(context, songNotNull)) {
                        _songChangeCallback(_currentSong)
                    }
                }
            } else if (_currentSong != null) {
                Log.i(TAG, "Queue is empty, play current song")
                _currentSong?.songUri?.let { songNotNull ->
                    if (playSong(context, songNotNull)) {
                        _songChangeCallback(_currentSong)
                    }
                }
            } else {
                Log.w(TAG, "Queue is empty!")
//                _songChangeCallback(null)
//                _initDone = false
//                getMediaPlayerInstance()?.release()
                clearMediaPlayer()
            }
        } catch(e: Exception) {
            Log.e(TAG, "Completion failed, exception: ${e.message}")
        }
    }

    private fun playSong(context: Context, songUri: Uri) : Boolean {
        var success = false
        try {
            //Log.i(TAG, "Play new song ${songUri}")
            getMediaPlayerInstance()?.apply {
                reset()
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, songUri)
                prepare()
                start()
            } ?: Log.e(TAG, "Failed to start player!")
            _handler.postDelayed(UpdateSongTime,100)
            success = true
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return success
    }

    override fun skip(context: Context) {
        try {
            Log.i(TAG, "Stop current song from playing")
            val player = getMediaPlayerInstance()
            val isPlaying = player?.isPlaying == true
            player?.stop()
            if (isPlaying && queueSize() > 0) {
                playSongFromQueue(context)
            } else {
                // Remove one item from the queue,
                // if the queue is empty report empty queue.
                _initDone = false
                _currentSong = getItemFromQueue()
                if (_currentSong == null && queueSize() == 0) {
                    Log.w(TAG, "Queue is empty!")
                    _songChangeCallback(null)
                } else {
                    _songChangeCallback(_currentSong)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        try {
            _handler.removeCallbacks(UpdateSongTime)
            getMediaPlayerInstance()?.stop()
            clearMediaPlayer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun play(context: Context) {
        try {
            if (!_initDone) {
                setupAudioErrorCallback()
                setupAudioCompletionCallback(context)
                _initDone = true
                playSongFromQueue(context)
            } else {
                Log.i(TAG, "Resume current song")
                getMediaPlayerInstance()?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    override fun pause() {
        try {
            Log.i(TAG, "Pause current song")
            getMediaPlayerInstance()?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val UpdateSongTime: Runnable = object : Runnable {
        override fun run() {
            if (_initDone) {
                val currentPosition = getMediaPlayerInstance()?.currentPosition?.toFloat() ?: 0F
                val duration = getMediaPlayerInstance()?.duration?.toFloat() ?: 0F
                val progress: Float = (currentPosition / duration) * 100

                _progressCallback(progress.toInt())
                if (progress < 100) {
                    _handler.postDelayed(this, 100)
                }
            } else {
                _progressCallback(0)
            }
        }
    }
}
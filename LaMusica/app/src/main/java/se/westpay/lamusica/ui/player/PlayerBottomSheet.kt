package se.westpay.lamusica.ui.player

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.audio.MusicPlayer
import se.westpay.lamusica.lyricsservice.LyricsService
import se.westpay.lamusica.lyricsservice.LyricsWithTimestamp
import se.westpay.lamusica.ui.categoryalbum.AlbumFragment
import se.westpay.lamusica.ui.categoryartist.ArtistFragment


class PlayerBottomSheet(layout: ConstraintLayout, private val _playerBottomSheetListener: IPlayerBottomSheet?) {
    private val _bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> =
            BottomSheetBehavior.from(layout)
    private lateinit var _playButtonImageView: ImageView
    private lateinit var _playNextButtonImageView: ImageView
    private lateinit var _lyricsButtonImageView: ImageView
    private val _lyricsData = MutableLiveData<LyricsWithTimestamp?>()
    val lyricsData: LiveData<LyricsWithTimestamp?> = _lyricsData

    init {
        _bottomSheetBehavior.isHideable = true
    }

    fun setupListener() {
        _bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        onStateExpanded(bottomSheet)
                    } BottomSheetBehavior.STATE_HIDDEN -> {
                        onStateHidden(bottomSheet)
                    } else -> {
                        Log.w(TAG, "Unhandled state = ${newState}")
                    }
                }
            }
        })

        // Add song callback
        MusicPlayer.addSongChangeCallback { audioItem ->
            _playerBottomSheetListener?.onSongChanged(
                artist = audioItem?.artist ?: "",
                title = audioItem?.title ?: "",
                albumUri = audioItem?.albumUri
            ) ?: Log.e(TAG, "No player bottom sheet listener registered")
        }

        // Register progress callback
        MusicPlayer.addProgressCallback { progress ->
            _playerBottomSheetListener?.onSongProgress(progress)
                ?: Log.e(TAG, "No player bottom sheet listener registered")
        }

        // Disable draggable bottomsheet
        // Currently the expanded bottomsheet is not used hence below code
        _bottomSheetBehavior.isDraggable = false
    }

    fun shoudBeVisible() : Boolean = MusicPlayer.isActive()

    fun hide() {
        _bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun show(activity: MainActivity) {
        _bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        setupButtonListener(activity)
    }

    private fun setupButtonListener(activity: MainActivity) {
        try {
            // Add play button listener,
            // tag == 0 means switch to pause button next time the user presses the play button
            // tag == 1 means switch to the play button next time the user presses the pauses button
            _playButtonImageView = activity.findViewById<ImageView>(R.id.playerPlayButton)
            if (!_playButtonImageView.hasOnClickListeners()) {
                _playButtonImageView.tag = 0
                _playButtonImageView.setOnClickListener {
                    val buttonState = _playButtonImageView.tag == 1
                    managePlayButtonState(activity, buttonState)
                }
            }

            // Add play next button listener
            _playNextButtonImageView = activity.findViewById<ImageView>(R.id.playerPlayNext)
            if (!_playNextButtonImageView.hasOnClickListeners()) {
                _playNextButtonImageView.setOnClickListener {
                    MusicPlayer.skip(activity)
                }
            }

            _lyricsButtonImageView = activity.findViewById<ImageView>(R.id.lyricsButton)
            if (!_lyricsButtonImageView.hasOnClickListeners()) {
                _lyricsButtonImageView.setOnClickListener {
                    val song = MusicPlayer.getCurrentPlayingSong()
                    song?.let { songNotNull ->
                        CoroutineScope(Dispatchers.Default).launch {
                            try {
                                val lyrics = LyricsService.getLyricsWithTimestamp(songNotNull.artist, songNotNull.title)
                                _lyricsData.postValue(lyrics)
                            } catch(e: Exception) {
                                Log.e(TAG, "Failed to show lyrics, exception: ${e.message}")
                            }
                        }
                    }
                }
            }
        } catch(e: Exception) {
            Log.e(TAG, "Failed to setup button listeners, exception: ${e.message}")
        }
    }

    private fun managePlayButtonState(activity: MainActivity, playButtonIconState: Boolean) {
        if (this::_playButtonImageView.isInitialized) {
            if (playButtonIconState) {
                _playButtonImageView.setImageResource(R.drawable.ic_play_circle)
                _playButtonImageView.tag = 0
                MusicPlayer.pause()
            } else {
                _playButtonImageView.setImageResource(R.drawable.ic_pause_circle)
                _playButtonImageView.tag = 1
                MusicPlayer.play(activity)
            }
        }
    }

    private fun adjustRecyclerViewPadding(activity: MainActivity, bottomSheet: View, isExpanded: Boolean) {
        activity.getFirstVisibleFragment(listOf(AlbumFragment.FRAGMENT_NAME, ArtistFragment.FRAGMENT_NAME) )?.let { fragment ->
            val recyclerView = when(fragment) {
                is AlbumFragment -> {
                    fragment.getRecycleView()
                }
                is ArtistFragment -> {
                    fragment.getRecycleView()
                }
                else -> {
                    null
                }
            }
            recyclerView?.let { recyclerViewNotNull ->
                if (isExpanded) {
                    // Set bottom padding when BottomSheet is visible
                    recyclerViewNotNull.setPadding(0, 0, 0, bottomSheet.height)
                } else {
                    // Reset padding when BottomSheet is hidden
                    recyclerViewNotNull.setPadding(0, 0, 0, 0)
                }
            } ?: Log.e(TAG, "Failed to obtain the recyclerview")
        }
    }

    private fun onStateExpanded(bottomSheet: View) {
        adjustRecyclerViewPadding(bottomSheet.context as MainActivity, bottomSheet, true)
    }

    private fun onStateHidden(bottomSheet: View) {
        adjustRecyclerViewPadding(bottomSheet.context as MainActivity, bottomSheet, false)
    }

    fun getAlbumArtwork(resolver: ContentResolver, contentUri: Uri) : Bitmap? {
        return try {
            resolver.loadThumbnail(contentUri, Size(640, 480), null)
        } catch(e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            null
        }
    }
}
package se.westpay.lamusica.ui.lyrics

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.lifecycle.ViewModel
import se.westpay.lamusica.TAG
import se.westpay.lamusica.utilities.LrcEntry
import se.westpay.lamusica.utilities.countAllCharacters
import se.westpay.lamusica.utilities.parseLRC


class LyricsViewModel(plainLyrics: String, syncedLyrics: String, duration: Int) : ViewModel() {
    private val _plainLyrics = plainLyrics
    private val _syncedLyrics = syncedLyrics

    /**
     * The max progress is 100, this mean that the interval in milliseconds between each
     * progress is (duration/100) * 1000.
     */
    private val _updateInterval: Long = duration * 10L

    fun getSpannableText(progressValue: Int, spanColor: ForegroundColorSpan) : Spannable? {
        return try {
            val highWaterMark: Long = progressValue * _updateInterval
            val linesWithTimestamp: List<LrcEntry> = parseLRC(_syncedLyrics)
            val filteredLines = linesWithTimestamp.filter { it.timeStamp < highWaterMark }
            val numberOfCharacters = filteredLines.map { it.line }.countAllCharacters()

            val spannable = SpannableString(_plainLyrics)

            spannable.setSpan(
                spanColor,
                0,
                numberOfCharacters,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            spannable
        } catch(e: Exception) {
            Log.e(TAG, "Failed to set spannable text, exception: ${e.message}")
            null
        }
    }
}
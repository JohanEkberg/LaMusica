package se.westpay.lamusica

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import se.westpay.lamusica.ui.lyrics.LyricsViewModel

@RunWith(AndroidJUnit4::class)
class LyricsViewModelTest {

    private val _plainLyrics = "Line one\nLine two\nLine three\nLine four\nLine five\nLine six\nLine seven\nLine eight\nLine nine\nLine ten\n"
    private val _syncedLyrics =
        "[00:01.12] Line one\n" +
        "[00:30.31] Line two\n" +
        "[00:55.72] Line three\n" +
        "[01:01.72] Line four\n" +
        "[01:10.72] Line five\n" +
        "[01:40.72] Line six\n" +
        "[02:10.72] Line seven\n" +
        "[02:25.72] Line eight\n" +
        "[02:55.72] Line nine\n" +
        "[03:33.33] Line ten"
    private val _duration = 200

    @Test
    fun getSpannableTextTest() {
        runBlocking {
            val expectedEndValue = 16
            val lyricsViewModel = LyricsViewModel(
                plainLyrics = _plainLyrics,
                syncedLyrics = _syncedLyrics,
                duration = _duration
            )
            val spanColor = ForegroundColorSpan(Color.BLUE)
            val spannableText = lyricsViewModel.getSpannableText(20, spanColor)
            val end = spannableText?.getSpanEnd(spanColor)

            if (spannableText != null && end == expectedEndValue) {
                assertTrue(true)
            } else {
                assertTrue(false)
            }
        }
    }
}
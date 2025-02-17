package se.westpay.lamusica

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import se.westpay.lamusica.utilities.parseLRC

@RunWith(AndroidJUnit4::class)
class LrcFormatUtilityTest {

    private val expectedLength = 10
    private val _lrcText =
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

    @Test
    fun parseLrcTest() {
        val parsed = parseLRC(_lrcText)

        if (parsed.size == expectedLength) {
            assertTrue(true)
        } else {
            assertTrue(false)
        }
    }
}
package se.westpay.lamusica

import org.junit.Test

import org.junit.Assert.*
import se.westpay.lamusica.audio.ArtistItem
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun find_in_list() {

        val myList = listOf<String>("test 1", "test 2", "test 3", "test 1")
        val result = myList.find { item -> "test 4".equals(item, ignoreCase = true) }

//        var nbr = 0
//        val actual = if (myList.find { item -> "test 1".equals(item, ignoreCase = true) } == null) {
//            ++nbr
//        } else {
//            0
//        }
        assertEquals(2, result)
    }
}
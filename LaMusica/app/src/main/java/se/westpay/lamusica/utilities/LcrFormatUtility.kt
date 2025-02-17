package se.westpay.lamusica.utilities

/**
 * Parse string in LRC format.
 *
 * @param lrcText
 * @return List<LrcEntry>
 */
fun parseLRC(lrcText: String): List<LrcEntry> {
    val pattern = Regex("\\[(\\d+):(\\d+\\.\\d+)](.*)")
    val lyricsList = mutableListOf<LrcEntry>()

    lrcText.lines().forEach { line ->
        val match = pattern.find(line)
        if (match != null) {
            val (minutes, seconds, lyric) = match.destructured
            val timestamp = (minutes.toLong() * 60 + seconds.toDouble()).toLong() * 1000  // Convert to milliseconds
            lyricsList.add(LrcEntry(timestamp, lyric.trim()))
        }
    }
    return lyricsList
}

data class LrcEntry(val timeStamp: Long, val line: String)
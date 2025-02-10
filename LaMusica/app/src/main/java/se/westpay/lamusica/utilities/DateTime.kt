package se.westpay.lamusica.utilities

object DateTime {
    fun getFormattedDurationTime(duration: String) : String {
        return  if (duration.isNotEmpty()) {
            val dur = duration.toLong()
            var seconds = (dur % 60000 / 1000).toString()
            if (seconds.length < 2) {
                seconds += "0"
            }
            val minutes = (dur / 60000).toString()
            "${minutes}:${seconds}"
        } else {
            ""
        }
    }
}
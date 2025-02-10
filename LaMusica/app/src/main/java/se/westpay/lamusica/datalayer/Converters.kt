package se.westpay.lamusica.datalayer

import androidx.room.TypeConverter
import java.util.*

/**
 * Timestamp converters.
 */
class Converters {

    /**
     * Convert timestamp from Long to Date.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Convert timestamp from Date to Long.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
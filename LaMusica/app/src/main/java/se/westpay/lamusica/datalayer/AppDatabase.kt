package se.westpay.lamusica.datalayer

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import se.westpay.lamusica.TAG
import java.io.File
import java.io.FileReader

@Database(entities = arrayOf(ArtistEntity::class, AlbumEntity::class, SongEntity::class), version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audioDataDao(): AudioDataDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : AppDatabase {
            return if (instance == null) {
                /**
                 * Check if database file is valid when creating database instance.
                 */
                if (!isValidFile(context)) {
                    Log.e(TAG, "Invalid database file detected, recreating database!")
                }
                instance = createInstance(context)
                instance!!
            } else {
                instance!!
            }
        }

        @Synchronized
        fun destroyInstance() {
            if (instance?.isOpen == true) {
                instance?.close()
                instance = null
            }
        }

        /**
         * Validate the database file.
         */
        fun isValidFile(context: Context): Boolean {
            val sqliteMagicString = "SQLite format 3\u0000"
            val directory = File(context.applicationInfo.dataDir, "databases")
            val file = File(directory, "artist.db")
            return if (!file.exists() || !file.canRead()) {
                false
            } else try {
                val fr = FileReader(file)
                val buffer = CharArray(16)
                fr.read(buffer, 0, 16)
                val str = String(buffer)
                fr.close()
                str == sqliteMagicString
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        /**
         * Migration script from version 1 to version 2.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transaction_items ADD COLUMN reportdone INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * Create instance of the database.
         * If the database file is somehow corrupt or version mismatch
         * we silently create a new database, this is done by using
         * the fallbackToDestructiveMigration function.
         */
//        private fun createInstance(context: Context) : AppDatabase {
//            return Room.databaseBuilder(
//                context,
//                AppDatabase::class.java, "artist.db"
//            ).addMigrations(MIGRATION_1_2).fallbackToDestructiveMigration().build()
//        }
        private fun createInstance(context: Context) : AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "artist.db"
            ).fallbackToDestructiveMigration().build()
        }
    }
}
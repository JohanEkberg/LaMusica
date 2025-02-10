package se.westpay.lamusica.datalayer

import androidx.room.*
import java.util.*

interface DataBaseEntity

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val artistId: Long = 0,

    @ColumnInfo(name = "artist_name")
    val artistName: String?,
) : DataBaseEntity

@Entity(tableName = "albums")
data class AlbumEntity(
        @PrimaryKey(autoGenerate = true)
        val albumId: Long = 0,

        @ColumnInfo(name = "album_name")
        val albumName: String?,

        @ColumnInfo(name = "album_uri")
        val albumUri: String?,

        @ColumnInfo(name = "year")
        val year: String?,

        @ColumnInfo(name = "genre")
        val genre: String?
) : DataBaseEntity

@Entity(tableName = "songs",
        indices = [Index("song_name", "album_id", unique = true)])
data class SongEntity(
        @PrimaryKey(autoGenerate = true)
        val songId: Long = 0,

        @ColumnInfo(name = "song_name")
        val songName: String?,

        @ColumnInfo(name = "artist_id")
        val songArtistId: Long?,

        @ColumnInfo(name = "album_id")
        val songAlbumId: Long?,

        @ColumnInfo(name = "song_uri")
        val songUri: String?,

        @ColumnInfo(name = "album_uri")
        val albumUri: String?,

        @ColumnInfo(name = "format")
        val format: String?,

        @ColumnInfo(name = "duration")
        val duration: String?,

        @ColumnInfo(name = "resolution")
        val resolution: String?,

        @ColumnInfo(name = "size")
        val size: Long?,

        @ColumnInfo(name = "timestamp")
        var timestampDate: Date?
) : DataBaseEntity

data class SongWithArtist(
        @Embedded val songEntity: SongEntity,
        @Relation(
                entity = ArtistEntity::class,
                parentColumn = "artist_id",
                entityColumn = "artistId"
        )
        val artist: ArtistEntity
)

data class AlbumWithSongs(
        @Embedded val albumEntity: AlbumEntity,
        @Relation(
                entity = SongEntity::class,
                parentColumn = "albumId",
                entityColumn = "album_id"
        )
        val songList: List<SongWithArtist>
)

data class ArtistWithSongs(
        @Embedded val artistEntity: ArtistEntity,
        @Relation(
                entity = SongEntity::class,
                parentColumn = "artistId",
                entityColumn = "artist_id"
        )
        val songList: List<SongEntity>
)

data class AlbumWithSongs2(
        @Embedded val songEntity: SongEntity,
        @Relation(
                entity = AlbumEntity::class,
                parentColumn = "album_id",
                entityColumn = "albumId"
        )
        val album: AlbumEntity
)

data class ArtistWithAlbums(
        @Embedded val artistEntity: ArtistEntity,
        @Relation(
                entity = SongEntity::class,
                parentColumn = "artistId",
                entityColumn = "artist_id"
        )
        val songList: List<AlbumWithSongs2>
)
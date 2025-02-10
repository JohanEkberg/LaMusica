package se.westpay.lamusica.datalayer

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface AudioDataDao {

//    @Query("SELECT * FROM artists")
//    fun getAll(): MutableList<AudioDataEntity>

    @Query("SELECT * FROM albums ORDER BY album_name")
    fun getPagedAlbums(): PagingSource<Int, AlbumEntity>

    @Query("SELECT * FROM artists ORDER BY artist_name")
    fun getPagedArtists(): PagingSource<Int, ArtistEntity>

    @Query("SELECT * FROM artists ORDER BY artist_name")
    fun getPagedArtistsWithSongs(): PagingSource<Int, ArtistWithSongs>

    @Query("SELECT * FROM artists WHERE artistId IN (:id)")
    fun getArtistById(id: Int): ArtistEntity

    @Transaction
    @Query("SELECT * FROM albums ORDER BY album_name")
    fun getPagedAlbumWithSongs(): PagingSource<Int, AlbumWithSongs>

//    @Transaction
//    @Query("SELECT * FROM albums WHERE album_name")
//    fun getAlbumWithSongs(album: String, artist: String): AlbumWithSongs

//    @Query("SELECT * FROM artists WHERE id IN (:ids)")
//    fun loadAllByIds(ids: IntArray): List<Artist>
//

    @Transaction
    @Query("SELECT * FROM artists")
    fun getArtists(): List<ArtistEntity>

    @Transaction
    @Query("SELECT * FROM artists WHERE artist_name LIKE :name")
    fun getArtistByName(name: String): List<ArtistEntity>

    @Query("SELECT * FROM artists WHERE artistId LIKE :id")
    fun getArtistWithSongs(id: Long): ArtistWithSongs

    @Transaction
    @Query("SELECT * FROM albums WHERE albumId LIKE :id")
    fun getAlbumWithSongs(id: Long): AlbumWithSongs

    @Transaction
    @Query("SELECT * FROM artists WHERE artistId LIKE :id")
    fun getArtistWithAlbums(id: Long): ArtistWithAlbums

    @Query("SELECT * FROM albums WHERE albumId IN (:id)")
    fun getAlbumById(id: Int): AlbumEntity

    @Transaction
    @Query("SELECT * FROM albums WHERE album_name LIKE :name")
    fun getAlbumByName(name: String): List<AlbumEntity>

    @Transaction
    @Query("SELECT * FROM songs WHERE song_name LIKE :name")
    fun getSongByName(name: String): List<SongEntity>

    @Transaction
    @Query("SELECT * FROM songs ")
    fun getAllSongs(): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(artistEntity: ArtistEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(albumEntity: AlbumEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg songEntity: SongEntity)

    /**
     * Delete all albums in the database.
     */
    @Query("DELETE FROM albums")
    fun deleteAlbums()

    /**
     * Delete all artists in the database.
     */
    @Query("DELETE FROM artists")
    fun deleteArtists()

    /**
     * Delete all songs in the database.
     */
    @Query("DELETE FROM songs")
    fun deleteSongs()

    @Transaction
    fun deleteAllData() {
        deleteAlbums()
        deleteArtists()
        deleteSongs()
    }
}
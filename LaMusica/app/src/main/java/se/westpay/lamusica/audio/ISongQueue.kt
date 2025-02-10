package se.westpay.lamusica.audio

import se.westpay.lamusica.repositories.AudioFileMetaData

interface ISongQueue {
    fun getQueueItem() : AudioFileMetaData?
    fun getQueueItems() : List<AudioFileMetaData>
    fun addQueueItem(song: AudioFileMetaData)
    fun removeQueueItem(song: AudioFileMetaData)
    fun clearQueue() : Boolean
    fun queueSize() : Int
    fun isEmpty() : Boolean
    fun isEqual(songQueue: List<AudioFileMetaData>) : Boolean
}
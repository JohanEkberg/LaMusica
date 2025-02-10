package se.westpay.lamusica.audio

import android.util.Log
import se.westpay.lamusica.TAG
import se.westpay.lamusica.repositories.AudioFileMetaData
import java.util.LinkedList
import java.util.Queue

class SongQueue : ISongQueue {
    companion object {
        val CLASS_NAME = "${SongQueue::class.simpleName} ${Integer.toHexString(hashCode())}"
    }

    private var _queue: Queue<AudioFileMetaData> = LinkedList<AudioFileMetaData>()

    override fun getQueueItem(): AudioFileMetaData? {
        return try {
            Log.i(TAG, "${CLASS_NAME} getQueueItem")
            _queue.poll()
        } catch(e: Exception) {
            Log.e(TAG, "${CLASS_NAME} Failed to get queue item, exception: ${e.message}")
            null
        }
    }

    override fun getQueueItems(): List<AudioFileMetaData> {
        return try {
            Log.i(TAG, "${CLASS_NAME} getQueueItems")
            val queue: List<AudioFileMetaData> = LinkedList(_queue.map { it.copy() })
            queue
        } catch(e: Exception) {
            Log.e(TAG, "${CLASS_NAME} Failed to get queue items, exception: ${e.message}")
            emptyList<AudioFileMetaData>()
        }
    }

    override fun addQueueItem(song: AudioFileMetaData) {
        try {
            Log.i(TAG, "${CLASS_NAME} addQueueItem")
            _queue.add(song)
        } catch(e: Exception) {
            Log.e(TAG, "${CLASS_NAME} Failed to add item to queue, exception: ${e.message}")
        }
    }

    override fun removeQueueItem(song: AudioFileMetaData) {
        try {
            Log.i(TAG, "${CLASS_NAME} removeQueueItem")
            _queue.remove(song)
        } catch(e: Exception) {
            Log.e(TAG, "${CLASS_NAME} Failed to remove item to queue, exception: ${e.message}")
        }
    }

    override fun clearQueue(): Boolean {
        return try {
            Log.i(TAG, "${CLASS_NAME} isEmpty")
            _queue.clear()
            true
        } catch(e: Exception) {
            Log.e(TAG, "${CLASS_NAME} Failed to check if queue is empty, exception: ${e.message}")
            false
        }
    }

    override fun queueSize(): Int {
        return try {
            Log.i(TAG, "${CLASS_NAME} queueSize")
            _queue.size
        } catch(e: Exception) {
            Log.e(TAG, "${CLASS_NAME} Failed to get queue size, exception: ${e.message}")
            0
        }
    }

    override fun isEmpty(): Boolean {
        return try {
            Log.i(TAG, "${CLASS_NAME} isEmpty")
            _queue.isEmpty()
        } catch(e: Exception) {
            Log.e(TAG, "${CLASS_NAME} Failed to check if queue is empty, exception: ${e.message}")
            true
        }
    }

    override fun isEqual(songQueue: List<AudioFileMetaData>): Boolean {
        return try {
            songQueue.zip(_queue.toList()).all { (a, b) -> a == b }
        } catch(e: Exception) {
            Log.e(TAG, "${CLASS_NAME} Failed to check if queue is equal, exception: ${e.message}")
            true
        }
    }
}
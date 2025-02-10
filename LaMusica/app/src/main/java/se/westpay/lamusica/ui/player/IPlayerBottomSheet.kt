package se.westpay.lamusica.ui.player

import android.net.Uri

interface IPlayerBottomSheet {
    fun onSongChanged(artist: String, title: String, albumUri: Uri?)
    fun onSongProgress(progress: Int)
}
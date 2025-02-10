package se.westpay.lamusica.repositories

import java.math.BigDecimal

data class Song(val artist: String, val songName: String, val duration: BigDecimal)

package se.westpay.lamusica.utilities

fun List<String>.countAllCharacters() : Int {
    return this.sumOf { it.length }
}
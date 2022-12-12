import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun IntRange.fullyContains(other: IntRange): Boolean = first <= other.first && last >= other.last
fun IntRange.contains(other: IntRange): Boolean = contains(other.first) || contains(other.last)

typealias Vec2 = Pair<Int, Int>

// cursed length, but just for Day09 we need to know if any component is >0
val Vec2.size get() = abs(first) + abs(second)

// Pythagoras rounded down
fun Vec2.distanceTo(other: Vec2) = (this - other).let { (x, y) ->
    sqrt(x.square().toFloat() + y.square().toFloat()).toInt()
}

fun Int.square() = this * this

operator fun Vec2.minus(other: Vec2) = (first - other.first) to (second - other.second)
operator fun Vec2.plus(other: Vec2) = (first + other.first) to (second + other.second)

fun Vec2.normalize() = first.sign to second.sign
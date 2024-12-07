import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.pow

/**
 * Reads lines from the given input txt file.
 */
fun readInputString(name: String) = Path("src/$name.txt").readText().trim()
fun readInputLines(name: String) = readInputString(name).lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
fun Any?.println(tag: String) {
    print("$tag: ")
    println(this)
}

fun <T> List<T>.copyOf() : List<T> = mutableListOf<T>().also { it.addAll(this) }
fun <T> List<T>.mutableCopyOf(): MutableList<T> = mutableListOf<T>().also { it.addAll(this) }

fun Int.pow(n: Int): Int = n.toDouble().pow(this).toInt()

fun <T> T.checkResult(expected: T) {
    if (this != expected) {
        throw IllegalStateException("Check failed - Result should be equal to $expected but was $this")
    }
}
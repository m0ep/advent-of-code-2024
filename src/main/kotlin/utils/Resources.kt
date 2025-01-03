package utils

import java.nio.charset.Charset


inline fun <reified T> T.resourceAsText(
    name: String,
    charset: Charset = Charsets.UTF_8
): String = T::class.java.getResource(name)?.readText(charset) ?: ""

inline fun <reified T> T.resourceAsLines(
    name: String,
    charset: Charset = Charsets.UTF_8
): List<String> = T::class.java.getResourceAsStream(name)
        ?.bufferedReader(charset)
        ?.readLines()
        ?: listOf()

inline fun <reified T> T.resourceAsListOfInts(
    name: String
): List<Int> = this.resourceAsLines(name).filter(String::isNotBlank).map(String::toInt)

inline fun <reified T> T.resourceAsListOfUInts(
    name: String
): List<UInt> = this.resourceAsLines(name).filter(String::isNotBlank).map(String::toUInt)

inline fun <reified T> T.resourceAsListOfLong(
    name: String
): List<Long> = this.resourceAsLines(name).filter(String::isNotBlank).map(String::toLong)

inline fun <reified T> T.resourceAsListOfULong(
    name: String
): List<ULong> = this.resourceAsLines(name).filter(String::isNotBlank).map(String::toULong)





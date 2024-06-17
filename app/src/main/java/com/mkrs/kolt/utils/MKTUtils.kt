package com.mkrs.kolt.utils

import android.text.Editable

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 13 / 06 / 2024
 *****/
fun isCode(word: String): Boolean {
    val regex = "^[A-Za-z]{1,2}[\\d]{5,6}\$".toRegex()
    return regex.matches(word)
}

fun isCodeFill(word: String): Boolean {
    val regex = "^[A-Za-z]{1,2}[\\d]*\$".toRegex()
    return regex.matches(word)
}

fun isLetter(word: String): Boolean {
    val regex = "^[A-Za-z]*$".toRegex()
    return regex.matches(word)
}

fun isDigit(word: String): Boolean {
    val regex = "^[\\d]{7}$".toRegex()
    return regex.matches(word)
}

fun isDigitGeneric(word: String): Boolean {
    val regex = "^[\\d.\\d]*$".toRegex()
    return regex.matches(word)
}

fun emptyStringEditable(): Editable = "".toEditable()
fun emptyString(): String = ""


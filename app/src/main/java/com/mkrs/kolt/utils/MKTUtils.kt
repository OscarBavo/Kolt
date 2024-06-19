package com.mkrs.kolt.utils

import android.text.Editable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 05 / 06 / 2024
 *****/

fun <T> parse(jsonSrt: String?, type: Type): T {
    return (GsonBuilder().registerTypeAdapterFactory(PostProcessingEnabler())
        .create()).fromJson(jsonSrt, type)
}

fun toJsonString(initial: Any): String {
    return Gson().toJson(initial)
}

fun getType(container: Class<*>, entity: Class<*>): Type {
    return object : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(entity)
        }

        override fun getRawType(): Type {
            return container
        }

        override fun getOwnerType(): Type? {
            return null
        }
    }

}

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


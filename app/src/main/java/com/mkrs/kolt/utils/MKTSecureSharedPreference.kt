package com.mkrs.kolt.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 03 / 05 / 2024
 *****/
class MKTSecureSharedPreference private constructor(context: Context, fileName: String) {
    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreference.edit()

    companion object {
        @JvmStatic
        fun newInstance(context: Context, fileName: String) =
            MKTSecureSharedPreference(context, fileName)
    }

    private fun encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(), Base64.NO_WRAP)
    }

    private fun decode(input: String): String {
        return String((Base64.decode(input, Base64.NO_WRAP)))
    }

    fun clear() {
        editor.clear()
        editor.apply()
    }

    fun saveString(key: String, value: String) {
        editor.putString(encode(key), encode(value))
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        val encoded = sharedPreference.getString(encode(key), encode(defaultValue))
        return decode(encoded ?: encode(defaultValue))
    }
}
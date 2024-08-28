package com.mkrs.kolt.preferences.domain

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.preferences.domain
 * Date: 06 / 05 / 2024
 *****/
interface PreferenceRepository {
    fun saveStringValue(key: String, value: String)

    fun getStringValue(key: String, defaultValue: String = ""): String

    fun saveIntValue(key: String, value: Int)

    fun getIntValue(key: String, defaultValue: Int = 0): Int
}
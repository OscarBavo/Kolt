package com.mkrs.kolt.preferences.data

import com.mkrs.kolt.preferences.domain.PreferenceRepository
import com.mkrs.kolt.utils.MKTSecureSharedPreference

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.preferences.data
 * Date: 06 / 05 / 2024
 *****/
class PreferenceRepositoryImp(private val preference: MKTSecureSharedPreference) :
    PreferenceRepository {
    override fun saveStringValue(key: String, value: String) {
        preference.saveString(key, value)
    }

    override fun getStringValue(key: String, defaultValue: String) =
        preference.getString(key, defaultValue)


    override fun saveIntValue(key: String, value: Int) {
        preference.saveInt(key, value)
    }

    override fun getIntValue(key: String, defaultValue: Int) = preference.getInt(key, defaultValue)
}
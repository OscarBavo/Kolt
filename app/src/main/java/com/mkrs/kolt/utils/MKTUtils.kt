package com.mkrs.kolt.utils

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
class MKTUtils {
    fun <T> parse(jsonSrt: String, destination: Class<T>): T {
        return (GsonBuilder().registerTypeAdapterFactory(PostProcessingEnabler())
            .create()).fromJson(jsonSrt, destination)
    }

    fun <T> parse(jsonSrt: String, type: Type): T {
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
}
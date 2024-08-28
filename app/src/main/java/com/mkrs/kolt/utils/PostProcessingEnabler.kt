package com.mkrs.kolt.utils

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 05 / 06 / 2024
 *****/

interface PostProcessable{
    fun gsonPostProcess()
}
class PostProcessingEnabler:TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T> {
        val delegate= gson?.getDelegateAdapter(this, type)

        return object :TypeAdapter<T>(){
            @Throws(IOException::class)
            override fun write(out: JsonWriter?, value: T) {
                delegate?.write(out, value)
            }

            @Throws(IOException::class)
            override fun read(readJson: JsonReader?): T? {
                val obj= delegate?.read(readJson)
                if(obj is PostProcessable){
                    (obj as PostProcessable).gsonPostProcess()
                }
                return  obj
            }
        }
    }
}
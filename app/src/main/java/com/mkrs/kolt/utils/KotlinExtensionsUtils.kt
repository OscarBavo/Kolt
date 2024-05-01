package com.mkrs.kolt.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mkrs.kolt.R
import kotlin.math.roundToInt

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 30 / 04 / 2024
 *****/

fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun View.hideKeyboard() {
    this.let { v ->
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun Double.float(): Float {
    val slideValue = (this / 10).roundToInt()
    return slideValue.toFloat()
}

fun ImageView.glide(url: String, context: Context) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .circleCrop()
        .into(this)
}

fun ImageView.glideCorner(url: String, context: Context) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .into(this)
}


fun getMsgErrorByCode(errorCode: Int): Int =
    when (errorCode) {
        else -> R.string.error_generic
    }


fun isNetworkConnected(context: Context): Boolean {
    //1
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //2
    val activeNetwork = connectivityManager.activeNetwork
    //3
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    //4
    return networkCapabilities != null &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
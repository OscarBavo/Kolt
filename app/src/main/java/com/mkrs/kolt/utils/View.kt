package com.mkrs.kolt.utils

import android.text.Editable
import android.view.View

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 06 / 05 / 2024
 *****/
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.enableOrDisable(expression: () -> Boolean) {
    isEnabled = expression()
}

fun View.visibleOrInvisible(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

fun View.visibleOrInvisible(expression: () -> Boolean) {
    visibility = if (expression()) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

fun View.visibleOrGone(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.visibleOrGone(expression: () -> Boolean) {
    visibility = if (expression()) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.disable() {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
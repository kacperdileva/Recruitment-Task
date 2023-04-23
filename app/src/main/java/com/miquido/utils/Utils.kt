package com.miquido.utils

import android.view.View
import android.view.animation.AlphaAnimation

inline fun View.fadeIn(durationMillis: Long = 1000) {
    this.startAnimation(AlphaAnimation(0F, 1F).apply {
        duration = durationMillis
        fillAfter = true
    })
}

inline fun View.fadeOut(durationMillis: Long = 1000) {
    this.startAnimation(AlphaAnimation(1F, 0F).apply {
        duration = durationMillis
        fillAfter = true
    })}
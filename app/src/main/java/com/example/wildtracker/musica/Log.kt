package com.example.wildtracker.musica

import android.util.Log

object Log {
    const val LOG_TAG = "mPlayerLog"

    fun l(string: String?) {
        Log.v(LOG_TAG, string!!)
    }

    fun e(string: String?) {
        Log.e(LOG_TAG, string!!)
    }
}
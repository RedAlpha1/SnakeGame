package com.learning.codingchallenge.utils

import android.content.Context
import android.content.SharedPreferences
import java.lang.Exception

object PreferenceUtils {
    var sharedPreferences: SharedPreferences? = null
    private const val PREF_NAME = "saved_user_data"
    private var editor: SharedPreferences.Editor? = null

    private fun getPrefs(context: Context): SharedPreferences? {
        try {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, 0)
            editor = sharedPreferences!!.edit()
        } catch (exception: Exception) {
            exception.message
        }
        return sharedPreferences
    }

    fun insertData(context: Context, key: String?, value: String?) {
        val editor = getPrefs(context)!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun retrieveData(context: Context, key: String?): String? {
        return getPrefs(context)!!.getString(key, "")
    }

    fun checkContains(ctx: Context, key: String?): Boolean {
        return getPrefs(ctx)!!.contains(key)
    }
}
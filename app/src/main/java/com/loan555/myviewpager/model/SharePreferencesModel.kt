package com.loan555.myviewpager.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private const val NAME = "SpinKotlin"
    private const val MODE = Context.MODE_PRIVATE
    lateinit var preferences: SharedPreferences

    private val PASSWORD = Pair("password", "")
    private val USER_NAME = Pair("name", "")
    private val START_DAY = Pair("startDay", 0)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    var password: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(PASSWORD.first, PASSWORD.second)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            this.putString(PASSWORD.first, value)
        }
    var userName: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(USER_NAME.first, USER_NAME.second)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            this.putString(USER_NAME.first, value)
        }
    var startDayOfWeek: Int
        get() = preferences.getInt(START_DAY.first, START_DAY.second)
        set(value) = preferences.edit {
            this.putInt(START_DAY.first, value)
        }
}
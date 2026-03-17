package com.carcollector.session

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("carcollector_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}

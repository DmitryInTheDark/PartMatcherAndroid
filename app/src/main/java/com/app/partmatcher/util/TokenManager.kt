package com.app.partmatcher.util

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun saveUser(id: Long, name: String, roles: Set<String>) {
        prefs.edit()
            .putLong("user_id", id)
            .putString("user_name", name)
            .putStringSet("user_roles", roles)
            .apply()
    }

    fun getUserId(): Long {
        return prefs.getLong("user_id", -1L)
    }

    fun getUserName(): String? {
        return prefs.getString("user_name", null)
    }

    fun getRoles(): Set<String> {
        return prefs.getStringSet("user_roles", emptySet()) ?: emptySet()
    }

    fun clearToken() {
        prefs.edit().clear().apply()
    }
}

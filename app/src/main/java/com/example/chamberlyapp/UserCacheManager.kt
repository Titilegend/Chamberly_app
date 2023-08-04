package com.example.chamberlyapp

import android.content.Context
import android.content.SharedPreferences



class UserCacheManager private constructor(context:Context) {
    private val USER_CACHE_PREFS = "user_cache_prefs"
    private val KEY_UID = "uid"
    private val KEY_DISPLAY_NAME = "display_name"
    private val KEY_EMAIL = "email"
    private val KEY_PLATFORM = "platform"

    private lateinit var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(USER_CACHE_PREFS, Context.MODE_PRIVATE)
    }

    companion object {
        private lateinit var instance: UserCacheManager

        fun initialize(context: Context) {
            instance = UserCacheManager(context)
        }

        fun getInstance(): UserCacheManager {
            if (!::instance.isInitialized) {
                throw Exception("UserCacheManager is not initialized. Call initialize() first.")
            }
            return instance
        }
    }

    fun cacheUserData(uid: String, displayName: String, email: String, platform: String) {
        sharedPreferences.edit().apply {
            putString(KEY_UID, uid)
            putString(KEY_DISPLAY_NAME, displayName)
            putString(KEY_EMAIL, email)
            putString(KEY_PLATFORM, platform)
            apply()
        }
    }

    fun getUID(): String? {
        return sharedPreferences.getString(KEY_UID, null)
    }

    fun getDisplayName(): String? {
        return sharedPreferences.getString(KEY_DISPLAY_NAME, null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun getPlatform(): String? {
        return sharedPreferences.getString(KEY_PLATFORM, null)
    }

    fun clearCache() {
        sharedPreferences.edit().clear().apply()
    }
}
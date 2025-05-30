package com.example.lendit.data.local.managers

import android.content.Context
import android.content.SharedPreferences
import com.example.lendit.data.repository.RepositoryProvider
import com.example.lendit.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PremiumManager(
    context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    private val userEmail: String = sharedPref.getString("email", "") ?: ""

    private val userRepository: UserRepository = RepositoryProvider.getUserRepository(context)

    fun isUserPremium(): Boolean {
        return sharedPref.getBoolean("isPremium", false)
    }

    suspend fun setPremiumStatus(isPremium: Boolean) {
        withContext(Dispatchers.IO) {
            userRepository.updateUserStatus(userEmail, isPremium, null)
            sharedPref.edit().putBoolean("isPremium", isPremium).apply()
        }
    }

    fun getUserEmail(): String = userEmail
}

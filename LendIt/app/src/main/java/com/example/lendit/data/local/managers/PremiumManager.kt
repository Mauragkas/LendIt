package com.example.lendit.data.local.managers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.lendit.data.repository.RepositoryProvider
import com.example.lendit.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * Manager class for handling premium subscription operations
 */
class PremiumManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onStatusUpdated: (Boolean) -> Unit,
    private val onError: (String) -> Unit
) {
    private val userRepository: UserRepository by lazy {
        RepositoryProvider.getUserRepository(context)
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    // Plan details
    data class PremiumPlan(val name: String, val price: Double, val description: String)

    val availablePlans = listOf(
        PremiumPlan("monthly", 9.99, "Monthly subscription"),
        PremiumPlan("quarterly", 24.99, "Quarterly subscription (save 16%)"),
        PremiumPlan("yearly", 89.99, "Yearly subscription (save 25%)")
    )

    private var selectedPlan: PremiumPlan? = null

    /**
     * Gets the user's current premium status
     */
    fun isPremiumUser(): Boolean {
        return sharedPreferences.getBoolean("isPremium", false)
    }

    /**
     * Gets the user's current plan
     */
    fun getCurrentPlan(): String? {
        return sharedPreferences.getString("premiumPlan", null)
    }

    /**
     * Gets the user's email
     */
    fun getUserEmail(): String {
        return sharedPreferences.getString("email", "") ?: ""
    }

    /**
     * Selects a premium plan
     */
    fun selectPlan(planName: String): PremiumPlan? {
        selectedPlan = availablePlans.find { it.name == planName }
        return selectedPlan
    }

    /**
     * Gets the currently selected plan
     */
    fun getSelectedPlan(): PremiumPlan? = selectedPlan

    /**
     * Updates the user's premium status
     */
    fun updatePremiumStatus(hasPremium: Boolean, planName: String? = null) {
        val userEmail = getUserEmail()

        if (userEmail.isEmpty()) {
            onError("User email not found")
            return
        }

        coroutineScope.launch {
            try {
                // Update premium status in database
                userRepository.updateUserStatus(userEmail, hasPremium, planName)

                // Update shared preferences
                withContext(Dispatchers.Main) {
                    with(sharedPreferences.edit()) {
                        putBoolean("isPremium", hasPremium)
                        if (planName != null) {
                            putString("premiumPlan", planName)
                        } else if (!hasPremium) {
                            remove("premiumPlan")
                        }
                        apply()
                    }

                    onStatusUpdated(hasPremium)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("PremiumManager", ("Error updating premium status"))
                }
            }
        }
    }

    /**
     * Activates premium subscription with the selected plan
     */
    fun activatePremium() {
        val plan = selectedPlan
        if (plan == null) {
            onError("No plan selected")
            return
        }

        updatePremiumStatus(true, plan.name)
    }

    /**
     * Cancels the premium subscription
     */
    fun cancelPremium() {
        updatePremiumStatus(false, null)
    }
}

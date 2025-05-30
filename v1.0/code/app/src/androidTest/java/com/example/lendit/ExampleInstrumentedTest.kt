package com.example.lendit

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginButtonDisabledInitially() {
        // Check that login button is disabled on startup
        onView(withId(R.id.button))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun emailAndPasswordInput_enableLoginButton() {
        // Type valid email and password
        onView(withId(R.id.editTextTextEmailAddress))
            .perform(typeText("test@example.com"), closeSoftKeyboard())

        onView(withId(R.id.editTextTextPassword))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Now login button should be enabled
        onView(withId(R.id.button))
            .check(matches(isEnabled()))
    }

    @Test
    fun validRenterCredentials_loginSuccess_navigateToNextActivity() {
        Intents.init()
        // Type valid email and password
        onView(withId(R.id.editTextTextEmailAddress))
            .perform(typeText("paflou@renter.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextTextPassword))
            .perform(typeText("123"), closeSoftKeyboard())

        // Click login button
        onView(withId(R.id.button)).perform(click())

        // Check if MainActivity (or the intended next activity) is launched.
        // Using Intents.intended to verify the navigation.
        Intents.intended(hasComponent(MainActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun validOwnerCredentials_loginSuccess_navigateToNextActivity() {
        Intents.init()
        // Type valid email and password
        onView(withId(R.id.editTextTextEmailAddress))
            .perform(typeText("mavragas@owner.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextTextPassword))
            .perform(typeText("123"), closeSoftKeyboard())

        // Click login button
        onView(withId(R.id.button)).perform(click())

        // Check if MainOwnerActivity (or the intended next activity) is launched.
        // Using Intents.intended to verify the navigation.
        Intents.intended(hasComponent(MainOwnerActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun validAdminCredentials_loginSuccess_navigateToNextActivity() {
        Intents.init()
        // Type valid email and password
        onView(withId(R.id.editTextTextEmailAddress))
            .perform(typeText("natalia@admin.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextTextPassword))
            .perform(typeText("123"), closeSoftKeyboard())

        // Click login button
        onView(withId(R.id.button)).perform(click())

        // Check if AdminActivity (or the intended next activity) is launched.
        // Using Intents.intended to verify the navigation.
        Intents.intended(hasComponent(AdminActivity::class.java.name))
        Intents.release()
    }
}

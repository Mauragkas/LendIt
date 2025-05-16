// LendIt/app/src/main/java/com/example/lendit/models/User.kt
package com.example.lendit.models

data class User(
    val userId: String,
    var name: String,
    var email: String,
    var password: String,
    var phoneNumber: String,
    var location: String
)

data class Renter(
    val user: User,
    val favorites: MutableList<EquipmentListing> = mutableListOf()
)

data class Owner(
    val user: User,
    var premiumStatus: Boolean = false,
    var ratings: Float = 0f
)

data class Coordinator(
    val user: User,
    val staffId: String
)

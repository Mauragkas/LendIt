package com.example.lendit.data.repository

import UserDao
import UserEntity

class UserRepository(private val userDao: UserDao) {

    /* ──────────────────────────────── CREATE ─────────────────────────────── */

    suspend fun insertUser(user: UserEntity) =
        userDao.insert(user)

    suspend fun insertUsers(users: List<UserEntity>) =
        userDao.insertAll(users)

    /* ──────────────────────────────── READ ──────────────────────────────── */

    suspend fun getAllUsers(): List<UserEntity> =
        userDao.getAllUsers()

    suspend fun getUserCount(): Int =
        userDao.getUserNumber()

    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity? =
        userDao.getUserByEmailAndPassword(email, password)

    suspend fun getUserByEmail(email: String): UserEntity? =
        userDao.getUserByEmail(email)

    /* ─────────────────────────────── UPDATE ─────────────────────────────── */

    suspend fun updateUserStatus(email: String, premiumStatus: Boolean, premiumPlan: String?) =
        userDao.updateUserStatus(email, premiumStatus, premiumPlan)

    suspend fun updateUser(user: UserEntity) =
        userDao.updateUser(user)

    /* ─────────────────────────────── DELETE ─────────────────────────────── */

    suspend fun deleteAllUsers() =
        userDao.deleteAllUsers()
}

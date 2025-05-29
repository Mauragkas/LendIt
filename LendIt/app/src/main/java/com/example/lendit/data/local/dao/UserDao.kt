import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert
    suspend fun insert(userEntity: UserEntity)

    @Insert
    suspend fun insertAll(users: List<UserEntity>)

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT COUNT(*) FROM user")
    suspend fun getUserNumber(): Int

    @Query("SELECT * FROM user WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("UPDATE user SET premiumStatus = :premiumStatus, premiumPlan = :premiumPlan WHERE email = :email")
    suspend fun updateUserStatus(email: String, premiumStatus: Boolean, premiumPlan: String?)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insert(userEntity: UserEntity)

    @Insert
    suspend fun insertAll(users: List<UserEntity>)

    @Query("SELECT * FROM user")  // match your @Entity(tableName = "users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM user WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?


    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}

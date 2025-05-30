import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lendit.data.local.entities.UserCart

@Dao
interface CartDao {
    @Insert
    suspend fun insert(userCart: UserCart): Long

    @Query("SELECT EXISTS(SELECT 1 FROM UserCart WHERE userId = :id AND listingId = :listingId)")
    suspend fun checkCart(id: Int, listingId: Int): Boolean

    @Query("DELETE FROM UserCart WHERE userId = :id AND listingId = :listingId")
    suspend fun deleteFromCart(id: Int, listingId: Int)

    @Query("DELETE FROM UserCart WHERE userId = :id ")
    suspend fun deleteCart(id: Int)

    @Query("SELECT L.* FROM UserCart AS C INNER JOIN listing AS L ON L.listingId = C.listingId WHERE userId = :id")
    suspend fun getCartById(id: Int): List<EquipmentListing>

    @Query("SELECT COUNT(*) FROM UserCart WHERE userId = :id ")
    suspend fun getCartCount(id: Int): Int
}
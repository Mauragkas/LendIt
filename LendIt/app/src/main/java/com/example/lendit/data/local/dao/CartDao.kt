import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.lendit.data.local.entities.UserCart

@Dao
interface CartDao {
    @Insert
    suspend fun insert(userCart: UserCart): Long

    @Query("DELETE FROM UserCart WHERE userId = :id ")
    suspend fun deleteCart(id: Int)

    @Query("SELECT L.* FROM UserCart AS C INNER JOIN listing AS L ON L.listingId = C.listingId WHERE userId = :id")
    suspend fun getCartById(id: Int): List<EquipmentListing>

    @Query("SELECT COUNT(*) FROM UserCart WHERE userId = :id ")
    suspend fun getCartCount(id: Int): Int
}
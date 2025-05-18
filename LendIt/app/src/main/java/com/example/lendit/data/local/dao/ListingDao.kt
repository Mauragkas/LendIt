import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ListingDao {

    @Insert
    suspend fun insert(listingEntity: ListingEntity)

    @Insert
    suspend fun insertAll(listings: List<ListingEntity>)

    @Query("SELECT * FROM listing")
    suspend fun getAllListings(): List<ListingEntity>

    @Query("DELETE FROM listing")
    suspend fun deleteAllListings()
}

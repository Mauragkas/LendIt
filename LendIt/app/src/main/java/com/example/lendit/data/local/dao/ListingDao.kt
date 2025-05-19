import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

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

    @RawQuery
    suspend fun getListings(query: SupportSQLiteQuery): List<ListingEntity>

}
fun buildQuery(f: ListingFilters): SupportSQLiteQuery {
    val queryBuilder = StringBuilder("SELECT * FROM listing WHERE 1=1")
    val args = mutableListOf<Any>()

    f.title?.let {
        queryBuilder.append(" AND (title LIKE ? OR description LIKE ?)")
        args.add("%$it%")
        args.add("%$it%")
    }

    f.location?.let {
        queryBuilder.append(" AND location = ?")
        args.add(it.name)  // convert enum to string
    }

    f.minPrice?.let {
        queryBuilder.append(" AND price >= ?")
        args.add(it)
    }
    f.category?.let {
        queryBuilder.append(" AND category = ?")
        args.add(it.name)
    }
    f.maxPrice?.let {
        queryBuilder.append(" AND price <= ?")
        args.add(it)
    }

    if (f.availableFrom != null && f.availableUntil != null) {
        queryBuilder.append(" AND availableFrom <= ? AND availableUntil >= ?")
        args.add(f.availableFrom)   // filter start date
        args.add(f.availableUntil)  // filter end date
    }


    return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
}



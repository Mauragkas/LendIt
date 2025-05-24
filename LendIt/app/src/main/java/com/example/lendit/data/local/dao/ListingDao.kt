import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.util.query
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface ListingDao {

    @Insert
    suspend fun insert(equipmentListing: EquipmentListing): Long

    @Insert
    suspend fun insertAll(listings: List<EquipmentListing>)

    @Query("SELECT * FROM listing")
    suspend fun getAllListings(): List<EquipmentListing>

    @Query("DELETE FROM listing")
    suspend fun deleteAllListings()

    @Query("SELECT COUNT(*) FROM listing")
    suspend fun getCount(): Int

    @RawQuery
    suspend fun getListings(query: SupportSQLiteQuery): List<EquipmentListing>

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

    when (f.sortBy ?: SortBy.SUGGESTED) {
        SortBy.ASC -> queryBuilder.append(" ORDER BY price ASC")
        SortBy.DESC -> queryBuilder.append(" ORDER BY price DESC")
        SortBy.SUGGESTED -> queryBuilder.append(" ORDER BY creationDate DESC")
    }
    Log.d("SQL_QUERY", "Query: ${queryBuilder} | Args: ${args.joinToString()}")
    return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
}

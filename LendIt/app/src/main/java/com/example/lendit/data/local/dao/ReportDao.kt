import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lendit.data.local.entities.Report

@Dao
interface ReportDao {
    @Insert
    suspend fun insert(report: Report): Long

    @Query("SELECT * FROM report WHERE listingId = :listingId")
    suspend fun getReportsForListing(listingId: Int): List<Report>

    @Query("SELECT * FROM report")
    suspend fun getAllReports(): List<Report>
}

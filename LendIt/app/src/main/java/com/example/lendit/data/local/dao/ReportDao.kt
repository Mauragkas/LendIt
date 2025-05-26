import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lendit.data.local.entities.Report

@Dao
interface ReportDao {
    @Insert
    suspend fun insert(report: Report): Long

    @Update
    suspend fun updateReport(report: Report)

    @Query("SELECT * FROM report WHERE listingId = :listingId")
    suspend fun getReportsForListing(listingId: Int): List<Report>

    @Query("SELECT * FROM report")
    suspend fun getAllReports(): List<Report>
    
    @Query("SELECT * FROM report WHERE status = :status")
    suspend fun getReportsByStatus(status: String): List<Report>

    @Query("DELETE FROM report WHERE reportId = :reportId")
    suspend fun deleteReport(reportId: Int)
    
    @Query("SELECT * FROM report WHERE reportId = :reportId LIMIT 1")
    suspend fun getReportById(reportId: Int): Report?
}

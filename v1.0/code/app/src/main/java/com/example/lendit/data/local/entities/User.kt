
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val userId: Int,
    var name: String,
    val email: String,
    var password: String,
    var phoneNumber: String,
    var location: String,
    val userType: String,  // "Renter", "Owner", or "Coordinator"

    // Owner fields
    var premiumStatus: Boolean? = null,
    var premiumPlan: String? = null, // depreciated
    var ratings: Float? = null,

    // Coordinator field
    val staffId: String? = null,
)

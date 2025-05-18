
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val userId: String,
    var name: String,
    val email: String,
    var password: String,
    var phoneNumber: String,
    var location: String,
    val userType: String,  // "Renter", "Owner", or "Coordinator"

    // Owner fields
    var premiumStatus: Boolean? = null,
    var ratings: Float? = null,

    // Coordinator field
    val staffId: String? = null,

    // Renter favorites stored as JSON string
    var favoritesJson: String? = null
)
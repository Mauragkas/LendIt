
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
    var premiumPlan: String? = null,
    var ratings: Float? = null,

    // Coordinator field
    val staffId: String? = null,
)

class UserClass(
    val userId: Int,
    var name: String,
    val email: String,
    var password: String,
    var phoneNumber: String,
    var location: String,
    val userType: String,  // "Renter", "Owner", or "Coordinator"

    // Owner fields
    var premiumStatus: Boolean? = null,
    var premiumPlan: String? = null,
    var ratings: Float? = null,

    // Coordinator field
    val staffId: String? = null,
) {
    fun UserEntity.toDomainModel() = UserClass(
        userId = userId,
        name = name,
        email = email,
        password = password,
        phoneNumber = phoneNumber,
        location = location,
        userType = userType,
        premiumStatus = premiumStatus,
        premiumPlan = premiumPlan,
        ratings = ratings,
        staffId = staffId
    )

    fun UserClass.toEntity() = UserEntity(
        userId = userId,
        name = name,
        email = email,
        password = password,
        phoneNumber = phoneNumber,
        location = location,
        userType = userType,
        premiumStatus = premiumStatus,
        premiumPlan = premiumPlan,
        ratings = ratings,
        staffId = staffId
    )

    // You can add methods here if needed, e.g. for updating info, validation, etc.
}

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class ListingStatus {
    AVAILABLE,
    UNAVAILABLE,
    INACTIVE
}
enum class ListingCategory {
    MANUAL,
    ELECTRIC
}
enum class Region {
    ATTICA,
    CENTRAL_MACEDONIA,
    EASTERN_MACEDONIA_AND_THRACE,
    EPIRUS,
    THESSALY,
    WESTERN_MACEDONIA,
    WESTERN_GREECE,
    CENTRAL_GREECE,
    PELOPONNESE,
    IONIAN_ISLANDS,
    NORTH_AEGEAN,
    SOUTH_AEGEAN,
    CRETE
}

@Parcelize
data class ListingFilters(
    val title: String?            = null,
    val description: String?      = null,
    var category: ListingCategory? = null,
    val location: Region?         = null,
    val minPrice: Double?         = null,
    val maxPrice: Double?         = null,
    val availableFrom: Int?  = null,
    val availableUntil: Int?  = null
): Parcelable




@Entity(tableName = "listing")
data class ListingEntity(
    @PrimaryKey(autoGenerate = true) val listingId: Int = 0,
    var title: String,
    var description: String,
    var category: ListingCategory,
    var location: Region,
    var status: ListingStatus = ListingStatus.AVAILABLE,
    var price: Double,
    var photos: String,
    val creationDate: Int?,
    val availableFrom: Int?,
    val availableUntil: Int?,
    var longTermDiscount: Double
)

class Converters {
    // Image conversion
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    // Date conversion
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Int? {
        return date?.format(dateFormatter)?.toInt()
    }

    @TypeConverter
    fun toLocalDate(dateInt: Int?): LocalDate? {
        return dateInt?.toString()?.let {
            LocalDate.parse(it, dateFormatter)
        }
    }
}

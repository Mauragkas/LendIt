import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.format.DateTimeFormatter

enum class ListingStatus {
    AVAILABLE,
    UNAVAILABLE,
    INACTIVE
}

@Entity(tableName = "listing")
data class ListingEntity(
    @PrimaryKey(autoGenerate = true) val listingId: Int = 0,
    var title: String,
    var description: String,
    var category: String,
    var location: String,
    var status: ListingStatus = ListingStatus.AVAILABLE,
    var price: Double,
    var photos: String,
    val creationDate: String?,
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
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? {
        return dateString?.let { LocalDateTime.parse(it, formatter) }
    }
}

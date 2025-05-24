import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.lendit.data.local.entities.Report

@Database(entities = [UserEntity::class, EquipmentListing::class, Report::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun listingDao(): ListingDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lendit-db"
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }

        suspend fun getListings(
            context: Context,
            filters: ListingFilters?
        ): List<EquipmentListing> {
            val database =
                INSTANCE
                    ?: synchronized(this) {
                        val instance =
                            Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "lendit-db"
                            )
                                .fallbackToDestructiveMigration(true)
                                .build()
                        INSTANCE = instance
                        instance
                    }

            // If there are no listings in the database, you might add sample data only once
            if (database.listingDao().getCount() == 0) {
                populateDatabase(database.listingDao())
            }

            val result =
                filters?.let { safeFilters ->
                    database.listingDao().getListings(buildQuery(safeFilters))
                }
                    ?: run { database.listingDao().getAllListings() }
            return result
        }

        fun getLogin(context: Context, scope: CoroutineScope): AppDatabase { // Pass in a scope
            return INSTANCE
                ?: synchronized(this) {
                    val instance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "lendit-db"
                        )
                            .fallbackToDestructiveMigration(true) // <-- add this line
                            .build()
                    INSTANCE = instance

                    // --- DEVELOPMENT ONLY: Clear and Repopulate every time ---
                    scope.launch(Dispatchers.IO) {
                        INSTANCE?.let { database ->
                            // Clear
                            database.userDao().deleteAllUsers()

                            populateDatabase(database.userDao())
                        }
                    }
                    // --- END DEVELOPMENT ONLY ---

                    instance
                }
        }

        private suspend fun populateDatabase(userDao: UserDao) {
            // insert dummy data here, e.g.:
            val renter =
                UserEntity(
                    userId = "paflou",
                    name = "paflou Renter",
                    email = "paflou@renter.com",
                    password = "123",
                    phoneNumber = "1112223333",
                    location = "CityA",
                    userType = "Renter",
                    favoritesJson = "[]"
                )

            val owner =
                UserEntity(
                    userId = "mavragkas",
                    name = "mavragas owner",
                    email = "mavragas@owner.com",
                    password = "123",
                    phoneNumber = "1112223333",
                    location = "CityA",
                    userType = "Owner",
                    premiumStatus = false,
                    premiumPlan = null,
                    ratings = 4.5f
                )

            val admin =
                UserEntity(
                    userId = "natalia",
                    name = "natalia admin",
                    email = "natalia@admin.com",
                    password = "123",
                    phoneNumber = "1112223333",
                    location = "CityA",
                    userType = "Admin",
                    staffId = "staff123"
                )
            userDao.insertAll(listOf(renter, owner, admin))
        }

        private suspend fun populateDatabase(listingDao: ListingDao) {

            val photoUriStrings =
                listOf(
                    "https://picsum.photos/200/300",
                    "https://picsum.photos/200/300",
                    "https://picsum.photos/200/300",
                    )
            val photos = Converters().fromList(photoUriStrings)

            val listing1 =
                EquipmentListing(
                    listingId = 0,
                    title = "Listing 1",
                    description = "Description of listing 1",
                    category = ListingCategory.ELECTRIC,
                    location = Region.CENTRAL_MACEDONIA,
                    status = ListingStatus.AVAILABLE,
                    price = 100.0,
                    photos = photos,
                    creationDate = Converters().fromLocalDate(LocalDate.of(2025, 5, 15)),
                    availableFrom = Converters().fromLocalDate(LocalDate.of(2025, 5, 15)),
                    availableUntil = Converters().fromLocalDate(LocalDate.of(2026, 5, 15)),
                    longTermDiscount = 0.0
                )

            val listing2 =
                EquipmentListing(
                    listingId = 0,
                    title = "Listing 2",
                    description = "Description of listing 2",
                    category = ListingCategory.ELECTRIC,
                    location = Region.CRETE,
                    status = ListingStatus.INACTIVE,
                    price = 150.0,
                    photos = photos,
                    creationDate = Converters().fromLocalDate(LocalDate.of(2025, 5, 10)),
                    availableFrom = Converters().fromLocalDate(LocalDate.of(2025, 5, 15)),
                    availableUntil = Converters().fromLocalDate(LocalDate.of(2026, 5, 15)),
                    longTermDiscount = 5.0
                )

            val listing3 =
                EquipmentListing(
                    listingId = 0,
                    title = "Listing 3",
                    description = "Description of listing 3",
                    category = ListingCategory.MANUAL,
                    location = Region.ATTICA,
                    status = ListingStatus.AVAILABLE,
                    price = 200.0,
                    photos = photos,
                    creationDate = Converters().fromLocalDate(LocalDate.of(2025, 5, 10)),
                    availableFrom = Converters().fromLocalDate(LocalDate.of(2025, 5, 15)),
                    availableUntil = Converters().fromLocalDate(LocalDate.of(2026, 5, 15)),
                    longTermDiscount = 10.0
                )

            val listing4 =
                EquipmentListing(
                    listingId = 0,
                    title = "Listing 4",
                    description = "Description of listing 4",
                    category = ListingCategory.MANUAL,
                    location = Region.PELOPONNESE,
                    status = ListingStatus.UNAVAILABLE,
                    price = 250.0,
                    photos = photos,
                    creationDate = Converters().fromLocalDate(LocalDate.of(2025, 5, 10)),
                    availableFrom = Converters().fromLocalDate(LocalDate.of(2025, 5, 15)),
                    availableUntil = Converters().fromLocalDate(LocalDate.of(2026, 5, 15)),
                    longTermDiscount = 15.0
                )

            val listings = listOf<EquipmentListing>(listing1, listing2, listing3, listing4)
            listingDao.insertAll(listings)
        }
    }
}

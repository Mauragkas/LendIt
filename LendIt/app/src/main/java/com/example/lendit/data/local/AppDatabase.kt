import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lendit.data.local.dao.CouponDao
import com.example.lendit.data.local.dao.FavoriteDao
import com.example.lendit.data.local.dao.OrderDao
import com.example.lendit.data.local.dao.RentalDao
import com.example.lendit.data.local.dao.ReviewDao
import com.example.lendit.data.local.entities.Coupon
import com.example.lendit.data.local.entities.Favorite
import com.example.lendit.data.local.entities.Order
import com.example.lendit.data.local.entities.Rental
import com.example.lendit.data.local.entities.Report
import com.example.lendit.data.local.entities.Review
import com.example.lendit.data.local.entities.UserCart
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
        entities =
                [
                        UserEntity::class,
                        EquipmentListing::class,
                        Report::class,
                        UserCart::class,
                        Favorite::class,
                        Order::class,
                        Coupon::class,
                        Rental::class,
                        Review::class],
        version = 16
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun couponDao(): CouponDao
    abstract fun listingDao(): ListingDao
    abstract fun reportDao(): ReportDao
    abstract fun cartDao(): CartDao
    abstract fun FavoriteDao(): FavoriteDao
    abstract fun OrderDao(): OrderDao
    abstract fun rentalDao(): RentalDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                AppDatabase::class.java,
                                                "lendit-db"
                                        )
                                        .build()

                        INSTANCE = instance
                        instance
                    }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                AppDatabase::class.java,
                                                "lendit_database"
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
                                populateDatabase(database.listingDao())

                                val existingCoupons = database.couponDao().getAllCoupons()

                                if (existingCoupons.isEmpty()) {
                                    insertCoupons(database.couponDao())
                                }

                                // Add sample rentals for testing
                                populateRentals(database)
                            }
                        }
                        // --- END DEVELOPMENT ONLY ---

                        instance
                    }
        }

        private suspend fun populateDatabase(userDao: UserDao) {
            // insert dummy data here, e.g.:
            if(userDao.getUserNumber() == 0) {
                val renter =
                        UserEntity(
                                userId = 1,
                                name = "paflou Renter",
                                email = "paflou@renter.com",
                                password = "123",
                                phoneNumber = "1112223333",
                                location = "CityA",
                                userType = "Renter",
                        )

                val owner =
                        UserEntity(
                                userId = 2,
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
                                userId = 3,
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
        }

        private suspend fun populateDatabase(listingDao: ListingDao) {
            if(listingDao.getCount() == 0) {
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
                                ownerName = "mavragas owner",
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
                                ownerName = "mavragas owner",
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
                                ownerName = "mavragas owner",
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
                                ownerName = "mavragas owner",
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

        private suspend fun insertCoupons(couponDao: CouponDao) {
            if(couponDao.getAllCoupons().isEmpty()) {
                couponDao.insert(Coupon("save30", 30))
                couponDao.insert(Coupon("nikos15", 15))
            }
        }

        // Add sample rentals for testing
        private suspend fun populateRentals(db: AppDatabase) {
            // Check if there are any rentals already
            val existingRentals = db.rentalDao().getUnreviewedRentalsForUser(1)
            if (existingRentals.isEmpty()) {
                // Add sample rentals for testing
                val rental1 =
                        Rental(
                                userId = 1,
                                listingId = 1,
                                rentalDate =
                                        System.currentTimeMillis() -
                                                7 * 24 * 60 * 60 * 1000, // 7 days ago
                                isReviewed = false
                        )

                val rental2 =
                        Rental(
                                userId = 1,
                                listingId = 3,
                                rentalDate =
                                        System.currentTimeMillis() -
                                                14 * 24 * 60 * 60 * 1000, // 14 days ago
                                isReviewed = false
                        )

                db.rentalDao().insert(rental1)
                db.rentalDao().insert(rental2)
            }
        }

        suspend fun showCart(context: Context, userId: Int): List<EquipmentListing> {

            val db = getInstance(context) // see helper below

            if (db.cartDao().getCartCount(userId) == 0) {
                // TODO: HANDLE NOTHING IN CART
            }

            return db.cartDao().getCartById(userId)
        }
    }
}

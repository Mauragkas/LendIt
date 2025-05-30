package com.example.lendit.data.repository

import android.content.Context
import AppDatabase

/**
 * Singleton that provides access to all repositories in the application.
 * This class ensures we only create one instance of each repository.
 */
object RepositoryProvider {
    private var orderRepository: OrderRepository? = null
    private var reviewRepository: ReviewRepository? = null
    private var listingRepository: ListingRepository? = null
    private var couponRepository: CouponRepository? = null
    private var favoritesRepository: FavoriteRepository? = null
    private var userRepository: UserRepository? = null
    private var reportRepository: ReportRepository? = null
    private var cartRepository: CartRepository? = null
    /**
     * Gets or creates the OrderRepository instance
     */
    fun getOrderRepository(context: Context): OrderRepository {
        return orderRepository ?: OrderRepository(AppDatabase.getInstance(context)).also {
            orderRepository = it
        }
    }
    /**
     * Gets or creates the ReviewRepository instance
     */
    fun getReviewRepository(context: Context): ReviewRepository {
        return reviewRepository ?: ReviewRepository(AppDatabase.getInstance(context)).also {
            reviewRepository = it
        }
    }

    fun getListingRepository(context: Context): ListingRepository =
        listingRepository ?: ListingRepository(AppDatabase.getInstance(context)).also {
            listingRepository = it
        }

    fun getCouponRepository(context: Context): CouponRepository {
        return couponRepository ?: CouponRepository(AppDatabase.getInstance(context).couponDao()).also {
            couponRepository = it
        }
    }
    fun getFavoriteRepository(context: Context): FavoriteRepository {
        return favoritesRepository ?: FavoriteRepository(AppDatabase.getInstance(context).FavoriteDao()).also {
            favoritesRepository = it
        }
    }
    fun getUserRepository(context: Context): UserRepository {
        return userRepository ?: UserRepository(AppDatabase.getInstance(context).userDao()).also {
            userRepository = it
        }
    }

    fun getReportRepository(context: Context): ReportRepository {
        return reportRepository ?: ReportRepository(AppDatabase.getInstance(context).reportDao()).also {
            reportRepository = it
        }
    }
    fun getCartRepository(context: Context): CartRepository {
        return cartRepository ?: CartRepository(AppDatabase.getInstance(context).cartDao()).also {
            cartRepository = it
        }
    }
}

package com.roamly.hotel.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.roamly.favorite.local.FavoriteDao
import com.roamly.favorite.local.FavoriteEntity

@Database(entities = [HotelEntity::class, FavoriteEntity::class], version = 2, exportSchema = false)
@TypeConverters(HotelTypeConverters::class)
abstract class RoamlyDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao
    abstract fun favoriteDao(): FavoriteDao
}

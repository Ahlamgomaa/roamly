package com.roamly.hotel.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HotelEntity::class], version = 1, exportSchema = false)
@TypeConverters(HotelTypeConverters::class)
abstract class RoamlyDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao
}

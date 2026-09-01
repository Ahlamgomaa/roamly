package com.roamly.di

import android.content.Context
import androidx.room.Room
import com.roamly.hotel.local.HotelDao
import com.roamly.hotel.local.RoamlyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RoamlyDatabase {
        return Room.databaseBuilder(
            context,
            RoamlyDatabase::class.java,
            "roamly_db"
        ).build()
    }

    @Provides
    fun provideHotelDao(database: RoamlyDatabase): HotelDao {
        return database.hotelDao()
    }
}

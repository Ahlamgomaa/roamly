package com.roamly.di

import com.roamly.favorite.repository.FavoriteRepository
import com.roamly.favorite.repository.FavoriteRepositoryImpl
import com.roamly.hotel.datasource.HotelDataSource
import com.roamly.hotel.datasource.HotelJsonDataSourceImpl
import com.roamly.hotel.repository.HotelRepository
import com.roamly.hotel.repository.HotelRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHotelDataSource(
        impl: HotelJsonDataSourceImpl
    ): HotelDataSource

    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        impl: HotelRepositoryImpl
    ): HotelRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl
    ): FavoriteRepository
}

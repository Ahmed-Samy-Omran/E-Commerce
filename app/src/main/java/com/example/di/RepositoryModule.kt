package com.example.di

import com.example.data.repository.auth.CountryRepository
import com.example.data.repository.auth.CountryRepositoryImpl
import com.example.data.repository.auth.FirebaseAuthRepository
import com.example.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.data.repository.categories.CategoriesRepository
import com.example.data.repository.categories.CategoriesRepositoryImpl
import com.example.data.repository.common.AppDataStoreRepositoryImpl
import com.example.data.repository.common.AppPreferenceRepository
import com.example.data.repository.home.SalesAdsRepository
import com.example.data.repository.home.SalesAdsRepositoryImpl
import com.example.data.repository.products.ProductsRepository
import com.example.data.repository.products.ProductsRepositoryImpl
import com.example.data.repository.special_sections.SpecialSectionsRepository
import com.example.data.repository.special_sections.SpecialSectionsRepositoryImpl
import com.example.data.repository.user.UserFirestoreRepository
import com.example.data.repository.user.UserFirestoreRepositoryImpl
import com.example.data.repository.user.UserPreferenceRepository
import com.example.data.repository.user.UserPreferenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun provideAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository


    @Binds
    @Singleton
    abstract fun provideUserPreferenceRepository(
        userPreferenceRepositoryImpl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository

    @Binds
    @Singleton
    abstract fun provideAppPreferenceRepository(
        appPreferenceRepositoryImpl: AppDataStoreRepositoryImpl
    ): AppPreferenceRepository

    @Binds
    @Singleton
    abstract fun provideUserFirestoreRepository(
        userFirestoreRepositoryImpl: UserFirestoreRepositoryImpl
    ): UserFirestoreRepository



    @Binds
    @Singleton
    abstract fun provideSalesAdsRepository(
        salesAdsRepositoryImpl: SalesAdsRepositoryImpl
    ): SalesAdsRepository

    @Binds
    @Singleton
    abstract fun provideCategoriesRepository(
        categoriesRepositoryImpl: CategoriesRepositoryImpl
    ): CategoriesRepository


    @Binds
    @Singleton
    abstract fun provideProductsRepository(
        productsRepositoryImpl: ProductsRepositoryImpl
    ): ProductsRepository


    @Binds
    @Singleton
    abstract fun provideCountryRepositoryImpl(
        countryRepositoryImpl: CountryRepositoryImpl
    ): CountryRepository

    @Binds
    @Singleton
    abstract fun provideSpecialSectionsRepositoryImpl(
        specialSectionsRepository: SpecialSectionsRepositoryImpl
    ): SpecialSectionsRepository

}
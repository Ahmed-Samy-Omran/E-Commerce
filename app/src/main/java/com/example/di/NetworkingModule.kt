package com.example.di

import com.example.data.datasource.networking.CloudFunctionAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    @Provides
    @Singleton
    fun provideCloudFunctionsApi(): CloudFunctionAPI {
        return CloudFunctionAPI.create()
    }
}
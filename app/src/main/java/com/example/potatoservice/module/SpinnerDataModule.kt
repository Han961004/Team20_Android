package com.example.potatoservice.module

import com.example.potatoservice.model.APIService
import com.example.potatoservice.ui.share.SpinnerDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpinnerDataModule {
	@Provides
	@Singleton
	fun provideSpinnerDataSource(apiService: APIService): SpinnerDataSource {
		return SpinnerDataSource(apiService)
	}
}
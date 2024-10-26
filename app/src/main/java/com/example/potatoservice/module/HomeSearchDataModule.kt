package com.example.potatoservice.module

import com.example.potatoservice.ui.home.HomeSearchData
import com.example.potatoservice.ui.home.HomeSearchDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeSearchDataModule {
	@Binds
	abstract fun bindHomeSearchData(impl: HomeSearchDataSource): HomeSearchData
}
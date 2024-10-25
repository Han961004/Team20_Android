package com.example.potatoservice.module

import com.example.potatoservice.ui.detail.DetailSearchData
import com.example.potatoservice.ui.detail.DetailSearchDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DetailSearchDataModule {
	@Binds
	abstract fun bindDetailSearchData(impl: DetailSearchDataSource): DetailSearchData
}
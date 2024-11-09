package com.example.potatoservice.ui.share

data class Request(
	val page: Int,
	val size: Int?,
	val sort: String?,
	val sidoCode: Int?,
	val sidoGunguCode: Int?,
	val beforeDeadlineOnly: Boolean?,
	val teenPossibleOnly: Boolean?,
	val category: String?,
	val keyword: String
)

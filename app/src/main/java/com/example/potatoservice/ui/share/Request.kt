package com.example.potatoservice.ui.share

data class Request(
	val page: Int,
	val size: Int?,
	val sort: String?,
	val beforeDeadlineOnly: Boolean?,
	val teenPossibleOnly: Boolean?,
	val category: String?
)

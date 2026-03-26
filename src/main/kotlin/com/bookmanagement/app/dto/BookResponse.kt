package com.bookmanagement.app.dto

import com.bookmanagement.app.domain.enums.PublishStatus

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publishStatus: PublishStatus,
    val authors: List<AuthorResponse>
)
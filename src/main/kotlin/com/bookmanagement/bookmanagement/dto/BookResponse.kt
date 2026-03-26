package com.bookmanagement.bookmanagement.dto

import com.bookmanagement.bookmanagement.domain.enums.PublishStatus

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publishStatus: PublishStatus,
    val authors: List<AuthorResponse>
)
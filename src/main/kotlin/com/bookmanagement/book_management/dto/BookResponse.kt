package com.bookmanagement.book_management.dto

import com.bookmanagement.book_management.domain.enums.PublishStatus

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publishStatus: PublishStatus,
    val authors: List<AuthorResponse>
)
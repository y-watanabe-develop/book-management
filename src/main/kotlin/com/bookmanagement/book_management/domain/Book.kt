package com.bookmanagement.book_management.domain

import com.bookmanagement.book_management.domain.enums.PublishStatus

data class Book(
    val id: Long?,
    val title: String,
    val price: Int,
    val publishStatus: PublishStatus,
    val authors: List<Author>
)
package com.bookmanagement.book_management.dto

data class BookRequest(
    val title: String,
    val price: Int,
    val authorIds: List<Long>
)
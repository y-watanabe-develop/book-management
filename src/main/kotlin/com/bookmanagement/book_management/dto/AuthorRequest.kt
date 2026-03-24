package com.bookmanagement.book_management.dto

import java.time.LocalDate

data class AuthorRequest(
    val name: String,
    val birthDate: LocalDate
)
package com.bookmanagement.app.dto

import java.time.LocalDate

data class AuthorResponse(
    val id: Long,
    val name: String,
    val birthDate: LocalDate
)
package com.bookmanagement.app.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class AuthorRequest(
    @field:NotBlank
    val name: String,
    @field:PastOrPresent
    val birthDate: LocalDate
)
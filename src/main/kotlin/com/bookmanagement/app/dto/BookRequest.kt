package com.bookmanagement.app.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class BookRequest(
    @field:NotBlank
    val title: String,
    @field:Min(0)
    val price: Int,
    @field:NotEmpty
    val authorIds: List<Long>
)
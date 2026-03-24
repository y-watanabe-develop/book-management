package com.bookmanagement.book_management.service

import com.bookmanagement.book_management.dto.BookRequest
import com.bookmanagement.book_management.dto.BookResponse
import org.springframework.stereotype.Service

@Service
class BookService {

    fun create(request: BookRequest): BookResponse {
        TODO()
    }

    fun update(id: Long, request: BookRequest): BookResponse {
        TODO()
    }

    fun publish(id: Long): BookResponse {
        TODO()
    }
}
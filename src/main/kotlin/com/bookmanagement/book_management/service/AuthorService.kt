package com.bookmanagement.book_management.service

import com.bookmanagement.book_management.dto.AuthorRequest
import com.bookmanagement.book_management.dto.AuthorResponse
import com.bookmanagement.book_management.dto.BookResponse
import org.springframework.stereotype.Service

@Service
class AuthorService {

    fun create(request: AuthorRequest): AuthorResponse {
        TODO()
    }

    fun update(id: Long, request: AuthorRequest): AuthorResponse {
        TODO()
    }

    fun getBooksByAuthor(id: Long): List<BookResponse> {
        TODO()
    }
}
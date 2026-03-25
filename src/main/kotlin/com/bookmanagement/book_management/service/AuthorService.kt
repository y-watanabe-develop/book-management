package com.bookmanagement.book_management.service

import com.bookmanagement.book_management.dto.AuthorRequest
import com.bookmanagement.book_management.dto.AuthorResponse
import com.bookmanagement.book_management.dto.BookResponse
import com.bookmanagement.book_management.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    @Transactional
    fun create(request: AuthorRequest): AuthorResponse =
        authorRepository.create(request)

    @Transactional
    fun update(id: Long, request: AuthorRequest): AuthorResponse =
        authorRepository.update(id, request)

    fun getBooksByAuthor(id: Long): List<BookResponse> =
        authorRepository.findBooksByAuthorId(id)
}
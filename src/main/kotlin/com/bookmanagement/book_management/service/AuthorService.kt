package com.bookmanagement.book_management.service

import com.bookmanagement.book_management.dto.AuthorRequest
import com.bookmanagement.book_management.dto.AuthorResponse
import com.bookmanagement.book_management.dto.BookResponse
import com.bookmanagement.book_management.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    @Transactional(readOnly = true)
    fun findAll(): List<AuthorResponse> =
        authorRepository.findAll()

    @Transactional(readOnly = true)
    fun findById(id: Long): AuthorResponse =
        authorRepository.findById(id)

    @Transactional
    fun create(request: AuthorRequest): AuthorResponse =
        authorRepository.create(request)

    @Transactional
    fun update(id: Long, request: AuthorRequest): AuthorResponse =
        authorRepository.update(id, request)

    @Transactional(readOnly = true)
    fun getBooksByAuthor(id: Long): List<BookResponse> =
        authorRepository.findBooksByAuthorId(id)
}
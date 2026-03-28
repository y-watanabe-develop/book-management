package com.bookmanagement.app.service

import com.bookmanagement.app.dto.AuthorRequest
import com.bookmanagement.app.dto.AuthorResponse
import com.bookmanagement.app.dto.BookResponse
import com.bookmanagement.app.repository.AuthorRepository
import com.bookmanagement.app.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {

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
        bookRepository.findByAuthorId(id)
}

package com.bookmanagement.book_management.service

import com.bookmanagement.book_management.dto.BookRequest
import com.bookmanagement.book_management.dto.BookResponse
import com.bookmanagement.book_management.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val bookRepository: BookRepository) {

    @Transactional
    fun create(request: BookRequest): BookResponse =
        bookRepository.create(request)

    @Transactional
    fun update(id: Long, request: BookRequest): BookResponse =
        bookRepository.update(id, request)

    @Transactional
    fun publish(id: Long): BookResponse =
        bookRepository.publish(id)
}
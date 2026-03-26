package com.bookmanagement.bookmanagement.service

import com.bookmanagement.bookmanagement.domain.enums.PublishStatus
import com.bookmanagement.bookmanagement.dto.BookRequest
import com.bookmanagement.bookmanagement.dto.BookResponse
import com.bookmanagement.bookmanagement.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val bookRepository: BookRepository) {

    @Transactional(readOnly = true)
    fun findAll(): List<BookResponse> =
        bookRepository.findAllWithAuthors()

    @Transactional(readOnly = true)
    fun findById(id: Long): BookResponse =
        bookRepository.findByIdWithAuthors(id) ?: throw NoSuchElementException("Book not found: $id")

    @Transactional
    fun create(request: BookRequest): BookResponse =
        bookRepository.create(request)

    @Transactional
    fun update(id: Long, request: BookRequest): BookResponse =
        bookRepository.update(id, request)

    @Transactional
    fun publish(id: Long): BookResponse {
        val book = bookRepository.findByIdWithAuthors(id) ?: throw NoSuchElementException("Book not found: $id")
        if (book.publishStatus == PublishStatus.PUBLISHED) {
            throw IllegalStateException("Book is already published")
        }
        bookRepository.updatePublishStatus(id, PublishStatus.PUBLISHED)
        return requireNotNull(bookRepository.findByIdWithAuthors(id)) { "Book not found after publish" }
    }
}
package com.bookmanagement.book_management.controller

import com.bookmanagement.book_management.dto.BookRequest
import com.bookmanagement.book_management.dto.BookResponse
import com.bookmanagement.book_management.service.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: BookRequest): BookResponse =
        bookService.create(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid request: BookRequest): BookResponse =
        bookService.update(id, request)

    @PatchMapping("/{id}/publish")
    fun publish(@PathVariable id: Long): BookResponse =
        bookService.publish(id)
}
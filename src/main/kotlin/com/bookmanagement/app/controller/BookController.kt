package com.bookmanagement.app.controller

import com.bookmanagement.app.dto.BookRequest
import com.bookmanagement.app.dto.BookResponse
import com.bookmanagement.app.service.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun findAll(): List<BookResponse> =
        bookService.findAll()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): BookResponse =
        bookService.findById(id)

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
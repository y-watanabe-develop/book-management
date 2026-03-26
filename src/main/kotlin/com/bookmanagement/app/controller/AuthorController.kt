package com.bookmanagement.app.controller

import com.bookmanagement.app.dto.AuthorRequest
import com.bookmanagement.app.dto.AuthorResponse
import com.bookmanagement.app.dto.BookResponse
import com.bookmanagement.app.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/authors")
class AuthorController(private val authorService: AuthorService) {

    @GetMapping
    fun findAll(): List<AuthorResponse> =
        authorService.findAll()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): AuthorResponse =
        authorService.findById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: AuthorRequest): AuthorResponse =
        authorService.create(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid request: AuthorRequest): AuthorResponse =
        authorService.update(id, request)

    @GetMapping("/{id}/books")
    fun getBooksByAuthor(@PathVariable id: Long): List<BookResponse> =
        authorService.getBooksByAuthor(id)
}
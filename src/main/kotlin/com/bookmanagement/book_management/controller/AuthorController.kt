package com.bookmanagement.book_management.controller

import com.bookmanagement.book_management.dto.AuthorRequest
import com.bookmanagement.book_management.dto.AuthorResponse
import com.bookmanagement.book_management.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/authors")
class AuthorController(private val authorService: AuthorService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: AuthorRequest): AuthorResponse =
        authorService.create(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid request: AuthorRequest): AuthorResponse =
        authorService.update(id, request)

    @GetMapping("/{id}/books")
    fun getBooksByAuthor(@PathVariable id: Long) =
        authorService.getBooksByAuthor(id)
}
package com.bookmanagement.app.service

import com.bookmanagement.app.domain.enums.PublishStatus
import com.bookmanagement.app.dto.AuthorRequest
import com.bookmanagement.app.dto.AuthorResponse
import com.bookmanagement.app.dto.BookResponse
import com.bookmanagement.app.repository.AuthorRepository
import com.bookmanagement.app.repository.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AuthorServiceTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @Mock
    private lateinit var bookRepository: BookRepository

    @InjectMocks
    private lateinit var authorService: AuthorService

    private val sampleAuthor = AuthorResponse(
        id = 1L,
        name = "テスト著者",
        birthDate = LocalDate.of(1990, 1, 1)
    )

    private val sampleRequest = AuthorRequest(
        name = "テスト著者",
        birthDate = LocalDate.of(1990, 1, 1)
    )

    private val sampleBook = BookResponse(
        id = 1L,
        title = "テスト書籍",
        price = 1000,
        publishStatus = PublishStatus.UNPUBLISHED,
        authors = listOf(sampleAuthor)
    )

    @Test
    fun `全著者一覧を取得できる`() {
        given(authorRepository.findAll()).willReturn(listOf(sampleAuthor))

        val result = authorService.findAll()

        assertEquals(listOf(sampleAuthor), result)
    }

    @Test
    fun `IDで著者を取得できる`() {
        given(authorRepository.findById(1L)).willReturn(sampleAuthor)

        val result = authorService.findById(1L)

        assertEquals(sampleAuthor, result)
    }

    @Test
    fun `存在しない著者IDで取得するとNoSuchElementExceptionが発生する`() {
        given(authorRepository.findById(999L)).willThrow(NoSuchElementException("Author not found: 999"))

        assertThrows(NoSuchElementException::class.java) {
            authorService.findById(999L)
        }
    }

    @Test
    fun `著者を登録できる`() {
        given(authorRepository.create(sampleRequest)).willReturn(sampleAuthor)

        val result = authorService.create(sampleRequest)

        assertEquals(sampleAuthor, result)
        verify(authorRepository).create(sampleRequest)
    }

    @Test
    fun `著者を更新できる`() {
        given(authorRepository.update(1L, sampleRequest)).willReturn(sampleAuthor)

        val result = authorService.update(1L, sampleRequest)

        assertEquals(sampleAuthor, result)
        verify(authorRepository).update(1L, sampleRequest)
    }

    @Test
    fun `著者に紐づく書籍一覧を取得できる`() {
        given(bookRepository.findByAuthorId(1L)).willReturn(listOf(sampleBook))

        val result = authorService.getBooksByAuthor(1L)

        assertEquals(listOf(sampleBook), result)
    }
}

package com.bookmanagement.book_management

import com.bookmanagement.book_management.dto.AuthorRequest
import com.bookmanagement.book_management.dto.AuthorResponse
import com.bookmanagement.book_management.repository.AuthorRepository
import com.bookmanagement.book_management.service.AuthorService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AuthorServiceTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var authorService: AuthorService

    @Test
    fun `著者を正常に登録できる`() {
        val request = AuthorRequest(
            name = "テスト著者",
            birthDate = LocalDate.of(1990, 1, 1)
        )
        val expected = AuthorResponse(
            id = 1L,
            name = "テスト著者",
            birthDate = LocalDate.of(1990, 1, 1)
        )
        `when`(authorRepository.create(request)).thenReturn(expected)

        val result = authorService.create(request)

        assertEquals(expected, result)
    }
}
package com.bookmanagement.book_management

import com.bookmanagement.book_management.domain.enums.PublishStatus
import com.bookmanagement.book_management.dto.AuthorResponse
import com.bookmanagement.book_management.dto.BookResponse
import com.bookmanagement.book_management.repository.BookRepository
import com.bookmanagement.book_management.service.BookService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @InjectMocks
    private lateinit var bookService: BookService

    private fun createBookResponse(publishStatus: PublishStatus) = BookResponse(
        id = 1L,
        title = "テスト書籍",
        price = 1000,
        publishStatus = publishStatus,
        authors = listOf(
            AuthorResponse(
                id = 1L,
                name = "テスト著者",
                birthDate = LocalDate.of(1990, 1, 1)
            )
        )
    )

    @Test
    fun `未出版の書籍を出版済みに変更できる`() {
        val published = createBookResponse(PublishStatus.PUBLISHED)

        `when`(bookRepository.publish(1L)).thenReturn(published)

        val result = bookService.publish(1L)

        assertEquals(PublishStatus.PUBLISHED, result.publishStatus)
    }

}
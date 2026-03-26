package com.bookmanagement.bookmanagement.service

import com.bookmanagement.bookmanagement.domain.enums.PublishStatus
import com.bookmanagement.bookmanagement.dto.AuthorResponse
import com.bookmanagement.bookmanagement.dto.BookResponse
import com.bookmanagement.bookmanagement.repository.BookRepository
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
class BookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @InjectMocks
    private lateinit var bookService: BookService

    private val sampleAuthor = AuthorResponse(
        id = 1L,
        name = "テスト著者",
        birthDate = LocalDate.of(1990, 1, 1)
    )

    private val unpublishedBook = BookResponse(
        id = 1L,
        title = "テスト書籍",
        price = 1000,
        publishStatus = PublishStatus.UNPUBLISHED,
        authors = listOf(sampleAuthor)
    )

    private val publishedBook = unpublishedBook.copy(publishStatus = PublishStatus.PUBLISHED)

    @Test
    fun `未公開の書籍を出版済みに変更できる`() {
        given(bookRepository.findById(1L))
            .willReturn(unpublishedBook)
            .willReturn(publishedBook)

        val result = bookService.publish(1L)

        assertEquals(PublishStatus.PUBLISHED, result.publishStatus)
        verify(bookRepository).updatePublishStatus(1L, PublishStatus.PUBLISHED)
    }

    @Test
    fun `存在しない書籍IDで出版するとNoSuchElementExceptionが発生する`() {
        given(bookRepository.findById(999L)).willReturn(null)

        assertThrows(NoSuchElementException::class.java) {
            bookService.publish(999L)
        }
    }

    @Test
    fun `出版済みの書籍を再出版するとIllegalStateExceptionが発生する`() {
        given(bookRepository.findById(1L)).willReturn(publishedBook)

        assertThrows(IllegalStateException::class.java) {
            bookService.publish(1L)
        }
    }
}

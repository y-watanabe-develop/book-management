package com.bookmanagement.book_management

import com.bookmanagement.book_management.dto.AuthorRequest
import com.bookmanagement.book_management.dto.BookRequest
import com.bookmanagement.book_management.repository.AuthorRepository
import com.bookmanagement.book_management.repository.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

class AuthorRepositoryTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Test
    fun `著者を正常に登録できる`() {
        val request = AuthorRequest(
            name = "テスト著者",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        val response = authorRepository.create(request)

        assertEquals("テスト著者", response.name)
        assertEquals(LocalDate.of(1990, 1, 1), response.birthDate)
    }

    @Test
    fun `著者を正常に更新できる`() {
        val createRequest = AuthorRequest(
            name = "テスト著者",
            birthDate = LocalDate.of(1990, 1, 1)
        )
        val created = authorRepository.create(createRequest)

        val updateRequest = AuthorRequest(
            name = "更新著者",
            birthDate = LocalDate.of(1995, 5, 5)
        )
        val updated = authorRepository.update(created.id, updateRequest)

        assertEquals("更新著者", updated.name)
        assertEquals(LocalDate.of(1995, 5, 5), updated.birthDate)
    }

    @Test
    fun `存在しない著者IDで更新するとNoSuchElementExceptionが発生する`() {
        val request = AuthorRequest(
            name = "テスト著者",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        assertThrows(NoSuchElementException::class.java) {
            authorRepository.update(999L, request)
        }
    }

    @Test
    fun `著者に紐づく書籍一覧を取得できる`() {
        val author = authorRepository.create(
            AuthorRequest(name = "テスト著者", birthDate = LocalDate.of(1990, 1, 1))
        )
        bookRepository.create(
            BookRequest(
                title = "テスト書籍",
                price = 1000,
                authorIds = listOf(author.id)
            )
        )

        val books = authorRepository.findBooksByAuthorId(author.id)

        assertEquals(1, books.size)
        assertEquals("テスト書籍", books.first().title)
        assertEquals(1, books.first().authors.size)
    }
}
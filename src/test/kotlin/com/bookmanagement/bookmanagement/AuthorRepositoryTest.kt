package com.bookmanagement.bookmanagement

import com.bookmanagement.bookmanagement.dto.AuthorRequest
import com.bookmanagement.bookmanagement.dto.BookRequest
import com.bookmanagement.bookmanagement.repository.AuthorRepository
import com.bookmanagement.bookmanagement.repository.BookRepository
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
    fun `著者一覧を取得できる`() {
        authorRepository.create(
            AuthorRequest(name = "テスト著者", birthDate = LocalDate.of(1990, 1, 1))
        )

        val authors = authorRepository.findAll()

        assertEquals(1, authors.size)
        assertEquals("テスト著者", authors.first().name)
    }

    @Test
    fun `著者を1件取得できる`() {
        val created = authorRepository.create(
            AuthorRequest(name = "テスト著者", birthDate = LocalDate.of(1990, 1, 1))
        )

        val found = authorRepository.findById(created.id)

        assertEquals("テスト著者", found.name)
        assertEquals(LocalDate.of(1990, 1, 1), found.birthDate)
    }

    @Test
    fun `存在しない著者IDで1件取得するとNoSuchElementExceptionが発生する`() {
        assertThrows(NoSuchElementException::class.java) {
            authorRepository.findById(999L)
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
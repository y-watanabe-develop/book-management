package com.bookmanagement.book_management

import com.bookmanagement.book_management.dto.AuthorRequest
import com.bookmanagement.book_management.dto.BookRequest
import com.bookmanagement.book_management.repository.AuthorRepository
import com.bookmanagement.book_management.repository.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate

class BookRepositoryTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    private fun createTestAuthor() = authorRepository.create(
        AuthorRequest(name = "テスト著者", birthDate = LocalDate.of(1990, 1, 1))
    )

    @Test
    fun `書籍を正常に登録できる`() {
        val author = createTestAuthor()
        val request = BookRequest(
            title = "テスト書籍",
            price = 1000,
            authorIds = listOf(author.id)
        )

        val response = bookRepository.create(request)

        assertEquals("テスト書籍", response.title)
        assertEquals(1000, response.price)
        assertEquals(1, response.authors.size)
    }

    @Test
    fun `存在しない著者IDで書籍を登録するとDataIntegrityViolationExceptionが発生する`() {
        assertThrows(DataIntegrityViolationException::class.java) {
            bookRepository.create(
                BookRequest(title = "テスト書籍", price = 1000, authorIds = listOf(999L))
            )
        }
    }

    @Test
    fun `複数著者で書籍を登録できる`() {
        val author1 = createTestAuthor()
        val author2 = authorRepository.create(
            AuthorRequest(name = "テスト著者2", birthDate = LocalDate.of(1985, 6, 15))
        )
        val response = bookRepository.create(
            BookRequest(title = "共著書籍", price = 1500, authorIds = listOf(author1.id, author2.id))
        )

        assertEquals(2, response.authors.size)
    }

    @Test
    fun `書籍を正常に更新できる`() {
        val author = createTestAuthor()
        val created = bookRepository.create(
            BookRequest(title = "テスト書籍", price = 1000, authorIds = listOf(author.id))
        )

        val updated = bookRepository.update(
            created.id,
            BookRequest(title = "更新書籍", price = 2000, authorIds = listOf(author.id))
        )

        assertEquals("更新書籍", updated.title)
        assertEquals(2000, updated.price)
    }

    @Test
    fun `存在しない書籍IDで更新するとNoSuchElementExceptionが発生する`() {
        val author = createTestAuthor()

        assertThrows(NoSuchElementException::class.java) {
            bookRepository.update(
                999L,
                BookRequest(title = "テスト書籍", price = 1000, authorIds = listOf(author.id))
            )
        }
    }

    @Test
    fun `書籍を出版済みに変更できる`() {
        val author = createTestAuthor()
        val created = bookRepository.create(
            BookRequest(title = "テスト書籍", price = 1000, authorIds = listOf(author.id))
        )

        val published = bookRepository.publish(created.id)

        assertEquals("PUBLISHED", published.publishStatus.name)
    }

    @Test
    fun `出版済みの書籍を再度出版しようとするとIllegalStateExceptionが発生する`() {
        val author = createTestAuthor()
        val created = bookRepository.create(
            BookRequest(title = "テスト書籍", price = 1000, authorIds = listOf(author.id))
        )
        bookRepository.publish(created.id)

        assertThrows(IllegalStateException::class.java) {
            bookRepository.publish(created.id)
        }
    }
}
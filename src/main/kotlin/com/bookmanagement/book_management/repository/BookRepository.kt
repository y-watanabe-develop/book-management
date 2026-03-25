package com.bookmanagement.book_management.repository

import com.bookmanagement.book_management.domain.enums.PublishStatus
import com.bookmanagement.book_management.dto.AuthorResponse
import com.bookmanagement.book_management.dto.BookRequest
import com.bookmanagement.book_management.dto.BookResponse
import com.bookmanagement.infrastructure.jooq.tables.Authors.AUTHORS
import com.bookmanagement.infrastructure.jooq.tables.BookAuthors.BOOK_AUTHORS
import com.bookmanagement.infrastructure.jooq.tables.Books.BOOKS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val dsl: DSLContext) {

    fun create(request: BookRequest): BookResponse {
        val bookRecord = requireNotNull(
            dsl.insertInto(BOOKS)
                .set(BOOKS.TITLE, request.title)
                .set(BOOKS.PRICE, request.price)
                .returning()
                .fetchOne()
        ) { "Book record must not be null after insert" }

        request.authorIds.forEach { authorId ->
            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, bookRecord.id)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                .execute()
        }

        val bookId = requireNotNull(bookRecord.id) { "Book id must not be null" }
        return requireNotNull(findById(bookId)) { "Book not found after insert" }
    }

    fun update(id: Long, request: BookRequest): BookResponse {
        dsl.update(BOOKS)
            .set(BOOKS.TITLE, request.title)
            .set(BOOKS.PRICE, request.price)
            .where(BOOKS.ID.eq(id))
            .returning()
            .fetchOne()
            ?: throw NoSuchElementException("Book not found: $id")

        dsl.deleteFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(id))
            .execute()

        request.authorIds.forEach { authorId ->
            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, id)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                .execute()
        }

        return requireNotNull(findById(id)) { "Book not found after update" }
    }

    fun publish(id: Long): BookResponse {
        val updated = dsl.update(BOOKS)
            .set(BOOKS.PUBLISH_STATUS, PublishStatus.PUBLISHED.name)
            .where(BOOKS.ID.eq(id))
            .and(BOOKS.PUBLISH_STATUS.eq(PublishStatus.UNPUBLISHED.name))
            .execute()

        // 出版済みの場合はUPDATE対象にならないためupdated == 0で検知
        if (updated == 0) {
            val exists = dsl.fetchExists(dsl.selectFrom(BOOKS).where(BOOKS.ID.eq(id)))
            if (!exists) throw NoSuchElementException("Book not found: $id")
            throw IllegalStateException("Book is already published")
        }

        return requireNotNull(findById(id)) { "Book not found after publish" }
    }

    fun findById(id: Long): BookResponse? {
        val authors = findAuthorsByBookId(id)

        return dsl.selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne()
            ?.let { record ->
                BookResponse(
                    id = requireNotNull(record.id) { "Book id must not be null" },
                    title = requireNotNull(record.title) { "Book title must not be null" },
                    price = requireNotNull(record.price) { "Book price must not be null" },
                    publishStatus = PublishStatus.valueOf(requireNotNull(record.publishStatus) { "Book publishStatus must not be null" }),
                    authors = authors
                )
            }
    }

    private fun findAuthorsByBookId(bookId: Long): List<AuthorResponse> {
        return dsl.select(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .from(AUTHORS)
            .join(BOOK_AUTHORS).on(AUTHORS.ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .fetch { record ->
                AuthorResponse(
                    id = requireNotNull(record[AUTHORS.ID]) { "Author id must not be null" },
                    name = requireNotNull(record[AUTHORS.NAME]) { "Author name must not be null" },
                    birthDate = requireNotNull(record[AUTHORS.BIRTH_DATE]) { "Author birthDate must not be null" }
                )
            }
    }
}
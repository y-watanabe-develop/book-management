package com.bookmanagement.bookmanagement.repository

import com.bookmanagement.bookmanagement.domain.enums.PublishStatus
import com.bookmanagement.bookmanagement.dto.AuthorResponse
import com.bookmanagement.bookmanagement.dto.BookRequest
import com.bookmanagement.bookmanagement.dto.BookResponse
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
        return requireNotNull(findByIdWithAuthors(bookId)) { "Book not found after insert" }
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

        return requireNotNull(findByIdWithAuthors(id)) { "Book not found after update" }
    }

    fun updatePublishStatus(id: Long, status: PublishStatus) {
        dsl.update(BOOKS)
            .set(BOOKS.PUBLISH_STATUS, status.name)
            .where(BOOKS.ID.eq(id))
            .execute()
    }

    fun findAllWithAuthors(): List<BookResponse> {
        val records = dsl.select(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLISH_STATUS,
            AUTHORS.ID,
            AUTHORS.NAME,
            AUTHORS.BIRTH_DATE
        )
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOKS.ID.eq(BOOK_AUTHORS.BOOK_ID))
            .join(AUTHORS).on(AUTHORS.ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .fetch()

        return records
            .groupBy { it[BOOKS.ID] }
            .map { (_, bookRecords) ->
                val first = bookRecords.first()
                BookResponse(
                    id = requireNotNull(first[BOOKS.ID]) { "Book id must not be null" },
                    title = requireNotNull(first[BOOKS.TITLE]) { "Book title must not be null" },
                    price = requireNotNull(first[BOOKS.PRICE]) { "Book price must not be null" },
                    publishStatus = PublishStatus.valueOf(requireNotNull(first[BOOKS.PUBLISH_STATUS]) { "Book publishStatus must not be null" }),
                    authors = bookRecords.map { record ->
                        AuthorResponse(
                            id = requireNotNull(record[AUTHORS.ID]) { "Author id must not be null" },
                            name = requireNotNull(record[AUTHORS.NAME]) { "Author name must not be null" },
                            birthDate = requireNotNull(record[AUTHORS.BIRTH_DATE]) { "Author birthDate must not be null" }
                        )
                    }
                )
            }
    }

    fun findByIdWithAuthors(id: Long): BookResponse? {
        val records = dsl.select(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLISH_STATUS,
            AUTHORS.ID,
            AUTHORS.NAME,
            AUTHORS.BIRTH_DATE
        )
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOKS.ID.eq(BOOK_AUTHORS.BOOK_ID))
            .join(AUTHORS).on(AUTHORS.ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .where(BOOKS.ID.eq(id))
            .fetch()

        if (records.isEmpty()) return null

        val first = records.first()
        return BookResponse(
            id = requireNotNull(first[BOOKS.ID]) { "Book id must not be null" },
            title = requireNotNull(first[BOOKS.TITLE]) { "Book title must not be null" },
            price = requireNotNull(first[BOOKS.PRICE]) { "Book price must not be null" },
            publishStatus = PublishStatus.valueOf(requireNotNull(first[BOOKS.PUBLISH_STATUS]) { "Book publishStatus must not be null" }),
            authors = records.map { record ->
                AuthorResponse(
                    id = requireNotNull(record[AUTHORS.ID]) { "Author id must not be null" },
                    name = requireNotNull(record[AUTHORS.NAME]) { "Author name must not be null" },
                    birthDate = requireNotNull(record[AUTHORS.BIRTH_DATE]) { "Author birthDate must not be null" }
                )
            }
        )
    }
}
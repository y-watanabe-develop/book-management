package com.bookmanagement.book_management.repository

import com.bookmanagement.book_management.domain.enums.PublishStatus
import com.bookmanagement.book_management.dto.AuthorRequest
import com.bookmanagement.book_management.dto.AuthorResponse
import com.bookmanagement.book_management.dto.BookResponse
import com.bookmanagement.infrastructure.jooq.tables.Authors.AUTHORS
import com.bookmanagement.infrastructure.jooq.tables.BookAuthors.BOOK_AUTHORS
import com.bookmanagement.infrastructure.jooq.tables.Books.BOOKS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(private val dsl: DSLContext) {

    fun create(request: AuthorRequest): AuthorResponse {
        val record = requireNotNull(
            dsl.insertInto(AUTHORS)
                .set(AUTHORS.NAME, request.name)
                .set(AUTHORS.BIRTH_DATE, request.birthDate)
                .returning()
                .fetchOne()
        ) { "Author record must not be null after insert" }

        return AuthorResponse(
            id = requireNotNull(record.id) { "Author id must not be null" },
            name = requireNotNull(record.name) { "Author name must not be null" },
            birthDate = requireNotNull(record.birthDate) { "Author birthDate must not be null" }
        )
    }

    fun update(id: Long, request: AuthorRequest): AuthorResponse {
        val record = dsl.update(AUTHORS)
            .set(AUTHORS.NAME, request.name)
            .set(AUTHORS.BIRTH_DATE, request.birthDate)
            .where(AUTHORS.ID.eq(id))
            .returning()
            .fetchOne()
            ?: throw NoSuchElementException("Author not found: $id")

        return AuthorResponse(
            id = requireNotNull(record.id) { "Author id must not be null" },
            name = requireNotNull(record.name) { "Author name must not be null" },
            birthDate = requireNotNull(record.birthDate) { "Author birthDate must not be null" }
        )
    }

    fun findBooksByAuthorId(authorId: Long): List<BookResponse> {
        val exists = dsl.fetchExists(dsl.selectFrom(AUTHORS).where(AUTHORS.ID.eq(authorId)))
        if (!exists) throw NoSuchElementException("Author not found: $authorId")

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
            .where(
                BOOKS.ID.`in`(
                    dsl.select(BOOK_AUTHORS.BOOK_ID)
                        .from(BOOK_AUTHORS)
                        .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
                )
            )
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

}
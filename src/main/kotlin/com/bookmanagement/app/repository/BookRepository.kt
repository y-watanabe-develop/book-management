package com.bookmanagement.app.repository

import com.bookmanagement.app.domain.enums.PublishStatus
import com.bookmanagement.app.dto.AuthorResponse
import com.bookmanagement.app.dto.BookRequest
import com.bookmanagement.app.dto.BookResponse
import com.bookmanagement.infrastructure.jooq.tables.references.AUTHORS
import com.bookmanagement.infrastructure.jooq.tables.references.BOOKS
import com.bookmanagement.infrastructure.jooq.tables.references.BOOK_AUTHORS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val dsl: DSLContext) {

    fun create(request: BookRequest): BookResponse {
        val bookRecord = dsl.insertInto(BOOKS)
            .set(BOOKS.TITLE, request.title)
            .set(BOOKS.PRICE, request.price)
            .returning()
            .fetchOne()!!

        dsl.batch(
            request.authorIds.map { authorId ->
                dsl.insertInto(BOOK_AUTHORS)
                    .set(BOOK_AUTHORS.BOOK_ID, bookRecord.id)
                    .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
            }
        ).execute()

        return findByIdWithAuthors(bookRecord.id!!)!!
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

        dsl.batch(
            request.authorIds.map { authorId ->
                dsl.insertInto(BOOK_AUTHORS)
                    .set(BOOK_AUTHORS.BOOK_ID, id)
                    .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
            }
        ).execute()

        return findByIdWithAuthors(id)!!
    }

    fun updatePublishStatus(id: Long, status: PublishStatus) {
        dsl.update(BOOKS)
            .set(BOOKS.PUBLISH_STATUS, status.name)
            .where(BOOKS.ID.eq(id))
            .execute()
    }

    fun findAllWithAuthors(): List<BookResponse> =
        dsl.select(
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
            .toBookResponses()

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
        return records.toBookResponses().first()
    }

    fun findByAuthorId(authorId: Long): List<BookResponse> {
        val exists = dsl.fetchExists(dsl.selectFrom(AUTHORS).where(AUTHORS.ID.eq(authorId)))
        if (!exists) throw NoSuchElementException("Author not found: $authorId")

        val baFilter = BOOK_AUTHORS.`as`("ba_filter")
        return dsl.select(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLISH_STATUS,
            AUTHORS.ID,
            AUTHORS.NAME,
            AUTHORS.BIRTH_DATE
        )
            .from(BOOKS)
            .join(baFilter).on(BOOKS.ID.eq(baFilter.BOOK_ID).and(baFilter.AUTHOR_ID.eq(authorId)))
            .join(BOOK_AUTHORS).on(BOOKS.ID.eq(BOOK_AUTHORS.BOOK_ID))
            .join(AUTHORS).on(AUTHORS.ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .fetch()
            .toBookResponses()
    }

    private fun List<Record>.toBookResponses(): List<BookResponse> =
        groupBy { it[BOOKS.ID] }
            .map { (_, bookRecords) ->
                val first = bookRecords.first()
                BookResponse(
                    id = first[BOOKS.ID]!!,
                    title = first[BOOKS.TITLE]!!,
                    price = first[BOOKS.PRICE]!!,
                    publishStatus = PublishStatus.valueOf(first[BOOKS.PUBLISH_STATUS]!!),
                    authors = bookRecords.map { record ->
                        AuthorResponse(
                            id = record[AUTHORS.ID]!!,
                            name = record[AUTHORS.NAME]!!,
                            birthDate = record[AUTHORS.BIRTH_DATE]!!
                        )
                    }
                )
            }
}

package com.bookmanagement.app.repository

import com.bookmanagement.app.domain.enums.PublishStatus
import com.bookmanagement.app.dto.AuthorResponse
import com.bookmanagement.app.dto.BookRequest
import com.bookmanagement.app.dto.BookResponse
import com.bookmanagement.infrastructure.jooq.tables.references.AUTHORS
import com.bookmanagement.infrastructure.jooq.tables.references.BOOKS
import com.bookmanagement.infrastructure.jooq.tables.references.BOOK_AUTHORS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val dsl: DSLContext) {

    fun create(request: BookRequest): BookResponse {
        val bookRecord = dsl.insertInto(BOOKS)
            .set(BOOKS.TITLE, request.title)
            .set(BOOKS.PRICE, request.price)
            .returning()
            .fetchOne()!!

        request.authorIds.forEach { authorId ->
            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, bookRecord.id)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                .execute()
        }

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

        request.authorIds.forEach { authorId ->
            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, id)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                .execute()
        }

        return findByIdWithAuthors(id)!!
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
            id = first[BOOKS.ID]!!,
            title = first[BOOKS.TITLE]!!,
            price = first[BOOKS.PRICE]!!,
            publishStatus = PublishStatus.valueOf(first[BOOKS.PUBLISH_STATUS]!!),
            authors = records.map { record ->
                AuthorResponse(
                    id = record[AUTHORS.ID]!!,
                    name = record[AUTHORS.NAME]!!,
                    birthDate = record[AUTHORS.BIRTH_DATE]!!
                )
            }
        )
    }
}

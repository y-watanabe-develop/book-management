package com.bookmanagement.app.repository

import com.bookmanagement.app.domain.enums.PublishStatus
import com.bookmanagement.app.dto.AuthorRequest
import com.bookmanagement.app.dto.AuthorResponse
import com.bookmanagement.app.dto.BookResponse
import com.bookmanagement.infrastructure.jooq.tables.references.AUTHORS
import com.bookmanagement.infrastructure.jooq.tables.references.BOOKS
import com.bookmanagement.infrastructure.jooq.tables.references.BOOK_AUTHORS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(private val dsl: DSLContext) {

    fun create(request: AuthorRequest): AuthorResponse {
        val record = dsl.insertInto(AUTHORS)
            .set(AUTHORS.NAME, request.name)
            .set(AUTHORS.BIRTH_DATE, request.birthDate)
            .returning()
            .fetchOne()!!

        return AuthorResponse(
            id = record.id!!,
            name = record.name,
            birthDate = record.birthDate
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
            id = record.id!!,
            name = record.name,
            birthDate = record.birthDate
        )
    }

    fun findAll(): List<AuthorResponse> =
        dsl.selectFrom(AUTHORS)
            .fetch { record ->
                AuthorResponse(
                    id = record.id!!,
                    name = record.name,
                    birthDate = record.birthDate
                )
            }

    fun findById(id: Long): AuthorResponse {
        val record = dsl.selectFrom(AUTHORS)
            .where(AUTHORS.ID.eq(id))
            .fetchOne()
            ?: throw NoSuchElementException("Author not found: $id")

        return AuthorResponse(
            id = record.id!!,
            name = record.name,
            birthDate = record.birthDate
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

}

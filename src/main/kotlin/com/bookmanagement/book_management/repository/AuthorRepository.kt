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
        val record = dsl.insertInto(AUTHORS)
            .set(AUTHORS.NAME, request.name)
            .set(AUTHORS.BIRTH_DATE, request.birthDate)
            .returning()
            .fetchOne()!!

        return AuthorResponse(
            id = record.id!!,
            name = record.name!!,
            birthDate = record.birthDate!!
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
            name = record.name!!,
            birthDate = record.birthDate!!
        )
    }

    fun findBooksByAuthorId(authorId: Long): List<BookResponse> {
        return dsl.select(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLISH_STATUS
        )
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOKS.ID.eq(BOOK_AUTHORS.BOOK_ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch { record ->
                BookResponse(
                    id = record[BOOKS.ID]!!,
                    title = record[BOOKS.TITLE]!!,
                    price = record[BOOKS.PRICE]!!,
                    publishStatus = PublishStatus.valueOf(record[BOOKS.PUBLISH_STATUS]!!),
                    // TODO: 自己参照のため要リファクタリング
                    authors = findAuthorsByBookId(record[BOOKS.ID]!!)
                )
            }
    }

    fun findAuthorsByBookId(bookId: Long): List<AuthorResponse> {
        return dsl.select(
            AUTHORS.ID,
            AUTHORS.NAME,
            AUTHORS.BIRTH_DATE
        )
            .from(AUTHORS)
            .join(BOOK_AUTHORS).on(AUTHORS.ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .fetch { record ->
                AuthorResponse(
                    id = record[AUTHORS.ID]!!,
                    name = record[AUTHORS.NAME]!!,
                    birthDate = record[AUTHORS.BIRTH_DATE]!!
                )
            }
    }
}
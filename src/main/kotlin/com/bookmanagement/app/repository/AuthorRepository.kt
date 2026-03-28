package com.bookmanagement.app.repository

import com.bookmanagement.app.dto.AuthorRequest
import com.bookmanagement.app.dto.AuthorResponse
import com.bookmanagement.infrastructure.jooq.tables.references.AUTHORS
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
}

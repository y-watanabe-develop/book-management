package com.bookmanagement.bookmanagement.controller

import com.bookmanagement.bookmanagement.domain.enums.PublishStatus
import com.bookmanagement.bookmanagement.dto.AuthorRequest
import com.bookmanagement.bookmanagement.dto.AuthorResponse
import com.bookmanagement.bookmanagement.dto.BookResponse
import com.bookmanagement.bookmanagement.service.AuthorService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate

@Suppress("DEPRECATION")
@ExtendWith(MockitoExtension::class)
class AuthorControllerTest {

    @Mock
    private lateinit var authorService: AuthorService

    @InjectMocks
    private lateinit var authorController: AuthorController

    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorController)
            .setControllerAdvice(GlobalExceptionHandler())
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .build()
    }

    private val sampleAuthor = AuthorResponse(
        id = 1L,
        name = "テスト著者",
        birthDate = LocalDate.of(1990, 1, 1)
    )

    @Test
    fun `著者一覧を取得できる`() {
        given(authorService.findAll()).willReturn(listOf(sampleAuthor))

        mockMvc.get("/api/authors")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].name") { value("テスト著者") }
            }
    }

    @Test
    fun `著者を1件取得できる`() {
        given(authorService.findById(1L)).willReturn(sampleAuthor)

        mockMvc.get("/api/authors/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value("テスト著者") }
            }
    }

    @Test
    fun `存在しない著者IDで取得すると404が返る`() {
        given(authorService.findById(999L)).willThrow(NoSuchElementException("Author not found: 999"))

        mockMvc.get("/api/authors/999")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `著者を登録すると201が返る`() {
        val request = AuthorRequest(name = "テスト著者", birthDate = LocalDate.of(1990, 1, 1))
        given(authorService.create(request)).willReturn(sampleAuthor)

        mockMvc.post("/api/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `名前が空で登録すると400が返る`() {
        val request = mapOf("name" to "", "birthDate" to "1990-01-01")

        mockMvc.post("/api/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `未来の生年月日で登録すると400が返る`() {
        val request = mapOf("name" to "テスト著者", "birthDate" to "2099-01-01")

        mockMvc.post("/api/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `著者を更新できる`() {
        val request = AuthorRequest(name = "更新著者", birthDate = LocalDate.of(1995, 5, 5))
        given(authorService.update(1L, request)).willReturn(sampleAuthor.copy(name = "更新著者"))

        mockMvc.put("/api/authors/1") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `存在しない著者IDで更新すると404が返る`() {
        val request = AuthorRequest(name = "更新著者", birthDate = LocalDate.of(1995, 5, 5))
        given(authorService.update(999L, request)).willThrow(NoSuchElementException("Author not found: 999"))

        mockMvc.put("/api/authors/999") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `著者に紐づく書籍一覧を取得できる`() {
        val books = listOf(
            BookResponse(
                id = 1L,
                title = "テスト書籍",
                price = 1000,
                publishStatus = PublishStatus.UNPUBLISHED,
                authors = listOf(sampleAuthor)
            )
        )
        given(authorService.getBooksByAuthor(1L)).willReturn(books)

        mockMvc.get("/api/authors/1/books")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].title") { value("テスト書籍") }
            }
    }

    @Test
    fun `存在しない著者IDで書籍一覧を取得すると404が返る`() {
        given(authorService.getBooksByAuthor(999L)).willThrow(NoSuchElementException("Author not found: 999"))

        mockMvc.get("/api/authors/999/books")
            .andExpect {
                status { isNotFound() }
            }
    }
}

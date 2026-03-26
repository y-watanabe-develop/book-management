package com.bookmanagement.app.controller

import com.bookmanagement.app.domain.enums.PublishStatus
import com.bookmanagement.app.dto.AuthorResponse
import com.bookmanagement.app.dto.BookRequest
import com.bookmanagement.app.dto.BookResponse
import com.bookmanagement.app.service.BookService
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
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate

@Suppress("DEPRECATION")
@ExtendWith(MockitoExtension::class)
class BookControllerTest {

    @Mock
    private lateinit var bookService: BookService

    @InjectMocks
    private lateinit var bookController: BookController

    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
            .setControllerAdvice(GlobalExceptionHandler())
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .build()
    }

    private val sampleAuthor = AuthorResponse(
        id = 1L,
        name = "テスト著者",
        birthDate = LocalDate.of(1990, 1, 1)
    )

    private val sampleBook = BookResponse(
        id = 1L,
        title = "テスト書籍",
        price = 1000,
        publishStatus = PublishStatus.UNPUBLISHED,
        authors = listOf(sampleAuthor)
    )

    @Test
    fun `書籍一覧を取得できる`() {
        given(bookService.findAll()).willReturn(listOf(sampleBook))

        mockMvc.get("/api/books")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].title") { value("テスト書籍") }
            }
    }

    @Test
    fun `書籍を1件取得できる`() {
        given(bookService.findById(1L)).willReturn(sampleBook)

        mockMvc.get("/api/books/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.title") { value("テスト書籍") }
                jsonPath("$.price") { value(1000) }
            }
    }

    @Test
    fun `存在しない書籍IDで取得すると404が返る`() {
        given(bookService.findById(999L)).willThrow(NoSuchElementException("Book not found: 999"))

        mockMvc.get("/api/books/999")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `書籍を登録すると201が返る`() {
        val request = BookRequest(title = "テスト書籍", price = 1000, authorIds = listOf(1L))
        given(bookService.create(request)).willReturn(sampleBook)

        mockMvc.post("/api/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `タイトルが空で登録すると400が返る`() {
        val request = mapOf("title" to "", "price" to 1000, "authorIds" to listOf(1))

        mockMvc.post("/api/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `価格が負の値で登録すると400が返る`() {
        val request = mapOf("title" to "テスト書籍", "price" to -1, "authorIds" to listOf(1))

        mockMvc.post("/api/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `著者IDが空で登録すると400が返る`() {
        val request = mapOf("title" to "テスト書籍", "price" to 1000, "authorIds" to emptyList<Long>())

        mockMvc.post("/api/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `書籍を更新できる`() {
        val request = BookRequest(title = "更新書籍", price = 2000, authorIds = listOf(1L))
        given(bookService.update(1L, request)).willReturn(sampleBook.copy(title = "更新書籍", price = 2000))

        mockMvc.put("/api/books/1") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `存在しない書籍IDで更新すると404が返る`() {
        val request = BookRequest(title = "更新書籍", price = 2000, authorIds = listOf(1L))
        given(bookService.update(999L, request)).willThrow(NoSuchElementException("Book not found: 999"))

        mockMvc.put("/api/books/999") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `書籍を出版済みに変更すると200が返る`() {
        given(bookService.publish(1L)).willReturn(sampleBook.copy(publishStatus = PublishStatus.PUBLISHED))

        mockMvc.patch("/api/books/1/publish")
            .andExpect {
                status { isOk() }
                jsonPath("$.publishStatus") { value("PUBLISHED") }
            }
    }

    @Test
    fun `出版済みを再出版すると409が返る`() {
        given(bookService.publish(1L)).willThrow(IllegalStateException("Book is already published"))

        mockMvc.patch("/api/books/1/publish")
            .andExpect {
                status { isConflict() }
            }
    }
}

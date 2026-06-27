package com.kairev.skillforge

import com.kairev.skillforge.data.model.Category
import com.kairev.skillforge.data.model.Course
import com.kairev.skillforge.data.model.Instructor
import com.kairev.skillforge.data.model.Lesson
import com.kairev.skillforge.data.model.SkillforgeResponse
import com.kairev.skillforge.data.remote.ApiService
import com.kairev.skillforge.data.repository.CourseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CourseRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: CourseRepository

    private val fakeInstructor = Instructor(
        id = "inst_1",
        name = "Test Instructor",
        title = "Senior Dev",
        avatarUrl = "https://example.com/avatar.png",
        bio = "Expert developer."
    )

    private val fakeLesson = Lesson(
        id = "les_1",
        title = "Intro Lesson",
        durationMinutes = 10,
        isFree = true,
        videoUrl = "https://example.com/video",
        content = "Welcome to the course."
    )

    private val fakeCourse = Course(
        id = "course_1",
        title = "Test Course",
        subtitle = "A test subtitle",
        thumbnailUrl = "https://example.com/thumb.png",
        level = "Beginner",
        durationHours = 5.0,
        rating = 4.5,
        studentsEnrolled = 1000,
        language = "English",
        lastUpdated = "2026-01-01",
        tags = listOf("Kotlin", "Android"),
        instructor = fakeInstructor,
        description = "Learn everything.",
        lessons = listOf(fakeLesson)
    )

    private val fakeCategory = Category(
        id = "cat_1",
        name = "Android Development",
        description = "Build Android apps.",
        iconColor = "#2dd4bf",
        courseCount = 1,
        courses = listOf(fakeCourse)
    )

    private val fakeResponse = SkillforgeResponse(categories = listOf(fakeCategory))

    @Before
    fun setUp() {
        apiService = mockk()
        repository = CourseRepository(apiService)
    }

    @Test
    fun `getCourses returns success when API responds correctly`() = runTest {
        // Given
        coEvery { apiService.getCourses() } returns fakeResponse

        // When
        val result = repository.getCourses()

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        assertEquals(1, data.categories.size)
        assertEquals("Android Development", data.categories[0].name)
        assertEquals("Test Course", data.categories[0].courses[0].title)
    }

    @Test
    fun `getCourses returns failure when API throws exception`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { apiService.getCourses() } throws RuntimeException(errorMessage)

        // When
        val result = repository.getCourses()

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getCourses returns correct lesson data`() = runTest {
        // Given
        coEvery { apiService.getCourses() } returns fakeResponse

        // When
        val result = repository.getCourses()

        // Then
        assertTrue(result.isSuccess)
        val lesson = result.getOrNull()!!.categories[0].courses[0].lessons[0]
        assertEquals("Intro Lesson", lesson.title)
        assertEquals(10, lesson.durationMinutes)
        assertTrue(lesson.isFree)
    }

    @Test
    fun `getCourses returns correct instructor data`() = runTest {
        // Given
        coEvery { apiService.getCourses() } returns fakeResponse

        // When
        val result = repository.getCourses()

        // Then
        assertTrue(result.isSuccess)
        val instructor = result.getOrNull()!!.categories[0].courses[0].instructor
        assertEquals("Test Instructor", instructor.name)
        assertEquals("Senior Dev", instructor.title)
    }

    @Test
    fun `getCourses course has correct tags`() = runTest {
        // Given
        coEvery { apiService.getCourses() } returns fakeResponse

        // When
        val result = repository.getCourses()

        // Then
        assertTrue(result.isSuccess)
        val tags = result.getOrNull()!!.categories[0].courses[0].tags
        assertEquals(2, tags.size)
        assertTrue(tags.contains("Kotlin"))
        assertTrue(tags.contains("Android"))
    }
}

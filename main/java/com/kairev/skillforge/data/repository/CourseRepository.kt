package com.kairev.skillforge.data.repository

import com.kairev.skillforge.data.model.SkillforgeResponse
import com.kairev.skillforge.data.remote.ApiService

class CourseRepository(private val api: ApiService) {

    suspend fun getCourses(): Result<SkillforgeResponse> {
        return try {
            val response = api.getCourses()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

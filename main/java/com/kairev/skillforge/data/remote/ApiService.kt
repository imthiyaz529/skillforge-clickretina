package com.kairev.skillforge.data.remote

import com.kairev.skillforge.data.model.SkillforgeResponse
import retrofit2.http.GET

interface ApiService {
    @GET("android-assesment/notes/refs/heads/main/data.json")
    suspend fun getCourses(): SkillforgeResponse
}

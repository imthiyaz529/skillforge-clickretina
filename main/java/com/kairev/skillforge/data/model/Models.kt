package com.kairev.skillforge.data.model

import com.google.gson.annotations.SerializedName

data class SkillforgeResponse(
    @SerializedName("categories") val categories: List<Category>
)

data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("iconColor") val iconColor: String,
    @SerializedName("courseCount") val courseCount: Int,
    @SerializedName("courses") val courses: List<Course>
)

data class Course(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String,
    @SerializedName("level") val level: String,
    @SerializedName("durationHours") val durationHours: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("studentsEnrolled") val studentsEnrolled: Int,
    @SerializedName("language") val language: String,
    @SerializedName("lastUpdated") val lastUpdated: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("instructor") val instructor: Instructor,
    @SerializedName("description") val description: String,
    @SerializedName("lessons") val lessons: List<Lesson>
)

data class Instructor(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String,
    @SerializedName("avatarUrl") val avatarUrl: String,
    @SerializedName("bio") val bio: String
)

data class Lesson(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("durationMinutes") val durationMinutes: Int,
    @SerializedName("isFree") val isFree: Boolean,
    @SerializedName("videoUrl") val videoUrl: String,
    @SerializedName("content") val content: String
)

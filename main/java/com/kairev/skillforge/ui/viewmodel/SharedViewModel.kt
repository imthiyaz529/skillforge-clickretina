package com.kairev.skillforge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairev.skillforge.data.model.Category
import com.kairev.skillforge.data.model.Course
import com.kairev.skillforge.data.remote.RetrofitInstance
import com.kairev.skillforge.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val categories: List<Category>) : UiState()
    data class Error(val message: String) : UiState()
}

class SharedViewModel(
    private val repository: CourseRepository = CourseRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Search query for Home screen
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        fetchCourses()
    }

    fun fetchCourses() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getCourses().fold(
                onSuccess = { response ->
                    _uiState.value = UiState.Success(response.categories)
                },
                onFailure = { e ->
                    _uiState.value = UiState.Error(e.message ?: "Unknown error")
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun getCourse(categoryIndex: Int, courseIndex: Int): Course? {
        val state = _uiState.value
        if (state is UiState.Success) {
            return state.categories.getOrNull(categoryIndex)?.courses?.getOrNull(courseIndex)
        }
        return null
    }

    fun getAllCourses(): List<Pair<Int, Course>> {
        val state = _uiState.value
        if (state is UiState.Success) {
            return state.categories.flatMapIndexed { catIdx, category ->
                category.courses.map { course -> Pair(catIdx, course) }
            }
        }
        return emptyList()
    }

    fun getFilteredCourses(query: String): List<Pair<Int, Course>> {
        val all = getAllCourses()
        if (query.isBlank()) return all
        return all.filter { (_, course) ->
            course.title.contains(query, ignoreCase = true) ||
            course.instructor.name.contains(query, ignoreCase = true) ||
            course.tags.any { it.contains(query, ignoreCase = true) }
        }
    }
}

package com.kairev.skillforge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kairev.skillforge.data.model.Course
import com.kairev.skillforge.data.model.Instructor
import com.kairev.skillforge.data.model.Lesson
import com.kairev.skillforge.ui.theme.*
import com.kairev.skillforge.ui.viewmodel.SharedViewModel
import com.kairev.skillforge.ui.viewmodel.UiState

@Composable
fun CourseDetailScreen(
    viewModel: SharedViewModel,
    categoryIndex: Int,
    courseIndex: Int,
    onBack: () -> Unit,
    onLessonClick: (lessonIndex: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Cream)) {
        when (val state = uiState) {
            is UiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Teal600
            )
            is UiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Failed to load", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = onBack) { Text("Go back") }
                }
            }
            is UiState.Success -> {
                val course = state.categories.getOrNull(categoryIndex)
                    ?.courses?.getOrNull(courseIndex)

                if (course == null) {
                    Text("Course not found", modifier = Modifier.align(Alignment.Center))
                } else {
                    CourseDetailContent(
                        course = course,
                        categoryName = state.categories[categoryIndex].name,
                        onBack = onBack,
                        onLessonClick = onLessonClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseDetailContent(
    course: Course,
    categoryName: String,
    onBack: () -> Unit,
    onLessonClick: (Int) -> Unit
) {
    val totalMinutes = course.lessons.sumOf { it.durationMinutes }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Teal700.copy(alpha = 0.7f)
                                ),
                                startY = 100f
                            )
                        )
                )
                // Top buttons row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .statusBarsPadding()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                // Category tag + title at bottom of hero
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(White.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "// ${categoryName.lowercase().split(" ").first()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                    // Tags row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        course.tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(White.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Course info section
        item {
            Column(modifier = Modifier.background(White).padding(20.dp)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = course.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(14.dp))

                // Stats row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatChip(icon = "★", value = course.rating.toString(), color = StarColor)
                    Text(
                        text = "${formatEnrolled(course.studentsEnrolled)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    StatChip(icon = "⏱", value = "${course.durationHours}h")
                    LevelBadge(level = course.level)
                }
            }
        }

        // Instructor card
        item {
            Spacer(Modifier.height(8.dp))
            InstructorCard(instructor = course.instructor)
        }

        // Description
        item {
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .background(White)
                    .padding(20.dp)
            ) {
                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            }
        }

        // Course content header
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Course content",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "${course.lessons.size} lessons · $totalMinutes min",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Lessons list
        itemsIndexed(course.lessons) { index, lesson ->
            LessonRow(
                lesson = lesson,
                lessonNumber = index + 1,
                onClick = { if (lesson.isFree) onLessonClick(index) }
            )
            if (index < course.lessons.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = BorderLight
                )
            }
        }

        // Enroll button
        item {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PRICE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Free",
                        style = MaterialTheme.typography.titleLarge,
                        color = Teal600,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Teal600),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Enroll now",
                        style = MaterialTheme.typography.labelLarge,
                        color = White
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(icon: String, value: String, color: Color = TextSecondary) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 13.sp, color = color)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = if (color == StarColor) TextPrimary else TextSecondary,
            fontWeight = if (color == StarColor) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun InstructorCard(instructor: Instructor) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AsyncImage(
                model = instructor.avatarUrl,
                contentDescription = instructor.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(TealLight)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = instructor.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = instructor.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal600),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Follow",
                    style = MaterialTheme.typography.labelMedium,
                    color = Teal600
                )
            }
        }
    }
}

@Composable
private fun LessonRow(lesson: Lesson, lessonNumber: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (lesson.isFree) TealLight else SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (lesson.isFree) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Teal600,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TextHint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleSmall,
                color = if (lesson.isFree) TextPrimary else TextSecondary
            )
            Text(
                text = "${lesson.durationMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = TextHint
            )
        }
        if (lesson.isFree) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TealLight)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "FREE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Teal600,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatEnrolled(count: Int): String {
    return if (count >= 1000) "${String.format("%.1f", count / 1000.0)}K" else count.toString()
}

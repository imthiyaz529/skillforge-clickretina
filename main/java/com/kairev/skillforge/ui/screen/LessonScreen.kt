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
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kairev.skillforge.data.model.Course
import com.kairev.skillforge.data.model.Lesson
import com.kairev.skillforge.ui.theme.*
import com.kairev.skillforge.ui.viewmodel.SharedViewModel
import com.kairev.skillforge.ui.viewmodel.UiState

@Composable
fun LessonScreen(
    viewModel: SharedViewModel,
    categoryIndex: Int,
    courseIndex: Int,
    lessonIndex: Int,
    onBack: () -> Unit,
    onLessonClick: (Int) -> Unit
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Failed to load", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onBack) { Text("Go back") }
                }
            }
            is UiState.Success -> {
                val course = state.categories.getOrNull(categoryIndex)
                    ?.courses?.getOrNull(courseIndex)
                val lesson = course?.lessons?.getOrNull(lessonIndex)
                val categoryName = state.categories.getOrNull(categoryIndex)?.name ?: ""
                if (course == null || lesson == null) {
                    Text("Lesson not found", modifier = Modifier.align(Alignment.Center))
                } else {
                    LessonContent(
                        course = course,
                        lesson = lesson,
                        lessonIndex = lessonIndex,
                        categoryName = categoryName,
                        onBack = onBack,
                        onLessonClick = onLessonClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonContent(
    course: Course,
    lesson: Lesson,
    lessonIndex: Int,
    categoryName: String,
    onBack: () -> Unit,
    onLessonClick: (Int) -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.37f) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lessons", "Notes", "Resources")

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = "Video background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(White)
                    .align(Alignment.Center)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Teal600,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            ) {
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Teal500,
                        activeTrackColor = Teal500,
                        inactiveTrackColor = White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentSec = (progress * lesson.durationMinutes * 60).toInt()
                    Text(
                        text = formatTime(currentSec),
                        style = MaterialTheme.typography.labelSmall,
                        color = White
                    )
                    Text(
                        text = formatTime(lesson.durationMinutes * 60),
                        style = MaterialTheme.typography.labelSmall,
                        color = White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().background(White),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "LESSON ${lessonIndex + 1} · ${course.title.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Teal600,
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = lesson.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                }
            }

            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = White,
                    contentColor = Teal600
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedTab == index) Teal600 else TextSecondary
                                )
                            }
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    itemsIndexed(course.lessons) { index, lessonItem ->
                        val isCurrentLesson = index == lessonIndex
                        LessonPlayerRow(
                            lesson = lessonItem,
                            isCurrentLesson = isCurrentLesson,
                            isPlaying = isPlaying && isCurrentLesson,
                            onClick = {
                                if (lessonItem.isFree || isCurrentLesson) onLessonClick(index)
                            }
                        )
                        if (index < course.lessons.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = BorderLight
                            )
                        }
                    }
                }
                1 -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Notes will appear here while you learn.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextHint
                            )
                        }
                    }
                }
                2 -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No resources attached to this lesson.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextHint
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonPlayerRow(
    lesson: Lesson,
    isCurrentLesson: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isCurrentLesson) TealLight else White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isCurrentLesson) Teal600 else SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCurrentLesson -> Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(18.dp)
                )
                lesson.isFree -> Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                else -> Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextHint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isCurrentLesson) Teal700 else TextPrimary,
                fontWeight = if (isCurrentLesson) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (isCurrentLesson) "Now playing · ${lesson.durationMinutes} min"
                else "${lesson.durationMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = if (isCurrentLesson) Teal600 else TextHint
            )
        }
        if (lesson.isFree && !isCurrentLesson) {
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

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
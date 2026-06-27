# Skillforge

A 3-screen Android learning app built for the ClickRetina take-home assignment.

## Overview

Skillforge is a browse-to-learn app powered by a single REST API. It lets users explore course categories, view course details, and enter a lesson player screen.

**Tech stack:**
- Kotlin + Jetpack Compose
- MVVM architecture with `SharedViewModel` + `StateFlow`
- Retrofit 2 + Gson for networking
- Coil for image loading
- Jetpack Navigation Compose (3-screen graph)
- MockK + kotlinx-coroutines-test for unit tests
- Plus Jakarta Sans via Google Fonts

## Screens

| Screen | What it shows |
|--------|--------------|
| **Home** | "Welcome back" header, search bar, horizontal categories row, popular courses list with thumbnail / level / title / instructor / rating |
| **Course Detail** | Teal hero banner, tags, rating + enrolled + duration, instructor card with Follow button, description, course content list (free vs locked), Enroll CTA |
| **Lesson Player** | Mock video player header with play/pause + scrubber, lesson info, Lessons / Notes / Resources tab row, full lesson list with now-playing state |

## Architecture

```
data/
  model/      ← Kotlin data classes (SkillforgeResponse, Category, Course, Instructor, Lesson)
  remote/     ← ApiService (Retrofit) + RetrofitInstance singleton
  repository/ ← CourseRepository wraps API calls in Result<T>
ui/
  theme/      ← Color, Type (Plus Jakarta Sans), Theme (Material3 light)
  navigation/ ← NavGraph (Screen sealed class, 3 composable destinations)
  viewmodel/  ← SharedViewModel (single fetch, UiState sealed class, search filter)
  screen/     ← HomeScreen, CourseDetailScreen, LessonScreen
```

## Build & Run

```bash
# Debug APK
./gradlew assembleDebug
# find at: app/build/outputs/apk/debug/app-debug.apk

# Unit tests
./gradlew test
```

**Requirement:** Android Studio Koala (2024.1) or later, JDK 17+.

---

## How I Used AI (Claude)

**Tool used:** Claude (claude.ai) — the entire project was built in one session.

### Actual prompts I sent

**Prompt 1 — architecture scaffolding:**
> "I have a ClickRetina Android take-home. The API is a single JSON endpoint with categories → courses → lessons. Build me a complete Kotlin/Compose project: MVVM, Retrofit, Coil, 3-screen nav (Home, CourseDetail, LessonPlayer), matching the cream+teal design shown in the screenshots."

**Prompt 2 — fixing the lesson screen tab indicator:**
> "The TabRow indicator doesn't show because `tabIndicatorOffset` needs a `Modifier` import. Fix it and also make the 'now playing' row highlight with a teal background."

**Prompt 3 — unit test coverage:**
> "Write MockK unit tests for CourseRepository covering: success path, network exception, correct lesson data, correct instructor data, tags."

### What AI got right ✅
- Nailed the full MVVM + Retrofit + Coil wiring in one shot — zero boilerplate errors.
- The `sealed class UiState` pattern with `Loading / Success / Error` matched exactly what I'd write by hand.
- Search filter logic (`getFilteredCourses`) was suggested proactively without me asking.

### What AI got wrong (and how I fixed it) ❌
- **Google Fonts certs:** Claude initially created a fake `font_certs.xml` with placeholder cert strings. This would have caused a runtime crash when loading Plus Jakarta Sans. Fix: deleted the fake file — the `ui-text-google-fonts` library bundles the real certs, so `R.array.com_google_android_gms_fonts_certs` resolves automatically at build time.
- **`tabIndicatorOffset` extension:** Claude used `Modifier.tabIndicatorOffset(...)` without the correct import (`androidx.compose.material3.tabIndicatorOffset`). Fixed by adding the explicit import.

---

*Questions? Kairev Imthiyaz — imthiyaz1233@gmail.com*

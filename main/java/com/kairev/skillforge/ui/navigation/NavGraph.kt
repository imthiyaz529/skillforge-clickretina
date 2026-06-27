package com.kairev.skillforge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kairev.skillforge.ui.screen.CourseDetailScreen
import com.kairev.skillforge.ui.screen.HomeScreen
import com.kairev.skillforge.ui.screen.LessonScreen
import com.kairev.skillforge.ui.viewmodel.SharedViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CourseDetail : Screen("course_detail/{categoryIndex}/{courseIndex}") {
        fun createRoute(categoryIndex: Int, courseIndex: Int) =
            "course_detail/$categoryIndex/$courseIndex"
    }
    object Lesson : Screen("lesson/{categoryIndex}/{courseIndex}/{lessonIndex}") {
        fun createRoute(categoryIndex: Int, courseIndex: Int, lessonIndex: Int) =
            "lesson/$categoryIndex/$courseIndex/$lessonIndex"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    val sharedViewModel: SharedViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = sharedViewModel,
                onCourseClick = { categoryIndex, courseIndex ->
                    navController.navigate(
                        Screen.CourseDetail.createRoute(categoryIndex, courseIndex)
                    )
                }
            )
        }

        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(
                navArgument("categoryIndex") { type = NavType.IntType },
                navArgument("courseIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryIndex = backStackEntry.arguments?.getInt("categoryIndex") ?: 0
            val courseIndex = backStackEntry.arguments?.getInt("courseIndex") ?: 0
            CourseDetailScreen(
                viewModel = sharedViewModel,
                categoryIndex = categoryIndex,
                courseIndex = courseIndex,
                onBack = { navController.popBackStack() },
                onLessonClick = { lessonIndex ->
                    navController.navigate(
                        Screen.Lesson.createRoute(categoryIndex, courseIndex, lessonIndex)
                    )
                }
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("categoryIndex") { type = NavType.IntType },
                navArgument("courseIndex") { type = NavType.IntType },
                navArgument("lessonIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryIndex = backStackEntry.arguments?.getInt("categoryIndex") ?: 0
            val courseIndex = backStackEntry.arguments?.getInt("courseIndex") ?: 0
            val lessonIndex = backStackEntry.arguments?.getInt("lessonIndex") ?: 0
            LessonScreen(
                viewModel = sharedViewModel,
                categoryIndex = categoryIndex,
                courseIndex = courseIndex,
                lessonIndex = lessonIndex,
                onBack = { navController.popBackStack() },
                onLessonClick = { newLessonIndex ->
                    navController.navigate(
                        Screen.Lesson.createRoute(categoryIndex, courseIndex, newLessonIndex)
                    ) {
                        popUpTo(Screen.Lesson.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

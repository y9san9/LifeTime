package me.y9san9.lifetime.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.y9san9.lifetime.core.screen.Screen

@Composable
fun NavHost(startScreen: Screen, other: List<Screen>) {
    val screens = (other + startScreen).distinctBy { it.name }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startScreen.name
    ) {
        for (screen in screens) {
            composable(
                route = screen.name,
                arguments = screen.arguments,
                content = { screen.Content(navController, it) }
            )
        }
    }
}

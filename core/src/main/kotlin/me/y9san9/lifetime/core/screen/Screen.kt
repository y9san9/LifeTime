package me.y9san9.lifetime.core.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

interface Screen {
    val name: String get() = this::class.simpleName
        ?: error("Cannot automatically infer screen name")

    val arguments: List<NamedNavArgument> get() = emptyList()

    @Composable
    fun Content(controller: NavController, entry: NavBackStackEntry)
}

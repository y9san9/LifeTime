package me.y9san9.lifetime.core.android

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

@Composable
inline fun <reified T : Any> viewModel(
    crossinline factory: (base: ViewModel) -> T
): T {
    val container = ViewModelContainer<T>()

    val vmFactory = viewModelFactory {
        initializer {
            container.viewModel = factory(container)
            container
        }
    }

    return androidx.lifecycle.viewmodel.compose.viewModel(
        factory = vmFactory
    )
}

open class ViewModelContainer<T : Any> : ViewModel() {
    lateinit var viewModel: T
}

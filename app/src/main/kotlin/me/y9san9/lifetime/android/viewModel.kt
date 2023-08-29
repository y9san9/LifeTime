package me.y9san9.lifetime.android

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

inline fun <reified T : Any> ComponentActivity.viewModel(
    crossinline factory: (base: ViewModel) -> T
): Lazy<T> {
    val container = ViewModelContainer<T>()

    val vmFactory = viewModelFactory {
        initializer {
            container.viewModel = factory(container)
            container
        }
    }

    return lazy {
        viewModels<ViewModelContainer<T>> { vmFactory }.value.viewModel
    }
}

open class ViewModelContainer<T : Any> : ViewModel() {
    lateinit var viewModel: T
}

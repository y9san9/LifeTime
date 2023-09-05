package me.y9san9.lifetime.core.stdlib

import kotlinx.coroutines.flow.*

/**
 * @param transform may be called more than once for each element and
 *  must be idempotent
 */
inline fun <T, R> StateFlow<T>.mapState(
    crossinline transform: (T) -> R
): StateFlow<R> = object : StateFlow<R> {
    override val replayCache: List<R> get() = listOf(value)
    override val value: R get() = transform(this@mapState.value)
    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        this@mapState.map { value ->
            transform(value)
        }.distinctUntilChanged()
            .collect(collector)

        error("Unreachable code")
    }
}

inline fun <T1, T2, R> StateFlow<T1>.combine(
    flow2: StateFlow<T2>,
    crossinline combine: (T1, T2) -> R
): StateFlow<R> = combineStates(
    flows = listOf(this, flow2),
    combine = { array ->
        @Suppress("UNCHECKED_CAST")
        combine(
            array[0] as T1,
            array[1] as T2
        )
    }
)

inline fun <T, R> combineStates(
    flows: Iterable<StateFlow<T>>,
    crossinline combine: (List<T>) -> R
): StateFlow<R> = object : StateFlow<R> {
    override val replayCache get() = listOf(value)
    override val value: R get() = combine(values)

    private val values: List<T> get() = flows.map(StateFlow<T>::value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        combine<Any?, _>(flows) { array ->
            @Suppress("UNCHECKED_CAST")
            combine(array.toList() as List<T>)
        }.distinctUntilChanged().collect(collector)

        error("Unreachable code")
    }
}

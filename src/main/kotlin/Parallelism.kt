import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.withIndex

/**
 * # Parallelism
 *
 * Chaining parallel Flow operations, for instance filter and map, presents an opportunity to
 * execute both operations in parallel.
 *
 * ## References
 *
 * [Prefix sum - Wikipedia](https://en.wikipedia.org/wiki/Prefix_sum#Parallel_algorithms)
 *
 * [Parallel algorithm - Wikipedia](https://en.wikipedia.org/wiki/Parallel_algorithm)
 *
 * [Parallelism - The Java Tutorials](https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html)
 */
object Parallelism {
    /**
     * ## Parallel balanced reduce
     *
     * The same algorithm as in [Monoid.balancedReduce]
     */
    suspend fun <T> parallelBalancedReduce(list: List<T>, combine: suspend (T, T) -> T): T? = coroutineScope {
        if (list.isEmpty()) return@coroutineScope null
        if (list.size == 1) return@coroutineScope list.first()

        // Split the list in the middle
        val middle = list.size / 2
        val leftHalve = list.subList(0, middle)
        val rightHalve = list.subList(middle, list.size)

        val leftDeferred = async { parallelBalancedReduce(leftHalve, combine) }
        val rightDeferred = async { parallelBalancedReduce(rightHalve, combine) }

        val leftResult = leftDeferred.await()
        val rightResult = rightDeferred.await()

        return@coroutineScope if (leftResult != null && rightResult != null) combine(
            leftResult,
            rightResult
        ) else leftResult
            ?: rightResult
    }

    /**
     * ## Parallel map with List
     *
     * Creates a new coroutine for each element in the list. While the number of threads is limited by
     * the dispatcher, the number of coroutines is not.
     */
    suspend fun <T, R> parallelListMap(list: List<T>, transform: suspend (T) -> R): List<R> = coroutineScope {
        list.map {
            async { transform(it) }
        }.awaitAll() // Similar to map { it.await() } but fails eagerly
    }

    /**
     * ## Parallel map with List with constrained number of coroutines
     *
     * Allows limiting the number of coroutines created at once. This is useful when the number of elements
     * in the list is large. and you want to avoid creating too many coroutines at once.
     */
    suspend fun <T, R> parallelListMap(list: List<T>, maxCoroutines: Int, transform: suspend (T) -> R): List<R> =
        list.chunked(maxCoroutines).flatMap { chunk ->
            parallelListMap(chunk, transform)
        }

    suspend fun <T> parallelListFilter(
        list: List<T>,
        maxCoroutines: Int,
        predicate: suspend (T) -> Boolean
    ): List<T> =
        parallelListMap(list, maxCoroutines) { if (predicate(it)) it else null }.filterNotNull()

    /**
     * ## Parallel merge map with Flow and constrained number of coroutines
     *
     * The order of mapped emissions does not match the order of source emissions.
     *
     * Preserving the order of reactive stream emissions while processing in parallel is tricky.
     * For example, [flatMapConcat] does not support concurrency at all. [channelFlow] can potentially be
     * used for implementation that preserves the order. A good options is using [withIndex] to
     * source emissions with their indices and sort the collected emissions accordingly.
     *
     * Unlike the approach used for Lists, this function cannot rely on [coroutineScope], since it exits
     * before launching the coroutines. The approach here is using parallel collection for which
     * collecting on multithreaded dispatcher, such as Dispatchers.Default, is essential.
     */
    fun <T, R> parallelFlowMergeMap(flow: Flow<T>, maxCoroutines: Int, transform: suspend (T) -> R): Flow<R> =
        @OptIn(ExperimentalCoroutinesApi::class)
        flow.flatMapMerge(maxCoroutines) { value ->
            flow { emit(transform(value)) }
        }

    /**
     * ## Parallel merge filter with Flow and constrained number of coroutines
     *
     * The order of filtered emissions does not match the order of source emissions.
     *
     * @see [parallelFlowMergeMap]
     */
    fun <T> parallelFlowMergeFilter(flow: Flow<T>, maxCoroutines: Int, predicate: (T) -> Boolean) =
        @OptIn(ExperimentalCoroutinesApi::class)
        flow.flatMapMerge(maxCoroutines) { value ->
            flow { if (predicate(value)) emit(value) }
        }
}
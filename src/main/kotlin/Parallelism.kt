import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * ## References
 *
 * [Prefix sum - Wikipedia](https://en.wikipedia.org/wiki/Prefix_sum#Parallel_algorithms)
 *
 * [Parallel algorithm - Wikipedia](https://en.wikipedia.org/wiki/Parallel_algorithm)
 */
object Parallelism {
    /**
     * ## Parallel balanced reduce
     *
     * The same algorithm as [Monoid.balancedReduce]
     */
    suspend fun <T> parallelBalancedReduce(list: List<T>, combine: (T, T) -> T): T? = coroutineScope {
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
        }.map { it.await() }
    }

    /**
     * ## Parallel map with List and limiting the number of coroutines
     *
     * Allows limiting the number of coroutines created at once. This is useful when the number of elements
     * in the list is large. and you want to avoid creating too many coroutines at once.
     */
    suspend fun <T, R> parallelListMap(list: List<T>, maxCoroutineNumber: Int, transform: (T) -> R): List<R> =
        list.chunked(maxCoroutineNumber).flatMap { chunk ->
            parallelListMap(chunk, transform)
        }

    /**
     * ## Parallel shuffle map with Flow and limiting the number of coroutines
     *
     * The order of the mapped emissions does not match the order of the source emissions.
     *
     * Notice that the approach used for List cannot be used here, because coroutineScope will be exited
     * before the coroutines are launched. So the approach here is to use parallel collection for which
     * collecting on multithreaded dispatcher, such as Dispatchers.Default, is essential.
     */
    fun <T, R> parallelFlowShuffleMap(flow: Flow<T>, maxCoroutineNumber: Int, transform: suspend (T) -> R): Flow<R> =
        @OptIn(ExperimentalCoroutinesApi::class)
        flow.flatMapMerge(maxCoroutineNumber) { value ->
            flow { emit(transform(value)) }
        }
}
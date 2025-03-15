import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
}
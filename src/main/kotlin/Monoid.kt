import Monoid.combine
import arrow.core.fold
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold

/**
 * # Monoid
 *
 * Monoids shine when used with the _fold_ family of functions, also known as _reduce_ or _scan_.
 *
 * In this context, and following Kotlin's standard libraries:
 * - A function with an initial identity element is called _fold_.
 * - A function without an initial identity element is called _reduce_.
 * - Functions that produce intermediate results are prefixed with _running_.
 *
 * In Kotlin libraries and RxJava, _scan_ is an alias for _runningFold_.
 *
 * ## References
 *
 * [Monoid - Wikipedia](https://en.wikipedia.org/wiki/Monoid)
 *
 * [Commutative property - Wikipedia](https://en.wikipedia.org/wiki/Commutative_property)
 *
 * [Fold - Wikipedia](https://en.wikipedia.org/wiki/Fold_(higher-order_function))
 *
 * [Prefix sum - Wikipedia](https://en.wikipedia.org/wiki/Prefix_sum)
 */
object Monoid {
    /**
     * ## Nesting two monoid operations to merge multimaps
     *
     * _Nesting_ monoids is useful for folding hierarchical structures such as multimaps and nested maps.
     *
     * [Map.combine] is the outer operation, while [Collection.plus] is the nested one.
     */
    fun combineMergeMultimap(map1: Map<String, List<Int>>, map2: Map<String, List<Int>>): Map<String, List<Int>> =
        map1.combine(map2, Collection<Int>::plus)

    /**
     * ## Nesting three monoids operations to merge nested maps
     *
     * Since a monoid's associative operation is not necessarily commutative - such as [String::plus],
     * it is essential to preserve the order of parameters when delegating to a nested operation.
     */
    fun combineMergeNestedMaps(map1: Map<String, Map<Int, String>>, map2: Map<String, Map<Int, String>>)
            : Map<String, Map<Int, String>> = map1.combine(map2) { value1, value2 ->
        value1.combine(value2, String::plus)
    }

    // Arrow's Map.combine() function has a known bug, which I reported and provided a fix for.
    // Until it's officially fixed, here is a correct implementation.
    // See: https://github.com/arrow-kt/arrow/issues/3591 and https://github.com/arrow-kt/arrow/pull/3592
    private fun <K, V> Map<K, V>.combine(other: Map<K, V>, valueCombine: (V, V) -> V): Map<K, V> {
        return if (this.size < other.size) this.fold(other) { acc, (k, v) ->
            val value = other[k]?.let { valueCombine(v, it) } ?: v
            acc + (k to value)
        } else other.fold(this@combine) { acc, (k, v) ->
            val value = this[k]?.let { valueCombine(it, v) } ?: v
            acc + (k to value)
        }
    }

    /**
     * ## Assembling two monoid operations into a product monoid
     *
     * _Product_ monoids are useful for fusing traversals.
     *
     * The identity element of a product monoid is a [Pair] consisting of the identity elements of the two monoids.
     */
    fun <L, R> productCombine(leftCombine: (L, L) -> L, rightCombine: (R, R) -> R) =
        fun(pair1: Pair<L, R>, pair2: Pair<L, R>) =
            leftCombine(pair1.first, pair2.first) to rightCombine(pair1.second, pair2.second)

    /**
     * ## Single-pass mean calculation using a product monoid
     *
     * Computes the mean in a single pass by leveraging a product monoid
     * to track both the sum and count of elements simultaneously.
     */
    fun mean(list: List<Int>): Double {
        // The product monoid operation and identity
        val plusPlusProductCombine = productCombine(Int::plus, Int::plus)
        val identity = Pair(0, 0)

        val sumAndCount = list.fold(identity) { acc: Pair<Int, Int>, i: Int ->
            plusPlusProductCombine(acc, Pair(i, 1))
        }

        return sumAndCount.first.toDouble() / sumAndCount.second
    }

    /**
     * ## Balanced reduce
     *
     * When the combine cost is proportional on the size of its arguments (e.g. strings, lists),
     * left and right folds become inefficient. In such cases _balanced reduce_ or _eager reduce_ algorithms
     * are preferred. However, traditional left and right folds offer the flexibility to accumulate
     * with an initial value and into a different structure.
     *
     * Due to its divide and conquer approach, balanced reduce does not support an initial value
     * or accumulation into a different structure, like traditional left/right folds.
     *
     * Instead of accepting an identity element (nil) for the base case of an empty list,
     * this function returns `null`, allowing the caller to fall back as needed.
     */
    fun <T> balancedReduce(list: List<T>, combine: (T, T) -> T): T? {
        if (list.isEmpty()) return null
        if (list.size == 1) return list.first()

        // Split the list in the middle
        val middle = list.size / 2
        val leftHalve = list.subList(0, middle)
        val rightHalve = list.subList(middle, list.size)

        val leftResult = balancedReduce(leftHalve, combine)
        val rightResult = balancedReduce(rightHalve, combine)

        return if (leftResult != null && rightResult != null) combine(leftResult, rightResult) else leftResult
            ?: rightResult
    }

    /**
     * ## Eager reduce
     *
     * Since this algorithm combines pairs of [T], it's not suited for handling an initial value
     * or accumulating into a different structure like traditional left/right folds.
     *
     * Instead of accepting an identity element (nil) for the base case of an empty list,
     * this function returns `null` allowing the caller to fall back as needed.
     *
     * Notice the use of tail call optimization with `tailrec`! See [Recursion].
     */
    tailrec fun <T> eagerReduce(list: List<T>, combine: (T, T) -> T): T? {
        if (list.isEmpty()) return null
        if (list.size == 1) return list.first()
        val shorterList = mutableListOf<T>()
        for (i in list.indices step 2) {
            if (i == list.size - 1)
                shorterList.add(list[i])
            else
                shorterList.add(combine(list[i], list[i + 1]))
        }

        return eagerReduce(shorterList, combine)
    }

    /**
     * ## Calculating running totals using running reduce
     *
     * A common use case is computing a bank account balance from a list of transactions
     */
    fun runningTotal(list: List<Int>) =
        list.runningReduce(Int::plus)

    /**
     * ## Calculating a running average using running reduce
     */
    fun runningAverage(list: List<Double>) =
        list.runningReduce { acc, d -> (acc + d) / 2 }

    /**
     * ## Calculating a running delta using running fold (scan)
     *
     * Computes successive differences between elements.
     *
     * Note that for [Iterable]s like, [List] [zipWithNext] can be utilised.
     */
    fun runningDelta(flow: Flow<Int>): Flow<Int> =
        flow.runningFold(Pair<Int?, Int?>(null, null)) { acc, current ->
            // The previous value goes in Pair.first, the current value goes in Pair.second, i.e. (previous, current)
            Pair(acc.second, current)
        }
            .map { (previous, current) ->
                if (previous != null && current != null) {
                    // This is the combine operation
                    current - previous
                } else null
            }
            .filterNotNull()

}
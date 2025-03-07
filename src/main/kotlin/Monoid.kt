import Monoid.combine
import arrow.core.fold

/**
 * # Monoid
 *
 * Monoids can be composed and transformed in various ways:
 *
 * _Nesting_ monoids is useful for folding hierarchical structures
 * such as multimaps and nested maps.
 *
 * Since a monoid's associative operation is not necessarily commutative,
 * preserving the order of parameters when delegating to a nested operation is essential.
 *
 * _Product_ monoids are useful for fusing traversals.
 *
 * ## References
 *
 * [Monoid - Wikipedia](https://en.wikipedia.org/wiki/Monoid)
 *
 * [Commutative property - Wikipedia](https://en.wikipedia.org/wiki/Commutative_property)
 *
 * [Fold - Wikipedia](https://en.wikipedia.org/wiki/Fold_(higher-order_function))
 */
object Monoid {
    /**
     * ## Nesting two monoid operations to merge two multimaps
     *
     * [Map.combine] is the outer operation and [Collection.plus] is the nested one.
     */
    fun combineMergeMultimap(map1: Map<String, List<Int>>, map2: Map<String, List<Int>>): Map<String, List<Int>> =
        map1.combine(map2, Collection<Int>::plus)

    /**
     * ## Nesting three monoids operations to merge two nested maps
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
     * The identity of the product monoid is a [Pair] of the identities of the two monoids.
     */
    fun <L, R> productCombine(leftCombine: (L, L) -> L, rightCombine: (R, R) -> R) =
        fun(pair1: Pair<L, R>, pair2: Pair<L, R>) =
            leftCombine(pair1.first, pair2.first) to rightCombine(pair1.second, pair2.second)

    /**
     * ## Single-pass mean calculation
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
     * Due to the divide and conquer algorithm, balanced reduce is not suited to handling initial value
     * and accumulation into a different structure like a traditional left/right fold.
     *
     * Instead of passing the identity element (nil) to handle the base case of empty list,
     * the function returns `null` allowing the caller to fall back to nil or some initial value.
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
     * Since this algorithm combines pairs of [T], it's not suited to handling initial value
     * and accumulation into a different structure like a traditional left/right fold.
     *
     * Instead of passing the identity element (nil) to handle the base case of empty list,
     * the function returns `null` allowing the caller to fall back to nil or some initial value.
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

}
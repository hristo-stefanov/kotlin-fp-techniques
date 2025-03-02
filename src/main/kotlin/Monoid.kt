import Monoid.combine
import arrow.core.fold

/**
 * # Monoid
 *
 * _Nesting_ monoid associative operations is useful for folding hierarchical structures
 * such as multimaps and nested maps.
 *
 * Since a monoid's associative operation is not necessarily commutative,
 * preserving the order of parameters when delegating to a nested operation is essential.
 *
 * ## References
 *
 * [Monoid - Wikipedia](https://en.wikipedia.org/wiki/Monoid)
 *
 * [Commutative property - Wikipedia](https://en.wikipedia.org/wiki/Commutative_property)
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
}
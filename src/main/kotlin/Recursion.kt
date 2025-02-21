import arrow.core.MemoizedDeepRecursiveFunction

/**
 * # Recursion
 *
 * ## References
 *
 * [Recursive functions](https://arrow-kt.io/learn/collections-functions/recursive/)
 *
 * [Tail recursive functions](https://kotlinlang.org/docs/functions.html#tail-recursive-functions)
 *
 * [Tail call](https://en.wikipedia.org/wiki/Tail_call)
 */
object Recursion {
    /**
     * ## Optimizing with tail recursion
     *
     * Note the `tailrec` Kotlin keyword
     */
    tailrec fun triangularNumber(n: Long, acc: Long = 0L): Long {
        if (n == 0L) return acc
        return triangularNumber(n - 1, acc + n)
    }


    /**
     * ## Optimizing with memoization using Arrow
     *
     * Note that a tail recursive algorithm exists, but the classical algorithm is used here
     * as it significantly benefits from memoization. Replacing [MemoizedDeepRecursiveFunction] with
     * [DeepRecursiveFunction] allows demonstrating the effect of memoization.
     */
    val fibonacci = MemoizedDeepRecursiveFunction<Long, Long> { n ->
        when (n) {
            0L -> 0L
            1L -> 1L
            else -> callRecursive(n - 1L) + callRecursive(n - 2L)
        }
    }
}
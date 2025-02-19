import arrow.core.curried
import arrow.core.partially1
import java.lang.Math.addExact
import kotlin.math.acos

/**
 * # Partial application
 *
 * Fixing a number of arguments of a function, producing another function of smaller arity.
 *
 * Both the curried and uncurried forms of a functions can be applied partially.
 *
 * ## References
 *
 * [Partial application](https://en.wikipedia.org/wiki/Partial_application)
 * [Utilities for functions](https://arrow-kt.io/learn/collections-functions/utils/#partial-application)
 */
object PartialApplication {
    /**
     * ## Partial application of a unary function using function declaration
     */
    fun pi() = acos(-1.0)

    /**
     * ## Partial application of a unary function using a lambda expression
     *
     * Note that there is another way to construct a function literal in Kotlin: using an anonymous function.
     */
    val piLambda = { acos(-1.0) }

    /**
     * ## Partial application of a binary function using Arrow
     */
    val plusOneArrow = ::addExact.partially1(1)

    /**
     * ## Partial application of a curried function
     *
     * Note that invoking the given function returns a function value; therefore,
     * there is no need to declare a new function or use a function literal.
     */
    val plusOneCurried: (y: Int) -> Int = ::addExact.curried()(1)
}
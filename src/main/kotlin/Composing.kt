import arrow.core.andThen
import arrow.core.compose
import kotlin.math.exp
import kotlin.math.sin

/**
 * # Composing functions
 *
 * Composing functions allows for the introduction of generalisations.
 *
 * ## References
 *
 * [Function composition](https://en.wikipedia.org/wiki/Function_composition_(computer_science))
 *
 * [Tacit programming](https://en.wikipedia.org/wiki/Tacit_programming)
 *
 * [Concatenative programming language](https://en.wikipedia.org/wiki/Concatenative_programming_language)
 *
 * [Concatenative language/Name code not values](https://concatenative.org/wiki/view/Concatenative%20language/Name%20code%20not%20values)
 */
object Composing {
    /**
     * ## Composing regular functions using explicit function application.
     */
    fun sinThenExp(x: Double) = exp(sin(x))

    /**
     * ## Composing regular functions using explicit function application and a lambda expression
     */
    val sinThenExpLambda = { x: Double -> exp(sin(x)) }

    /**
     * ## Composing regular functions using point-free style
     *
     * Point-free style is also referred to as _tacit programming_.
     *
     * Note that composition is applied in right-to-left order, as in mathematics and Haskell.
     */
    val sinThenExpPfs: (Double) -> Double = ::exp compose ::sin

    /**
     * ## Composing regular functions using point-free style and chaining
     *
     * Note that composition is applied in left-to-right order, similar to syntactic chains.
     */
    val sinThenExpPfsChain = ::sin andThen ::exp

}
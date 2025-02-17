import arrow.core.andThen
import arrow.core.compose
import arrow.core.curried
import java.lang.Math.pow
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
 *
 * [Ordering of parameters to make use of currying](https://stackoverflow.com/questions/5863128/ordering-of-parameters-to-make-use-of-currying)
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
    val sinThenExpPfs = ::exp compose ::sin

    /**
     * ## Composing regular functions using point-free style and chaining
     *
     * Note that composition is applied in left-to-right order, similar to syntactic chains.
     */
    val sinThenExpPfsChain = ::sin andThen ::exp

    /**
     * ## Curry to compose using point-free style
     */
    val powTwicePfs = ::applyTwice.curried() compose ::pow.curried()

    /**
     * ## Applies a policy twice
     *
     * Note the order of parameters - it follows the data-last style to allow lining-up when used with curring.
     */
    private fun applyTwice(policy: (Double) -> Double, x: Double): Double = policy(policy(x))
}
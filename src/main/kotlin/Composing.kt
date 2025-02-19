import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.andThen
import arrow.core.compose
import arrow.core.curried
import java.lang.Math.*

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
     * ## Using currying to compose non-unary functions in point-free style
     */
    val powTwicePfs = ::applyTwice.curried() compose ::pow.curried()

    /**
     * ## Applies a policy twice
     *
     * Note the order of parameters - it follows the data-last style to allow lining-up when used with curring.
     */
    private fun applyTwice(policy: (Double) -> Double, x: Double): Double = policy(policy(x))

    /**
     * ## Debugging composition with tracing
     */
    val sinThenExpTraced = trace<Double>("exp result") compose ::exp compose trace("sin result") compose ::sin

    private fun <T> trace(tag: String) = fun(value: T): T {
        println("[$tag] $value")
        return value
    }

    /**
     * ## Large scale composition of regular functions
     *
     * @throws ClassCastException if the consumed and produced types of [functions] don't line up
     */
    @Suppress("UNCHECKED_CAST")
    fun chain(vararg functions: Function1<*, *>) = { argOfFirstFunction: Any? ->
        functions.fold(argOfFirstFunction) { acc, f -> (f as (Any?) -> Any?)(acc) }
    }

    /**
     * ## Composing monadic unary functions
     */
    fun sqrtThenLog10Num(x: Double): Option<Double> = sqrtNum(x).flatMap(::log10Num)

    private fun sqrtNum(x: Double): Option<Double> = sqrt(x).let { if (it.isNaN()) None else Some(it) }
    private fun log10Num(x: Double): Option<Double> = log10(x).let { if (it.isNaN()) None else Some(it) }

    /**
     * ## Composing monadic binary functions
     */
    fun addThenMultiplySafely(x: Int, y: Int, z: Int) = addSafely(x, y).flatMap { multiplySafely(it, z) }

    private fun addSafely(x: Int, y: Int): Option<Int> = Option.catch { addExact(x, y) }
    private fun multiplySafely(x: Int, y: Int): Option<Int> = Option.catch { multiplyExact(x, y) }
}

fun main() {
    Composing.sinThenExpTraced(PI / 2)
}

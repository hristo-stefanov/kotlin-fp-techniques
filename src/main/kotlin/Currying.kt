import arrow.core.curried
import java.lang.Math.fma
import kotlin.math.log

/**
 * # Currying
 */
object Currying {
    /**
     * ## Currying a function with arity 2
     */
    fun curriedLog(x: Double) = fun(base: Double) = log(x = x, base = base)

    /**
     * ## Currying a function with arity 2 using a lambda expression
     */
    val curriedLogLambda = { x: Double -> { base: Double -> log(x = x, base = base) } }

    /**
     * ## Currying a function with arity 3
     */
    fun curriedFma(a: Double) = fun(b: Double) = fun(c: Double) = fma(a, b, c)

    /**
     * ## Currying a function with arity 3 using a lambda expression
     */
    val curriedFmaLambda = { a: Double -> { b: Double -> { c: Double -> fma(a, b, c) } } }

    /**
     * ## Auto-currying a function with Arrow
     * Arrow can handle arities of up to 22.
     */
    val autoCurriedFma = ::fma.curried()
}
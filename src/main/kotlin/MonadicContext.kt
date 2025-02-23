import arrow.core.Either
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.raise.option
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip
import java.lang.Math.exp
import java.lang.Math.fma
import java.lang.Math.pow

/**
 * # Monadic context
 *
 * _Wrapping_ is a technique for embedding a plain value into a monadic (computation) context.
 * It's also called "lifting a value".
 * A monad defines an operation `unit` (also known as `return` or `pure`) which, depending on the monad
 * implementation can have different names.
 *
 * A monad does not provide a generic "unwrap" operation, as it is designed for composing computations.
 * However, certain monad implementations offer functions for controlled _extraction_ of embedded values.
 *
 * _Lifting_ is a technique for transforming a regular function to operate within a monadic context.
 *
 * _Composing_ monadic functions is done by using the "bind" operator, also known as `flatmap` or the Kleisli operator.
 * The technique is demonstrated by [Composing.addThenMultiplySafely]
 *
 * ## References
 *
 * [Monad - Wikipedia](https://en.wikipedia.org/wiki/Monad_(functional_programming))
 *
 * [Syntactic sugar do-notation - Wikipedia](https://en.wikipedia.org/wiki/Monad_(functional_programming)#do-notation)
 *
 * [Working with typed errors](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/)
 */
object MonadicContext {

    /**
     * ## Wrapping with an extension function
     */
    val right: Either<Throwable, String> = "right".right()

    /**
     *  ## Wrapping with a class constructor
     */
    val some: Option<String> = Some("some")

    /**
     * ## Extracting a value from an Option monad
     */
    val someValue: String = some.getOrElse { "error" }

    /**
     * ## Extracting a value from an Either monad
     */
    val rightValue = right.fold({ it.message }, { it })

    /**
     * ## Lifting a unary function with `map`
     */
    fun expOption(xOption: Option<Double>) = xOption.map(::exp)

    /**
     * ## Lifting a binary function with `map` and `flatMap`
     */
    fun powOption(aOption: Option<Double>, bOption: Option<Double>): Option<Double> {
        return aOption.flatMap { a ->
            bOption.map { b ->
                pow(a, b)
            }
        }
    }

    /**
     * ## Lifting a ternary function with `map` and `flatMap`
     */
    fun fmaOption(aOption: Option<Double>, bOption: Option<Double>, cOption: Option<Double>): Option<Double> {
        return aOption.flatMap { a ->
            bOption.flatMap { b ->
                cOption.map { c ->
                    fma(a, b, c)
                }
            }
        }
    }

    /**
     * ## Lifting a binary function with `zip`
     *
     * Note that zip implementations can have limited arity as low as 2.
     */
    fun powFlow(aFlow: Flow<Double>, bFlow: Flow<Double>): Flow<Double> {
        return aFlow.zip(bFlow) { a, b -> pow(a, b) }
    }

    /**
     * ## Lifting a ternary function with syntactic sugar
     *
     * Arrow's Raise DSL does not suffer from `zip`'s arity limitation and provides builders for error-handling monads
     * such as `Either`, `Ior` and `Option`.
     */
    fun fmaOptionArrow(aOption: Option<Double>, bOption: Option<Double>, cOption: Option<Double>): Option<Double> =
        option {
            fma(aOption.bind(), bOption.bind(), cOption.bind())
        }

}
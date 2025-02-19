import Composing.addThenMultiplySafely
import Composing.chain
import Composing.powTwicePfs
import Composing.sinThenExp
import Composing.sinThenExpLambda
import Composing.sinThenExpPfs
import Composing.sinThenExpPfsChain
import Composing.sqrtThenLog10Num
import arrow.core.Some
import org.assertj.core.api.Assertions.assertThat
import java.lang.Math.E
import java.lang.Math.PI
import java.lang.Math.exp
import java.lang.Math.sin
import java.util.*
import kotlin.test.Test

class ComposingTest {
    @Test
    fun testSinThenExp() {
        val result = sinThenExp(PI / 2)

        assertThat(result).isEqualTo(E)
    }

    @Test
    fun testSinThenExpLambda() {
        val result = sinThenExpLambda(PI / 2)

        assertThat(result).isEqualTo(E)
    }

    @Test
    fun textSinThenExpPfs() {
        val result = sinThenExpPfs(PI / 2)

        assertThat(result).isEqualTo(E)
    }

    @Test
    fun testSinThenExpPfsChain() {
        val result = sinThenExpPfsChain(PI / 2)

        assertThat(result).isEqualTo(E)
    }

    @Test
    fun testPowTwicePfs() {
        val result = powTwicePfs(2.0)(3.0)

        // The actual policy applied twice is f(x) = 2.0^x
        // Thus, result = 2^(2^3) = 2^8 = 256
        assertThat(result).isEqualTo(256.0)
    }

    @Test
    fun testChain() {
        fun toShortSciFormat(x: Double): String = "%.4e".format(Locale.ENGLISH, x)

        val result = chain(::sin, ::exp, ::toShortSciFormat)(PI / 2)

        assertThat(result).isEqualTo("2.7183e+00")
    }

    @Test
    fun testSqrtThenLog10Num() {
        val result = sqrtThenLog10Num(100.0)

        assertThat(result).isEqualTo(Some(1.0))
    }

    @Test
    fun testAddThenMultiplySafely() {
        val result = addThenMultiplySafely(1, 2, 3)

        assertThat(result).isEqualTo(Some(9))
    }
}
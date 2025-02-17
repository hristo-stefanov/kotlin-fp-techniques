import Composing.powTwicePfs
import Composing.sinThenExp
import Composing.sinThenExpLambda
import Composing.sinThenExpPfs
import Composing.sinThenExpPfsChain
import org.assertj.core.api.Assertions.assertThat
import kotlin.math.E
import kotlin.math.PI
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
}
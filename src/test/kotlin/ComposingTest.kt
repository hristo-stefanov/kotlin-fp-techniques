import Composing.sinThenExp
import Composing.sinThenExpLambda
import Composing.sinThenExpPfs
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
}
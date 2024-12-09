import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class CurryingTest {
    @Test
    fun testCurriedLog() {
        val result = Currying.curriedLog(9.0)(3.0)

        assertThat(result).isEqualTo(2.0)
    }

    @Test
    fun testCurriedLogLambda() {
        val result = Currying.curriedLogLambda(9.0)(3.0)

        assertThat(result).isEqualTo(2.0)
    }

    @Test
    fun testCurriedFma() {
        // = a * b + c
        val result = Currying.curriedFma(2.0)(3.0)(1.0)

        assertThat(result).isEqualTo(7.0)
    }

    @Test
    fun testCurriedFmaLambda() {
        // = a * b + c
        val result = Currying.curriedFmaLambda(2.0)(3.0)(1.0)

        assertThat(result).isEqualTo(7.0)
    }

    @Test
    fun testAutoCurriedFma() {
        // = a * b + c
        val result = Currying.autoCurriedFma(2.0)(3.0)(1.0)

        assertThat(result).isEqualTo(7.0)
    }
}

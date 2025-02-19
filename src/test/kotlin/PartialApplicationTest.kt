import PartialApplication.pi
import PartialApplication.piLambda
import PartialApplication.plusOneArrow
import PartialApplication.plusOneCurried
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.PI

class PartialApplicationTest {
    @Test
    fun testPi() {
        val result = pi()

        assertThat(result).isEqualTo(PI)
    }

    @Test
    fun testPiLambda() {
        val result = piLambda()

        assertThat(result).isEqualTo(PI)
    }


    @Test
    fun testPlusOneArrow() {
        val result = plusOneArrow(2)

        assertThat(result).isEqualTo(3)
    }

    @Test
    fun testPlusOneCurried() {
        val result = plusOneCurried(2)

        assertThat(result).isEqualTo(3)
    }
}
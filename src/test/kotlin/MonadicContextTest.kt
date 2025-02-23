
import MonadicContext.expOption
import MonadicContext.fmaOption
import MonadicContext.fmaOptionArrow
import MonadicContext.powFlow
import MonadicContext.powOption
import MonadicContext.right
import MonadicContext.rightValue
import MonadicContext.some
import MonadicContext.someValue
import arrow.core.Some
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.Math.E

class MonadicContextTest {
    @Test
    fun testRight() {
        assertThat(right.isRight()).isTrue()
    }

    @Test
    fun testSome() {
        assertThat(some.isSome()).isTrue()
    }

    @Test
    fun testSomeValue() {
        assertThat(someValue).isEqualTo("some")
    }

    @Test
    fun testRightVale() {
        assertThat(rightValue).isEqualTo("right")
    }

    @Test
    fun testExpOption() {
        val result = expOption(Some(1.0))

        assertThat(result).isEqualTo(Some(E))
    }

    @Test
    fun testPowOption() {
        val result = powOption(Some(2.0), Some(3.0))

        assertThat(result).isEqualTo(Some(8.0))
    }

    @Test
    fun testFmaOption() {
        val result = fmaOption(Some(2.0), Some(3.0), Some(1.0))

        assertThat(result).isEqualTo(Some(7.0))
    }

    @Test
    fun testFmaOptionArrow() {
        val result = fmaOptionArrow(Some(2.0), Some(3.0), Some(1.0))

        assertThat(result).isEqualTo(Some(7.0))
    }

    @Test
    fun testPowFlow() = runTest {
        val result = powFlow(flowOf(2.0), flowOf(3.0))

        assertThat(result.single()).isEqualTo(8.0)
    }
}
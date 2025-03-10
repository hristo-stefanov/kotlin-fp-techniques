import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecursionTest {
    @Test
    fun testTriangularNumber() {
        val result = Recursion.triangularNumber(10_000_000)

        assertThat(result).isEqualTo(50_000_005_000_000)
    }

    @Test
    fun testFibonacci() {
        val result = Recursion.fibonacci.invoke(70)

        assertThat(result).isEqualTo(190_392_490_709_135)
    }
}
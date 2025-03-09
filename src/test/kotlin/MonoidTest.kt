import Monoid.Action
import Monoid.balancedReduce
import Monoid.combineMergeMultimap
import Monoid.combineMergeNestedMaps
import Monoid.eagerReduce
import Monoid.mean
import Monoid.productCombine
import Monoid.runningAverage
import Monoid.runningDelta
import Monoid.runningTotal
import Monoid.runningTransformState
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MonoidTest {
    @Test
    fun testCombineMergeMultimap() {
        val maps = listOf(
            mapOf("ones" to listOf(1, 2, 3)),
            mapOf("tens" to listOf(10, 20, 30)),
            mapOf("ones" to listOf(4, 5, 6)),
            mapOf("tens" to listOf(40, 50, 60))
        )

        val result = maps.fold(emptyMap(), ::combineMergeMultimap)

        assertThat(result).isEqualTo(
            mapOf(
                "ones" to listOf(1, 2, 3, 4, 5, 6),
                "tens" to listOf(10, 20, 30, 40, 50, 60)
            )
        )
    }

    @Test
    fun testCombineMergeNestedMaps() {
        val maps = listOf(
            mapOf("category" to mapOf(1 to "A", 2 to "B")),
            mapOf("category" to mapOf(1 to "lpha", 2 to "eta"))
        )

        val result = maps.fold(emptyMap(), ::combineMergeNestedMaps)

        assertThat(result).isEqualTo(
            mapOf("category" to mapOf(1 to "Alpha", 2 to "Beta"))
        )
    }

    @Test
    fun testProductCombine() {
        val plusMinusProductCombine: (Pair<String, Int>, Pair<String, Int>) -> Pair<String, Int> =
            productCombine(String::plus, Int::minus)

        val result = plusMinusProductCombine(Pair("Left", 4), Pair("Right", 1))

        assertThat(result).isEqualTo(Pair("LeftRight", 3))

    }

    @Test
    fun testMean() {
        val list = listOf(1, 2, 3, 4, 5)

        val result = mean(list)

        assertThat(result).isEqualTo(3.0)
    }

    @Test
    fun testBalancedReduce() {
        val list = listOf("abc", "def", "ghi", "jkl", "mno")

        val result = balancedReduce(list, String::plus)

        assertThat(result).isEqualTo("abcdefghijklmno")
    }

    @Test
    fun testEagerReduce() {
        val list = listOf("abc", "def", "ghi", "jkl", "mno")

        val result = eagerReduce(list, String::plus)

        assertThat(result).isEqualTo("abcdefghijklmno")
    }

    @Test
    fun testRunningTotal() {
        val list = listOf(1, 2, 3, 4, 5)

        val result = runningTotal(list)

        assertThat(result).isEqualTo(listOf(1, 3, 6, 10, 15))
    }

    @Test
    fun testRunningAverage() {
        val list = listOf(0.0, 1.0, 2.0, 2.0, 2.0)

        val result = runningAverage(list)

        assertThat(result).isEqualTo(listOf(0.0, 0.5, 1.25, 1.625, 1.8125))
    }

    @Test
    fun testRunningDelta() = runTest {
        val flow = flowOf(1, 1, 2, 1, 3)

        val result = runningDelta(flow).toList()

        assertThat(result).isEqualTo(listOf(0, 1, -1, 2))
    }

    @Test
    fun testRunningTransformState() = runTest {
        val actionFlow = flowOf(
            Action.Add(1),
            Action.Add(20),
            Action.Subtract(5)
        )

        val result = runningTransformState(actionFlow).toList()

        assertThat(result).isEqualTo(listOf(0, 1, 21, 16))
    }
}
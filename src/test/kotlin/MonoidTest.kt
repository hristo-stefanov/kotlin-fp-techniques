import Monoid.balancedReduce
import Monoid.combineMergeMultimap
import Monoid.combineMergeNestedMaps
import Monoid.eagerReduce
import Monoid.mean
import Monoid.productCombine
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
}
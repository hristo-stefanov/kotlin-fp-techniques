import Monoid.combineMergeMultimap
import Monoid.combineMergeNestedMaps
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
}
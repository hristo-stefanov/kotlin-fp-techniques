import Parallelism.parallelBalancedReduce
import Parallelism.parallelFlowMergeMap
import Parallelism.parallelListMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class ParallelismTest {
    @Test
    fun testParallelBalancedReduce() = runTest {
        val list = listOf("abc", "def", "ghi", "jkl", "mno")

        val result = withContext(Dispatchers.Default) {
            parallelBalancedReduce(list, String::plus)
        }

        assertThat(result).isEqualTo("abcdefghijklmno")
    }

    @Test
    fun testParallelListMap() = runTest {
        val list = listOf(1, 2, 3)

        val result = withContext(Dispatchers.Default) {
            parallelListMap(list = list, maxCoroutineNumber = 1000) { value ->
                value * 2
            }
        }

        assertThat(result).isEqualTo(listOf(2, 4, 6))
    }

    @Test
    fun testParallelFlowMergeMap() = runTest {
        val flow: Flow<Int> = flowOf(1, 2, 3, 4)

        val resultFlow = parallelFlowMergeMap(flow, 1000) { it * 2 }

        val result = withContext(Dispatchers.Default) {
            resultFlow.toList()
        }

        // We sort the result because it's shuffled
        assertThat(result.sorted()).isEqualTo(listOf(2, 4, 6, 8))
    }
}
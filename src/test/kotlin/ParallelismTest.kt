
import Parallelism.parallelBalancedReduce
import Parallelism.parallelFlowMergeFilter
import Parallelism.parallelFlowMergeMap
import Parallelism.parallelListFilter
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
            parallelListMap(list = list, maxCoroutineNumber = 1000) { it * 2 }
        }

        assertThat(result).isEqualTo(listOf(2, 4, 6))
    }

    @Test
    fun testParallelListFilter() = runTest {
        val list = listOf(1, 2, 3)

        val result = withContext(Dispatchers.Default) {
            parallelListFilter(list = list, maxCoroutineNumber = 1000) { it % 2 != 0 }
        }

        assertThat(result).isEqualTo(listOf(1, 3))
    }

    @Test
    fun testParallelFlowMergeMap() = runTest {
        val flow: Flow<Int> = flowOf(1, 2, 3, 4)

        val resultFlow = parallelFlowMergeMap(flow, maxCoroutineNumber = 1000) { it * 2 }

        val result = withContext(Dispatchers.Default) { resultFlow.toList() }

        // We sort the result because it's shuffled
        assertThat(result.sorted()).isEqualTo(listOf(2, 4, 6, 8))
    }

    @Test
    fun testParallelFlowMergeFilter() = runTest {
        val flow = flowOf(1, 2, 3)

        val resultFlow = parallelFlowMergeFilter(flow = flow, maxCoroutineNumber = 1000) { it % 2 != 0 }

        val result = withContext(Dispatchers.Default) { resultFlow.toList() }

        // We sort the result because it's shuffled
        assertThat(result.sorted()).isEqualTo(listOf(1, 3))
    }
}
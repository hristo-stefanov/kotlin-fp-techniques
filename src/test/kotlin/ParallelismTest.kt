import Parallelism.parallelBalancedReduce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class ParallelismTest {
    @Test
    fun parallelBalancedReduce() = runTest {
        val list = listOf("abc", "def", "ghi", "jkl", "mno")

        val result = withContext(Dispatchers.Default) {
            parallelBalancedReduce(list, String::plus)
        }

        assertThat(result).isEqualTo("abcdefghijklmno")
    }
}
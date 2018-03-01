package regex.unitTest

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import regex.Concated
import regex.NonTerminalCharacter
import regex.TerminalCharacter
import kotlin.test.assertTrue

internal class ConcatedTest {
    @Test
    fun isRegular() {
        val a = TerminalCharacter("a")
        val B = NonTerminalCharacter("B")
        assertTrue(((a concat B) as Concated).isRegular)
        assertFalse(((a concat a concat B) as Concated).isRegular)
        assertFalse(((B concat a) as Concated).isRegular)
        assertFalse(((B concat B concat a) as Concated).isRegular)
        assertFalse(((a concat a concat a) as Concated).isRegular)
    }
}
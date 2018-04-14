package regex.unitTest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import regex.*
import kotlin.test.assertFailsWith

internal class RegexComponentFactoryTest {
    @Test
    fun fromString() {
        assertTrue(RegexComponentFactory.fromString("a") is TerminalCharacter)
        assertTrue(RegexComponentFactory.fromString("ab") is Concated)
        assertTrue(RegexComponentFactory.fromString("a|b") is Optioned)
        assertTrue(RegexComponentFactory.fromString("a*") is Repeated)
        assertTrue(RegexComponentFactory.fromString("a*|b") is Optioned)
        assertTrue(RegexComponentFactory.fromString("a*|bc") is Optioned)
        assertTrue(RegexComponentFactory.fromString("a*bc") is Concated)
        assertTrue(RegexComponentFactory.fromString("(abc)*") is Repeated)
        assertTrue(RegexComponentFactory.fromString("") === NullCharacter)
        assertFailsWith(IllegalArgumentException::class, { RegexComponentFactory.fromString("(ab") })
    }

    @Test
    fun concated() {
        val a = TerminalCharacter("a")
        val b = TerminalCharacter("b")
        assertEquals(a concat b, RegexComponentFactory.fromString("ab"))
        assertEquals(a concat b concat (b concat a), RegexComponentFactory.fromString("abba"))
        assertEquals(a concat (b concat a), RegexComponentFactory.fromString("aba"))
        assertEquals((a or b) concat (a or b), RegexComponentFactory.fromString("aa|ab|ba|bb"))
        assertEquals((a or b) concat a, RegexComponentFactory.fromString("aa|ba"))
        assertEquals(a concat (a or b), RegexComponentFactory.fromString("aa|ab"))
    }

    @Test
    fun optioned() {
        val a = TerminalCharacter("a")
        val b = TerminalCharacter("b")
        assertEquals(a or b, RegexComponentFactory.fromString("a|b"))
        assertEquals(a, a or a)
        assertEquals(a or b, a or b or a)
        assertEquals(a or b, a or (b or a))
        assertEquals(a or b, (a or b) or (a or b))
    }

    @Test
    fun repeated() {
        val a = TerminalCharacter("a")
        assertEquals(a.repeat(), RegexComponentFactory.fromString("a*"))
        assertEquals(a.repeat().repeat(), a.repeat())
    }
}
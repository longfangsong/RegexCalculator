package regex.unitTest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import regex.NullCharacter
import regex.TerminalCharacter

internal class NullCharacterTest {

    @Test
    fun concat() {
        val a = TerminalCharacter("a")
        assertEquals(a, a concat NullCharacter)
        assertEquals(a, NullCharacter concat a)
        assertEquals(NullCharacter, NullCharacter concat NullCharacter)
    }

    @Test
    fun or() {
        val a = TerminalCharacter("a")
        assertNotEquals(a, a or NullCharacter)
        assertNotEquals(a, NullCharacter or a)
        assertEquals(NullCharacter, NullCharacter or NullCharacter)
        assertEquals(NullCharacter or a or NullCharacter, NullCharacter or a)
    }

    @Test
    fun repeat() {
        assertEquals(NullCharacter, NullCharacter.repeat())
    }
}
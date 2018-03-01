package regex.unitTest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import regex.TerminalCharacter
import regex.nullCharacter

internal class NullCharacterTest {

    @Test
    fun concat() {
        val a = TerminalCharacter("a")
        assertEquals(a, a concat nullCharacter)
        assertEquals(a, nullCharacter concat a)
        assertEquals(nullCharacter, nullCharacter concat nullCharacter)
    }

    @Test
    fun or() {
        val a = TerminalCharacter("a")
        assertNotEquals(a, a or nullCharacter)
        assertNotEquals(a, nullCharacter or a)
        assertEquals(nullCharacter, nullCharacter or nullCharacter)
        assertEquals(nullCharacter or a or nullCharacter, nullCharacter or a)
    }

    @Test
    fun repeat() {
        assertEquals(nullCharacter, nullCharacter.repeat())
    }
}
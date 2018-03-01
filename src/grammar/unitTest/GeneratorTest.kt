package grammar.unitTest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import regex.NonTerminalCharacter
import regex.Regex
import regex.TerminalCharacter
import regex.nullCharacter

internal class GeneratorTest {

    @Test
    fun getRegulizedWithDirectDelegate() {
        NonTerminalCharacter.reset()
        var excepted = setOf(
                NonTerminalCharacter("A") to TerminalCharacter("a"),
                NonTerminalCharacter("A") to TerminalCharacter("b")
        )
        assertEquals(excepted, (NonTerminalCharacter.next() to Regex("a|b")).regulizedWithDirectDelegate)
        NonTerminalCharacter.reset()
        excepted = setOf(
                NonTerminalCharacter("A") to (TerminalCharacter("a") concat NonTerminalCharacter("B")),
                NonTerminalCharacter("B") to TerminalCharacter("b"))
        assertEquals(excepted, (NonTerminalCharacter.next() to Regex("ab")).regulizedWithDirectDelegate)
        NonTerminalCharacter.reset()
        excepted = setOf(
                NonTerminalCharacter("A") to (TerminalCharacter("a") concat NonTerminalCharacter("A")),
                NonTerminalCharacter("A") to nullCharacter)
        assertEquals(excepted, (NonTerminalCharacter.next() to Regex("a*")).regulizedWithDirectDelegate)
        NonTerminalCharacter.reset()
        excepted = setOf(
                NonTerminalCharacter("A") to (TerminalCharacter("a") concat NonTerminalCharacter("B")),
                NonTerminalCharacter("B") to (TerminalCharacter("b") concat NonTerminalCharacter("C")),
                NonTerminalCharacter("C") to (TerminalCharacter("c")))
        assertEquals(excepted, (NonTerminalCharacter.next() to Regex("abc")).regulizedWithDirectDelegate)
        NonTerminalCharacter.reset()
        val a = TerminalCharacter("a")
        val b = TerminalCharacter("b")
        val A = NonTerminalCharacter("A")
        val B = NonTerminalCharacter("B")
        val C = NonTerminalCharacter("C")
        NonTerminalCharacter.reset('C')
        excepted = setOf(
                A to (a concat C),
                C to (b concat B))
        assertEquals(excepted, (A to (a concat b concat B)).regulizedWithDirectDelegate)
        NonTerminalCharacter.reset()
        excepted = setOf(
                A to (a concat B),
                A to (b concat B))
        assertEquals(excepted, (A to ((a or b) concat B)).regulizedWithDirectDelegate)
        NonTerminalCharacter.reset()
        excepted = setOf(A to (a concat A), A to B)
        assertEquals(excepted, (NonTerminalCharacter.next() to (a.repeat() concat B)).regulizedWithDirectDelegate)
    }
}
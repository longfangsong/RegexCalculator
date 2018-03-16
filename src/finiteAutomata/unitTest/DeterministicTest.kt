package finiteAutomata.unitTest

import finiteAutomata.Deterministic
import grammar.Grammar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import regex.Regex
import regex.TerminalCharacter

internal class DeterministicTest {
    @Test
    fun getGraph() {
        val a = TerminalCharacter("a")
        val b = TerminalCharacter("b")
        val stateA = Deterministic.State("A")
        val stateB = Deterministic.State("B")
        stateA.transitions[a] = stateB
        stateA.transitions[b] = stateA
        stateB.transitions[a] = stateB
        stateB.transitions[b] = stateA
        val theDFA = Deterministic(setOf(stateA, stateB), stateA)
        assertEquals("digraph G {\n" +
                "    node[shape=circle];\n" +
                "    start[shape=none];\n" +
                "    start->A;\n" +
                "A->B [label=a];\n" +
                "A->A [label=b];\n" +
                "B->B [label=a];\n" +
                "B->A [label=b];}", theDFA.graph)
    }

    @Test
    fun match() {
        val theDFA = Grammar(Regex("1(1010*|1(010)*1)*0"))
                .toNFA()
                .toDFA()
                .minimized
        assertTrue(theDFA.match("10"))
        assertTrue(theDFA.match("110100"))
        assertTrue(theDFA.match("11010000000"))
        assertTrue(theDFA.match("11010"))
        assertTrue(theDFA.match("1101001010"))
        assertFalse(theDFA.match("1"))
        assertFalse(theDFA.match("0"))
        assertFalse(theDFA.match("10100"))
    }
}
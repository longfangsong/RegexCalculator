package finiteAutomata.unitTest

import finiteAutomata.Deterministic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
}
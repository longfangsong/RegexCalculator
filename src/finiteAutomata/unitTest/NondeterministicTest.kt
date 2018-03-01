package finiteAutomata.unitTest

import finiteAutomata.Nondeterministic
import org.junit.jupiter.api.Test
import regex.TerminalCharacter
import regex.nullCharacter
import kotlin.test.assertEquals

internal class NondeterministicTest {
    @Test
    fun getGraph() {
        val a = TerminalCharacter("a")
        val b = TerminalCharacter("b")
        val stateA = Nondeterministic.State("A")
        val stateB = Nondeterministic.State("B")
        stateA.transitions[a] = mutableSetOf(stateB, stateA)
        stateA.transitions[b] = mutableSetOf(stateA)
        stateA.transitions[nullCharacter] = mutableSetOf(stateA, stateB)
        stateB.transitions[a] = mutableSetOf(stateB)
        stateB.transitions[b] = mutableSetOf(stateA, stateB)
        val theNFA = Nondeterministic(setOf(stateA, stateB), stateA)
        assertEquals("digraph G {\n" +
                "    node[shape=circle];\n" +
                "    start[shape=none];\n" +
                "    start->A;\n" +
                "A->B [label=a];\n" +
                "A->A [label=a];\n" +
                "A->A [label=b];\n" +
                "A->A [label=ε];\n" +
                "A->B [label=ε];\n" +
                "B->B [label=a];\n" +
                "B->A [label=b];\n" +
                "B->B [label=b];}", theNFA.graph)
    }

    @Test
    fun stateEquivalentStates() {
        val a = TerminalCharacter("a")
        val b = TerminalCharacter("b")
        val stateA = Nondeterministic.State("A")
        val stateB = Nondeterministic.State("B")
        val stateC = Nondeterministic.State("B")
        val stateD = Nondeterministic.State("B")
        val stateE = Nondeterministic.State("B")
        stateA.transitions[nullCharacter] = mutableSetOf(stateB)
        stateA.transitions[a] = mutableSetOf(stateE)
        stateB.transitions[nullCharacter] = mutableSetOf(stateC)
        stateC.transitions[nullCharacter] = mutableSetOf(stateA, stateD)
        stateE.transitions[a] = mutableSetOf(stateD)
        stateE.transitions[b] = mutableSetOf(stateC)
        assertEquals(setOf(stateA, stateB, stateC, stateD), stateA.equivalentStates())
    }
}
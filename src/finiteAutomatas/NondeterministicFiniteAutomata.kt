package finiteAutomatas

import regexParts.TerminalChar
import java.util.*

class NondeterministicFiniteAutomata(
        val states: Set<NondeterministicFiniteAutomata.State>,
        val start: NondeterministicFiniteAutomata.State
) : HasGraph {
    class State(
            val name: String,
            val transitions: MutableMap<TerminalChar?, MutableSet<NondeterministicFiniteAutomata.State>>,
            var acceptable: Boolean
    ) {
        constructor(name: String, acceptable: Boolean = false) : this(name, mutableMapOf(), acceptable)

        operator fun get(terminalChar: TerminalChar?): Set<NondeterministicFiniteAutomata.State>? {
            return transitions[terminalChar]
        }

        fun addTransition(terminalChar: TerminalChar?, other: NondeterministicFiniteAutomata.State) {
            if (transitions[terminalChar] == null) {
                transitions[terminalChar] = mutableSetOf(other)
            } else {
                transitions[terminalChar]!!.add(other)
            }
        }

        fun addTransition(terminalChar: TerminalChar?, others: Collection<NondeterministicFiniteAutomata.State>) {
            if (transitions[terminalChar] == null) {
                transitions[terminalChar] = others.toMutableSet()
            } else {
                transitions[terminalChar]!!.addAll(others)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (name != other.name) return false
            if (transitions != other.transitions) return false
            if (acceptable != other.acceptable) return false

            return true
        }

        override fun hashCode(): Int {
            return name.hashCode()
        }

        override fun toString(): String {
            return "State(name='$name')"
        }

        val nullClosure: Set<NondeterministicFiniteAutomata.State> = (transitions[null]?.toSet()
                ?: setOf()) + setOf(this)

    }

    val alphabet = states.map { it.transitions.map { it.key }.toSet() }.reduce { it1, it2 -> it1 + it2 }.toSet()

    val Set<NondeterministicFiniteAutomata.State>.nullClosure: Set<NondeterministicFiniteAutomata.State>
        get() {
            val s = Stack<NondeterministicFiniteAutomata.State>()
            val result = this.toMutableSet()
            while (!s.empty()) {
                val t = s.pop()
                for (connectedState in t.nullClosure) {
                    if (connectedState !in result) {
                        result += connectedState
                        s.push(connectedState)
                    }
                }
            }
            return result
        }

    fun Set<NondeterministicFiniteAutomata.State>.directConnectedTo(withChar: TerminalChar?): Set<NondeterministicFiniteAutomata.State> {
        val temp = this.map {
            it.transitions[withChar]?.toSet() ?: setOf()
        }
        return if (temp.isEmpty()) setOf() else temp.reduce { it1, it2 -> it1 + it2 }.toSet()
    }

    fun Set<NondeterministicFiniteAutomata.State>.equivalenteStates(): Set<NondeterministicFiniteAutomata.State> {
        return this.map { it.nullClosure }.reduce { it1, it2 -> it1 + it2 }.toSet()
    }

    override val graph: String
        get() {
            return """
digraph G {
    node[shape=circle];
    start[shape=none];
    ${states.filter { it.acceptable }.joinToString(";\n") { "${it.name}[shape=doublecircle]" }}
    start->${start.name};
    ${
            states.joinToString(";\n    ") { state ->
                state.transitions.map { transition ->
                    transition.value.joinToString(";\n    ") {
                        "${state.name}->${it.name} " +
                                "[label=${transition.key ?: 'ε'}]"
                    }
                }.joinToString(";\n    ")
            }
            }
}"""
        }

    val DFA: DeterministicFiniteAutomata
        get() {
            val statesToFind = mutableSetOf(start.nullClosure)
            val alphabetBuffer = alphabet
            val result = mutableMapOf<Set<NondeterministicFiniteAutomata.State>, DeterministicFiniteAutomata.State>()
            var nextChar = 'A'
            result[start.nullClosure] = DeterministicFiniteAutomata.State(nextChar++.toString(), mutableMapOf(), start.nullClosure.any { it.acceptable })
            while (!statesToFind.isEmpty()) {
                val theStates = statesToFind.first()
                statesToFind.remove(theStates)
//                println("Now doing $theStates, $statesToFind remain\n")
                for (ch in alphabetBuffer) {
                    val canGoTo = theStates.directConnectedTo(ch)
                    val equivalenteStates = canGoTo.nullClosure
//                    println("$theStates + $ch = $canGoTo => $equivalenteStates\n")
                    if (equivalenteStates !in result.keys) {
                        result[equivalenteStates] = DeterministicFiniteAutomata.State(nextChar++.toString(), mutableMapOf(), equivalenteStates.any { it.acceptable })
                        statesToFind.add(equivalenteStates)
//                        println("For $equivalenteStates create new state ${result[equivalenteStates]?.name}\n")
                    }
                    result[theStates]!!.transitions[ch!!] = result[equivalenteStates]!!
                }
            }
            return DeterministicFiniteAutomata(result.values.toSet(), result.values.minBy { it.name }!!)
        }
}

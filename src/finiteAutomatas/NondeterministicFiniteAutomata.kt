package finiteAutomatas

import regexParts.TerminalChar

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
}
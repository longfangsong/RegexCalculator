package finiteAutomata

import regex.TerminalCharacter

class Deterministic(states: Set<Deterministic.State>,
                    start: State) : Automata(states, start) {
    class State(name: String, accept: Boolean = false, val transitions: MutableMap<TerminalCharacter, Deterministic.State> = mutableMapOf()) : Automata.State(name, accept) {
        override fun addTransition(transitionRoute: TerminalCharacter, to: Automata.State) {
            to as Deterministic.State
            transitions[transitionRoute] = to
        }
    }


    override fun transitionGraph(): String {
        return states.joinToString("\n") { state ->
            state as Deterministic.State
            state.transitions.map { transition ->
                Companion.graph(state, transition.value, transition.key)
            }.joinToString("\n")
        }
    }
}
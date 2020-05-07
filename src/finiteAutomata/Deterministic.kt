package finiteAutomata

import regex.NullCharacter
import regex.TerminalCharacter

class Deterministic(states: Set<Deterministic.State>,
                    start: State) : Automata(states, start) {
    class State(name: String, accept: Boolean = false, val transitions: MutableMap<TerminalCharacter, Deterministic.State> = mutableMapOf()) : Automata.State(name, accept) {
        override fun addTransition(transitionRoute: TerminalCharacter, to: Automata.State) {
            to as Deterministic.State
            transitions[transitionRoute] = to
        }

        val dead: Boolean
            get() = transitions.values.all { it == this }
    }

    override fun transitionGraph(): String {
        return states.joinToString("\n") { state ->
            state as Deterministic.State
            state.transitions
                    .filter { it.key != NullCharacter }
                    .map { transition ->
                        Companion.graph(state, transition.value, transition.key)
                    }.joinToString("\n")
        }
    }

    val alphabet: Set<TerminalCharacter> = states.first().transitions.keys

    val removeUnreachable: Deterministic
        get() {
            val reachable = mutableSetOf(start)
            val pending = mutableSetOf(start)
            while (pending.isNotEmpty()) {
                val check = pending.first() as Deterministic.State
                for (t in check.transitions) {
                    if (t.key != NullCharacter && !reachable.contains(t.value)) {
                        reachable.add(t.value)
                        pending.add(t.value)
                    }
                }
                pending.remove(check)
            }
            return Deterministic(reachable.map { it as Deterministic.State }.toSet(), start as State)
        }

    val minimized: Deterministic
        get() {
            var stateGroups = states.groupBy { it.accept }.values.map { it.toSet() }.toSet()
            var lastSize: Int
            outmost@ do {
                lastSize = stateGroups.size
                val newStateGroups = stateGroups.toMutableSet()
                for (stateGroup in stateGroups) {
                    for (ch in alphabet) {
                        val splitted = stateGroup.groupBy { state ->
                            state as Deterministic.State
                            stateGroups.find { it ->
                                state.transitions[ch] as Deterministic.State in it
                            }
                        }
                        if (splitted.size != 1) {
                            newStateGroups.remove(stateGroup)
                            newStateGroups.addAll(splitted.values.map { it.toSet() })
                            stateGroups = newStateGroups
                            continue@outmost
                        }
                    }
                }
            } while (stateGroups.size != lastSize)
            val newStates = stateGroups.map { it.first() }.toSet()
            newStates.forEach { state ->
                state as Deterministic.State
                for (ch in alphabet) {
                    val toState = state.transitions[ch] as Deterministic.State
                    state.transitions[ch] = stateGroups.find { toState in it }!!.first() as Deterministic.State
                }
            }
            return Deterministic(newStates.map { it as Deterministic.State }.toSet(), stateGroups.find { start in it }!!.first() as Deterministic.State).removeUnreachable
        }

    fun match(string: String): Boolean {
        var currentState = start as Deterministic.State
        for (ch in string) {
            currentState = currentState.transitions[TerminalCharacter(ch.toString())] as Deterministic.State
            if (currentState.dead)
                return false
        }
        return currentState.accept
    }
}
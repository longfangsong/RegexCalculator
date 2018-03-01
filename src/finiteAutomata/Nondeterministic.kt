package finiteAutomata

import regex.TerminalCharacter
import regex.nullCharacter

class Nondeterministic(states: Set<Nondeterministic.State>,
                       start: Nondeterministic.State) : Automata(states, start) {
    class State(name: String, accept: Boolean = false, val transitions: MutableMap<TerminalCharacter, MutableSet<State>> = mutableMapOf()) : Automata.State(name, accept) {
        override fun addTransition(transitionRoute: TerminalCharacter, to: Automata.State) {
            to as Nondeterministic.State
            if (transitionRoute == nullCharacter && to == this)
                return
            if (transitions[transitionRoute] == null)
                transitions[transitionRoute] = mutableSetOf()
            transitions[transitionRoute]!!.add(to)
        }

        fun equivalentStates(alreadyKnown: Set<Nondeterministic.State> = setOf()): Set<Nondeterministic.State> {
            val directedConnectedTo = transitions[nullCharacter]?.filter { it != this && it !in alreadyKnown }
            val directedConnectedToEquStates = directedConnectedTo?.map { it.equivalentStates(alreadyKnown + this) }?.reduce { acc, set -> acc + set }
            return alreadyKnown + this + (directedConnectedToEquStates ?: setOf())
        }
    }

    override fun transitionGraph(): String {
        return states.joinToString("\n") { state ->
            state as Nondeterministic.State
            state.transitions.map { transition ->
                transition.value.joinToString("\n") { possibleTarget ->
                    Companion.graph(state, possibleTarget, transition.key)
                }
            }.joinToString("\n")
        }
    }

    val alphabet = states.map { it.transitions.map { it.key }.toSet() }.reduce { it1, it2 -> it1 + it2 }.toSet()

    private val Set<Nondeterministic.State>.equivalentStates: Set<Nondeterministic.State>
        get() {
            val everyonesEqu = this.map { it.equivalentStates() }
            if (everyonesEqu.isEmpty())
                return this
            return everyonesEqu.reduce { acc, set -> acc + set }
        }

    private fun Set<Nondeterministic.State>.directConnectedTo(withChar: TerminalCharacter): Set<Nondeterministic.State> {
        val temp = this.map {
            it.transitions[withChar]?.toSet() ?: setOf()
        }
        return if (temp.isEmpty()) setOf() else temp.reduce { it1, it2 -> it1 + it2 }.toSet()
    }

    fun Set<Nondeterministic.State>.connectedTo(withChar: TerminalCharacter): Set<Nondeterministic.State> {
        return this.equivalentStates.directConnectedTo(withChar)
    }

    fun toDFA(): Deterministic {
        start as Nondeterministic.State
        val statesToFind = mutableSetOf(start.equivalentStates())
        val result = mutableMapOf<Set<Nondeterministic.State>, Deterministic.State>()
        var nextChar = 'A'
        result[start.equivalentStates()] = Deterministic.State(
                nextChar++.toString(),
                start.equivalentStates().any { it.accept },
                mutableMapOf())
        while (!statesToFind.isEmpty()) {
            val theStates = statesToFind.first()
            statesToFind.remove(theStates)
            for (ch in alphabet) {
                val canGoTo = theStates.directConnectedTo(ch)
                val equivalenteStates = canGoTo.equivalentStates
                if (equivalenteStates !in result.keys) {
                    result[equivalenteStates] = Deterministic.State(nextChar++.toString(), equivalenteStates.any { it.accept }, mutableMapOf())
                    statesToFind.add(equivalenteStates)
                }
                result[theStates]!!.transitions[ch] = result[equivalenteStates]!!
            }
        }
        return Deterministic(result.values.toSet(), result.values.minBy { it.name }!!)
    }
}
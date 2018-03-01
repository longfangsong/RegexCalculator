package finiteAutomata

import regex.TerminalCharacter
import java.io.Writer

abstract class Automata(
        val states: Set<State>,
        val start: State
) {
    abstract class State(val name: String, val accept: Boolean) {
        override fun toString(): String {
            return name
        }

        abstract fun addTransition(transitionRoute: TerminalCharacter, to: State)
    }

    companion object {
        fun graph(from: State, to: State, though: TerminalCharacter): String {
            return "$from->$to [label=$though];"
        }
    }

    abstract fun transitionGraph(): String

    private fun renderAcceptableStates(): String {
        return states.filter { it.accept }.joinToString("\n") {
            "${it.name}[shape=doublecircle];"
        }
    }

    val graph: String
        get() {
            return "digraph G {\n" +
                    "    node[shape=circle];\n" +
                    "    start[shape=none];\n" +
                    "    start->$start;\n" +
                    renderAcceptableStates() +
                    transitionGraph() +
                    "}"
        }


    fun saveGraph(writer: Writer) {
        writer.write(graph)
    }
}
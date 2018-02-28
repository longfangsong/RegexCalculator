package finiteAutomatas

import regexParts.TerminalChar

class DeterministicFiniteAutomata(
        val states: Set<DeterministicFiniteAutomata.State>,
        val start: DeterministicFiniteAutomata.State
) : HasGraph {
    class State(
            val name: String,
            val transitions: MutableMap<TerminalChar, DeterministicFiniteAutomata.State>,
            var acceptable: Boolean
    )

    override val graph: String
        get() {
            return """
digraph G {
    node[shape=circle];
    start[shape=none];
    ${states.filter { it.acceptable }.joinToString(";\n") { "${it.name}[shape=doublecircle]" }}
    start->${start.name};
    ${states.joinToString(";\n    ") { state ->
                state.transitions.map { transition ->
                    "${state.name}->${transition.value.name} " +
                            "[label=${transition.key}]"
                }.joinToString(";\n    ")
            }}
}"""
        }

//    val simplified: DeterministicFiniteAutomata
//        get() {
//            var seperatedGroups = setOf(states.filter { it.acceptable }.toSet(), states.filter { !it.acceptable }.toSet())
//            do {
//                val oldSeperated = seperatedGroups
//                var newSeparated = setOf<Set<DeterministicFiniteAutomata.State>>()
//                for (group in oldSeperated) {
//
//                }
//            } while ()
//        }
}
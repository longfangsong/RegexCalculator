package finiteAutomatas

import regexParts.TerminalChar

class DeterministicFiniteAutomata(
        val states: Set<DeterministicFiniteAutomata.State>,
        val start: DeterministicFiniteAutomata.State
) {
    class State(
            val transitions: Map<TerminalChar, DeterministicFiniteAutomata.State>
    )
}
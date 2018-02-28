package regexParts

object RegexPartNullChar : RegexPart {
    override fun contains(nonTerminalChar: NonTerminalChar): Boolean {
        return false
    }

    override fun contains(terminalChar: TerminalChar): Boolean {
        return false
    }

    override fun toString(): String {
        return "ε"
    }
}

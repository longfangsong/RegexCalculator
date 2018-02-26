package regexParts

class TerminalChar(val char: Char) : RegexPart {
    override fun toString(): String {
        return char.toString()
    }

    override fun contains(nonTerminalChar: NonTerminalChar): Boolean {
        return false
    }

    override fun contains(terminalChar: TerminalChar): Boolean {
        return terminalChar == this
    }

    override fun equals(other: Any?): Boolean {
        return other is TerminalChar && char == other.char
    }

    override fun hashCode(): Int {
        return char.hashCode()
    }


}
class TerminalChar(val char: Char) : RegexPart {
    override fun toString(): String {
        return char.toString()
    }

    override fun contains(regexPart: RegexPart): Boolean {
        return regexPart == this
    }
}
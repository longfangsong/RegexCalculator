class NonTerminalChar(val char: Char) : RegexPart, Comparable<NonTerminalChar> {
    override fun toString(): String {
        return char.toString()
    }

    companion object {
        private var current = 'A'
        fun next(): NonTerminalChar {
            return NonTerminalChar(current++)
        }

        fun reset(char: Char = 'A') {
            current = char
        }
    }

    override fun compareTo(other: NonTerminalChar): Int {
        return char.compareTo(other.char)
    }

    override fun equals(other: Any?): Boolean {
        return other is NonTerminalChar && char == other.char
    }

    override fun hashCode(): Int {
        return char.hashCode()
    }

    override fun contains(regexPart: RegexPart): Boolean {
        return regexPart == this
    }
}
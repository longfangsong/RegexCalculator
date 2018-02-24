class NonTerminalChar(val char: Char) : Comparable<NonTerminalChar>, SubstitutableRegexPart {
    override fun toString(): String {
        return char.toString()
    }

    override fun substitute(generator: Generator): RegexPart {
        if (this == generator.from)
            return generator.to
        return this
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
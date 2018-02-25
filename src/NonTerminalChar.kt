/**
 * 非终结符，一般用于表示一个正则表达式的一部分。
 */
class NonTerminalChar(private val char: Char) : Comparable<NonTerminalChar>, SubstitutableRegexPart {
    override fun substitute(generator: Generator): RegexPart {
        if (this == generator.from)
            return generator.to
        return this
    }

    /**
     * 非终结符池
     * 实现待改进
     */
    companion object {
        private var current = 'A'
        /**
         * 取池中下一个非终结符
         */
        fun next(): NonTerminalChar {
            return NonTerminalChar(current++)
        }

        /**
         * 复位非终结符池
         */
        fun reset(char: Char = 'A') {
            current = char
        }
    }

    override fun compareTo(other: NonTerminalChar): Int {
        return char.compareTo(other.char)
    }

    override fun toString(): String {
        return char.toString()
    }

    override fun equals(other: Any?): Boolean {
        return other is NonTerminalChar && char == other.char
    }

    override fun hashCode(): Int {
        return char.hashCode()
    }

    override fun contains(nonTerminalChar: NonTerminalChar): Boolean {
        return nonTerminalChar == this
    }

    override fun contains(terminalChar: TerminalChar): Boolean {
        return false
    }

    /**
     * 一个通过 NonTerminalChar 和 RegexPart 来构造 Generator 的快捷方式
     * 构造出形如 NonTerminalChar -> RegexPart 的 Generator
     */
    infix fun to(regexPart: RegexPart): Generator {
        return Generator(this, regexPart)
    }
}
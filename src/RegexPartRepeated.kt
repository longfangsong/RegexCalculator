/**
 * 某个RegexPart的星闭包
 */
class RegexPartRepeated(partToRepeat: RegexPart) : RegexPart {
    val toRepeat: RegexPart = if (partToRepeat is RegexPartRepeated) {
        // 运用幂等律
        partToRepeat.toRepeat
    } else {
        partToRepeat
    }

    override fun contains(nonTerminalChar: NonTerminalChar): Boolean {
        return nonTerminalChar in toRepeat
    }

    override fun contains(terminalChar: TerminalChar): Boolean {
        return terminalChar in toRepeat
    }

    override fun toString(): String {
        return if (toRepeat.toString().length != 1) {
            "($toRepeat)*"
        } else {
            "$toRepeat*"
        }
    }
}
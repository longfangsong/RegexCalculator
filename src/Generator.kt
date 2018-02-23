class Generator(val from: NonTerminalChar, val to: RegexPart) {
    private val isDirectDelegate: Boolean
        get() = to is NonTerminalChar

    private operator fun contains(nonTerminalChar: NonTerminalChar): Boolean {
        return nonTerminalChar in to
    }

    private val regulizedWithDirectDelegate: Set<Generator>
        get() {
            when (to) {
                is NonTerminalChar, is TerminalChar -> return setOf(this)
                is RegexPartConcated -> {
                    if (to.isRegular) {
                        return setOf(this)
                    }
                    val last = to.last
                    if (last is NonTerminalChar) {
                        val init = to.init
                        when (init) {
                            is RegexPartConcated -> {
                                val nextTerminal = NonTerminalChar.next()
                                return Generator(from, init.head concat nextTerminal).regulizedWithDirectDelegate +
                                        Generator(nextTerminal, init.tail concat last).regulizedWithDirectDelegate
                            }
                            is RegexPartOptioned -> {
                                return init.options.map { Generator(from, it concat last).regulizedWithDirectDelegate }.reduce { it1, it2 -> it1 + it2 }
                            }
                            is RegexPartRepeated -> {
                                return Generator(from, init.toRepeat concat from).regulizedWithDirectDelegate + Generator(from, last).regulizedWithDirectDelegate
                            }
                        }
                    }
                    val nextTerminal = NonTerminalChar.next()
                    return Generator(from, to.head concat nextTerminal).regulizedWithDirectDelegate + Generator(nextTerminal, to.tail).regulizedWithDirectDelegate
                }
                is RegexPartOptioned -> {
                    return to.options.map { Generator(from, it).regulizedWithDirectDelegate }.reduce { it1, it2 -> it1 + it2 }
                }
                is RegexPartRepeated -> {
                    return Generator(from, to.toRepeat concat from).regulizedWithDirectDelegate + Generator(from, to.toRepeat).regulizedWithDirectDelegate
                }
            }
            return setOf()
        }

    val regulized: Set<Generator>
        get() {
            val withDirectDelegate = regulizedWithDirectDelegate.toMutableSet()
            while (withDirectDelegate.any { it.isDirectDelegate }) {
                val nextDirectDelegate = withDirectDelegate.first { it.isDirectDelegate }
                withDirectDelegate.remove(nextDirectDelegate)
                val delegateFrom = nextDirectDelegate.from
                val delegateTo = withDirectDelegate.filter { it.from == nextDirectDelegate.to }
                withDirectDelegate.addAll(delegateTo.map { Generator(delegateFrom, it.to) })
            }
            val usedCount = mutableMapOf<NonTerminalChar, Int>()
            for (ele in withDirectDelegate) {
                usedCount[ele.from] = withDirectDelegate.count { ele.from in it }
            }
            return withDirectDelegate.filter { usedCount[it.from] != 0 || it.from == withDirectDelegate.minBy { it.from }?.from }.toSet()
        }

    val simplfiedRegulized: Set<Generator>
        get() = regulized.groupBy { it.from }.map {
            Generator(it.key,
                    if (it.value.size == 1) {
                        it.value.first().to
                    } else {
                        RegexPartOptioned(it.value.map { it.to })
                    })
        }.toSet()

    override fun toString(): String {
        return from.toString() + "->" + to.toString()
    }

    override fun equals(other: Any?): Boolean {
        return other is Generator && from == other.from && to == other.to
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }
}
/**
 * RegexPart间 或 运算的结果
 */
class RegexPartOptioned(val options: MutableSet<RegexPart>) : SubstitutableRegexPart {
    constructor(regex1: RegexPart, regex2: RegexPart) : this(mutableSetOf()) {
        // "展平"嵌套的RegexPartOptioned
        if (regex1 is RegexPartOptioned && regex2 is RegexPartOptioned) {
            options.addAll(regex1.options)
            options.addAll(regex2.options)
        } else if (regex1 is RegexPartOptioned) {
            options.addAll(regex1.options)
            options.add(regex2)
        } else if (regex2 is RegexPartOptioned) {
            options.add(regex1)
            options.addAll(regex2.options)
        } else {
            options.add(regex1)
            options.add(regex2)
        }
    }

    constructor(opt: Collection<RegexPart>) : this(mutableSetOf()) {
        opt.forEach {
            if (it is RegexPartOptioned) {
                // "展平"嵌套的RegexPartOptioned
                options.addAll(it.options)
            } else {
                options.add(it)
            }
        }
    }

    override fun substitute(generator: Generator): RegexPart {
        return RegexPartOptioned(options.map {
            (it as? SubstitutableRegexPart)?.substitute(generator) ?: it
        })
    }

    override fun contains(nonTerminalChar: NonTerminalChar): Boolean {
        return options.any { nonTerminalChar in it }
    }

    override fun contains(terminalChar: TerminalChar): Boolean {
        return options.any { terminalChar in it }
    }

    override fun toString(): String {
        return options.joinToString("|") {
            if (it.toString().length != 1 && it !is RegexPartRepeated && it !is RegexPartConcated) "($it)"
            else it.toString()
        }
    }
}
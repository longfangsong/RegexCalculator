class Grammar(
        val nonTerminals: Set<NonTerminalChar>,
        val terminals: Set<TerminalChar>,
        val rules: Set<Generator>,
        val start: NonTerminalChar
) {
    constructor(rles: Collection<Generator>) : this(
            rles.map { it.from }.toSet(),
            ('a'..'z').map { TerminalChar(it) }.toSet().union(('0'..'9').map { TerminalChar(it) }),
            rles.toSet(),
            rles.minBy { it.from }!!.from
    )

    constructor(regexPart: RegexPart) : this(
            Generator(NonTerminalChar.next(), regexPart).regulized
    )

    val simplifiedRules: Set<Generator>
        get() = rules.groupBy { it.from }.map {
            Generator(it.key,
                    if (it.value.size == 1) {
                        it.value.first().to
                    } else {
                        RegexPartOptioned(it.value.map { it.to })
                    })
        }.toSet()

    private fun simplify(generator: Generator): Generator {
        val from = generator.from
        val to = generator.to
        return if (from in to) {
            when (to) {
                is RegexPartConcated -> simplify(Generator(from, to.init.repeat()))
                is RegexPartOptioned -> {
                    val firstItem = to.options.find { generator.from in it } as RegexPartConcated
                    simplify(Generator(from, firstItem.init.repeat() concat
                            RegexPartOptioned(to.options.filter { it != firstItem })))
                }
                else -> throw NotImplementedError()
            }
        } else {
            generator
        }
    }

    private fun simplify(mutableSet: Collection<Generator>): MutableSet<Generator> {
        return mutableSet.map { simplify(it) }.toMutableSet()
    }

    fun toRegex(): RegexPart {
        var theSet = simplifiedRules.toMutableSet()
        theSet = simplify(theSet)
        while (theSet.size > 1) {
            val nextGeneratorToKill = theSet.findLast { it.from != start }
            theSet.remove(nextGeneratorToKill)
            theSet = theSet.map { it.substitute(nextGeneratorToKill!!) }.toMutableSet()
            theSet = simplify(theSet)
        }
        return theSet.first().to
    }
}
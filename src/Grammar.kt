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
}
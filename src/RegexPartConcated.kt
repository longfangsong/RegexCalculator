/**
 * 连接过的 RegexPart
 * @property list 按顺序保存了每个连接上的 RegexPart
 */
class RegexPartConcated(private val list: MutableList<RegexPart>) : SubstitutableRegexPart {
    constructor(part1: RegexPart, part2: RegexPart) : this(mutableListOf<RegexPart>()) {
        // "展平"嵌套的RegexPartConcated
        if (part1 is RegexPartConcated && part2 is RegexPartConcated) {
            list.addAll(part1.list)
            list.addAll(part2.list)
        } else if (part1 is RegexPartConcated) {
            list.addAll(part1.list)
            list.add(part2)
        } else if (part2 is RegexPartConcated) {
            list.add(part1)
            list.addAll(part2.list)
        } else {
            list.add(part1)
            list.add(part2)
        }
    }

    constructor(l: Collection<RegexPart>) : this(mutableListOf()) {
        l.forEach {
            if (it is RegexPartConcated) {
                // "展平"嵌套的RegexPartConcated
                list.addAll(it.list)
            } else {
                list.add(it)
            }
        }
    }

    override fun substitute(generator: Generator): RegexPart {
        if (last == generator.from) {
            return init concat generator.to
        }
        return this
    }

    override fun toString(): String {
        return list.joinToString(".") {
            if (it.toString().length != 1 && it !is RegexPartRepeated) "($it)"
            else it.toString()
        }
    }

    override fun contains(nonTerminalChar: NonTerminalChar): Boolean {
        return list.any { nonTerminalChar in it }
    }

    override fun contains(terminalChar: TerminalChar): Boolean {
        return list.any { terminalChar in it }
    }

    /**
     * 判断是否符合正规文法的要求
     */
    val isRegular: Boolean
        get() = list.size == 2 && list[0] is TerminalChar && list[1] is NonTerminalChar

    /**
     * 仿haskell列表操作的一些 property
     */
    val head: RegexPart
        get() = list.first()
    val tail: RegexPart
        get() = if (list.size == 2) {
            list[1]
        } else {
            RegexPartConcated(list.drop(1))
        }
    val last: RegexPart
        get() = list.last()
    val init: RegexPart
        get() = if (list.size == 2) {
            list[0]
        } else {
            RegexPartConcated(list.dropLast(1))
        }
}
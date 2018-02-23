class RegexPartConcated(val list: MutableList<RegexPart>) : RegexPart {
    constructor(part1: RegexPart, part2: RegexPart) : this(mutableListOf<RegexPart>()) {
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

    constructor(l: Collection<RegexPart>) : this(l.toMutableList())

    override fun toString(): String {
        return list.joinToString(".") {
            if (it.toString().length != 1 && it !is RegexPartRepeated) "($it)"
            else it.toString()
        }
    }

    override fun contains(regexPart: RegexPart): Boolean {
        return list.any { regexPart in it }
    }

    val isRegular: Boolean
        get() = list.size == 2 && list[0] is TerminalChar && list[1] is NonTerminalChar

    val head: RegexPart
        get() = list.first()
    val tail: RegexPart
        get() = if (list.size == 2) {
            list[1]
        } else {
            RegexPartConcated(list.drop(1).toMutableList())
        }
    val last: RegexPart
        get() = list.last()
    val init: RegexPart
        get() = if (list.size == 2) {
            list[0]
        } else {
            RegexPartConcated(list.dropLast(1).toMutableList())
        }
}
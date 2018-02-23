class RegexPartOptioned(val options: MutableSet<RegexPart>) : RegexPart {
    constructor(regex1: RegexPart, regex2: RegexPart) : this(mutableListOf()) {
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
                options.addAll(it.options)
            } else {
                options.add(it)
            }
        }
    }

    override fun contains(regexPart: RegexPart): Boolean {
        return options.any { regexPart in it }
    }

    override fun toString(): String {
        return options.joinToString("|") {
            if (it.toString().length != 1 && it !is RegexPartRepeated && it !is RegexPartConcated) "($it)"
            else it.toString()
        }
    }
}
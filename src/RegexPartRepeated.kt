class RegexPartRepeated(var toRepeat: RegexPart) : RegexPart {
    init {
        if (toRepeat is RegexPartRepeated) {
            this.toRepeat = (toRepeat as RegexPartRepeated).toRepeat
        }
    }

    override fun contains(regexPart: RegexPart): Boolean {
        return regexPart in toRepeat
    }

    override fun toString(): String {
        return if (toRepeat.toString().length != 1) {
            "($toRepeat)*"
        } else {
            "$toRepeat*"
        }
    }

}
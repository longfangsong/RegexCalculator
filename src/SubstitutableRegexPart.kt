interface SubstitutableRegexPart : RegexPart {
    fun substitute(generator: Generator): RegexPart
}
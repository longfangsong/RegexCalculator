package regex

class Regex(val component: RegexComponent) {
    constructor(string: String) : this(RegexComponentFactory.fromString(string))
}
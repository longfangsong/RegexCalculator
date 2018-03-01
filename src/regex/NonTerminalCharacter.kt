package regex

import grammar.Generator

class NonTerminalCharacter(name: String) : Character(name) {
    override fun equals(other: Any?): Boolean {
        return other is NonTerminalCharacter && super.equals(other)
    }

    infix fun to(regexComponent: RegexComponent): Generator {
        return Generator(this, regexComponent)
    }

    infix fun to(regex: Regex): Generator {
        return this to regex.component
    }

    override fun contains(item: NonTerminalCharacter): Boolean {
        return this == item
    }

    companion object {
        private var nextCharacter = 'A'

        fun next(): NonTerminalCharacter {
            return NonTerminalCharacter(nextCharacter++.toString())
        }

        fun reset(char: Char = 'A') {
            nextCharacter = char
        }
    }

    override val alphabet = setOf<TerminalCharacter>()

    override fun substituteWith(generatorToKill: Generator): RegexComponent {
        if (this == generatorToKill.from) {
            return generatorToKill.to
        }
        return this
    }

}
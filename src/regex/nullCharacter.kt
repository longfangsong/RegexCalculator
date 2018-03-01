package regex

import grammar.Generator

object nullCharacter : TerminalCharacter("ε") {
    override fun equals(other: Any?): Boolean {
        return other === this
    }

    override fun concat(other: RegexComponent): RegexComponent {
        return other
    }

    override fun repeat(): RegexComponent {
        return this
    }

    override fun contains(item: NonTerminalCharacter): Boolean {
        return false
    }

    override val alphabet = setOf<TerminalCharacter>()

    override fun substituteWith(generatorToKill: Generator): RegexComponent {
        return this
    }
}
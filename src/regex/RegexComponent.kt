package regex

import grammar.Generator

interface RegexComponent {
    override fun toString(): String

    infix fun concat(other: RegexComponent): RegexComponent {
        return RegexComponentFactory.concated(this, other)
    }

    infix fun or(other: RegexComponent): RegexComponent {
        return RegexComponentFactory.optioned(this, other)
    }

    fun repeat(): RegexComponent {
        return RegexComponentFactory.repeated(this)
    }

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    operator fun contains(item: NonTerminalCharacter): Boolean

    val alphabet: Set<TerminalCharacter>

    fun substituteWith(generatorToKill: Generator): RegexComponent
}
package regex

import grammar.Generator

open class TerminalCharacter(name: String) : Character(name) {
    override fun equals(other: Any?): Boolean {
        return other is TerminalCharacter && super.equals(other)
    }

    override fun contains(item: NonTerminalCharacter): Boolean {
        return false
    }

    override val alphabet: Set<TerminalCharacter>
        get() {
            return setOf(this)
        }

    override fun substituteWith(generatorToKill: Generator): RegexComponent {
        return this
    }
}
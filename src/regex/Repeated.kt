package regex

import grammar.Generator

class Repeated(val toRepeat: RegexComponent) : RegexComponent {
    override fun toString(): String {
        return if (toRepeat.toString().length <= 1) {
            "$toRepeat*"
        } else {
            "($toRepeat)*"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Repeated

        if (toRepeat != other.toRepeat) return false

        return true
    }

    override fun hashCode(): Int {
        return toRepeat.hashCode()
    }

    override fun contains(item: NonTerminalCharacter): Boolean {
        return item in toRepeat
    }

    override val alphabet = toRepeat.alphabet

    override fun substituteWith(generatorToKill: Generator): RegexComponent {
        return (toRepeat.substituteWith(generatorToKill)).repeat()
    }
}
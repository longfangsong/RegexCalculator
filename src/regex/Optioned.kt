package regex

import grammar.Generator

class Optioned(val components: Set<RegexComponent>) : RegexComponent {
    constructor(componentCollection: Collection<RegexComponent>) : this(componentCollection.toSet())

    override fun toString(): String {
        return components.joinToString("|") { it.toString() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Optioned

        if (components != other.components) return false

        return true
    }

    override fun hashCode(): Int {
        return components.hashCode()
    }

    override fun contains(item: NonTerminalCharacter): Boolean {
        return components.any { item in it }
    }

    override val alphabet = components.map { it.alphabet }.reduce { acc, set -> acc + set }

    override fun substituteWith(generatorToKill: Generator): RegexComponent {
        return components.map { it.substituteWith(generatorToKill) }.reduce { acc, regexComponent -> acc or regexComponent }
    }
}
package regex

import grammar.Generator

class Concated(val components: List<RegexComponent>) : RegexComponent {
    override fun toString(): String {
        return components.joinToString(".") { it.toString() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Concated
        if (components != other.components) return false
        return true
    }

    override fun hashCode(): Int {
        return components.hashCode()
    }

    override fun contains(item: NonTerminalCharacter): Boolean {
        return components.any { item in it }
    }

    /**
     * 判断是否符合正规文法的要求
     */
    val isRegular = components.size == 2 && components[0] is TerminalCharacter && components[1] is NonTerminalCharacter

    /**
     * 仿haskell列表操作的一些 property
     * [*,*,*,*,*]
     *  ^           head
     *          ^   last
     *  ^,^,^,^     init
     *    ^,^,^,^   tail
     */
    val head = components.first()

    val tail = if (components.size == 2) {
        components[1]
    } else {
        Concated(components.drop(1))
    }

    val last = components.last()

    val init = if (components.size == 2) {
        components[0]
    } else {
        Concated(components.dropLast(1))
    }

    override val alphabet = components.map { it.alphabet }.reduce { acc, set -> acc + set }

    override fun substituteWith(generatorToKill: Generator): RegexComponent {
        return components.map { it.substituteWith(generatorToKill) }.reduce { acc, regexComponent -> acc concat regexComponent }
    }
}
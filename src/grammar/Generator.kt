package grammar

import regex.*

class Generator(
        val from: NonTerminalCharacter,
        val to: RegexComponent
) {
    override fun toString(): String {
        return "$from->$to"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Generator

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }

    val isDirectDelegate = to is NonTerminalCharacter

    /**
     * 与这个推导式等价的，包含直接推导到另外一个非终结符（如 A->B ）的正规化推导式集合
     */
    val regulizedWithDirectDelegate: Set<Generator> =
            when (to) {
                is Concated -> {
                    if (to.isRegular) {
                        setOf(this)
                    } else {
                        val last = to.last
                        if (last is NonTerminalCharacter) {
                            endWithOtherNonTerminalRegulized(to, last)
                        } else {
                            // A->abc
                            // A->aB, B->bc
                            val nextTerminal = NonTerminalCharacter.next()
                            (from to (to.head concat nextTerminal)).regulizedWithDirectDelegate + (nextTerminal to to.tail).regulizedWithDirectDelegate

                        }
                    }
                }
                is Optioned -> {
                    // A->a|b
                    // => A->a, A->b
                    to.components.map { (from to it).regulizedWithDirectDelegate }.reduce { acc, set -> acc + set }
                }
                is Repeated -> {
                    // A->a*
                    // A->aA, A->ε
                    (from to (to.toRepeat concat from)).regulizedWithDirectDelegate + (from to nullCharacter).regulizedWithDirectDelegate
                }
                else -> setOf(this)
            }

    /**
     * 消除直接推导到另外一个非终结符（如 A->B ）的推导式
     */
    private fun eliminateDirectDelegate(items: Set<Generator>): MutableSet<Generator> {
        val result = items.toMutableSet()
        while (result.any { it.isDirectDelegate }) {
            val nextDirectDelegate = result.first { it.isDirectDelegate }
            result.remove(nextDirectDelegate)
            val delegateFrom = nextDirectDelegate.from
            val delegateTo = result.filter { it.from == nextDirectDelegate.to }
            result.addAll(delegateTo.map { Generator(delegateFrom, it.to) })
        }
        return result
    }

    /**
     * 移除不被其他推导式使用的推导式
     */
    private fun removeUselessGenerator(eliminated: MutableSet<Generator>): Set<Generator> {
        val usedCount = mutableMapOf<NonTerminalCharacter, Int>()
        for (ele in eliminated) {
            usedCount[ele.from] = eliminated.count { ele.from in it }
        }
        return eliminated.filter { usedCount[it.from] != 0 || it.from == eliminated.minBy { it.from }?.from }.toSet()
    }

    private operator fun contains(item: NonTerminalCharacter): Boolean {
        return item in to
    }

    /**
     * 真正的正规化推导式集合
     */
    val regulized: Set<Generator> = removeUselessGenerator(eliminateDirectDelegate(regulizedWithDirectDelegate))

    private fun endWithOtherNonTerminalRegulized(to: Concated, last: NonTerminalCharacter): Set<Generator> {
        // 形如 A->...B 的推导式
        // 此时应该针对最后一个字符之前的所有字符处理
        val init = to.init
        return when (init) {
            is Concated -> {
                // A->abB
                // => A->aC, C->bB
                val nextNonTerminal = NonTerminalCharacter.next()
                (from to (init.head concat nextNonTerminal)).regulizedWithDirectDelegate +
                        (nextNonTerminal to (init.tail concat last)).regulizedWithDirectDelegate
            }
            is Optioned -> {
                // | 的运算律应该会保证这一个分支不会被执行到
                // 出于完整性与保险起见保留
                // A->(a|b)B
                // => A->aB, A->bB
                init.components.map { (from to (it concat last)).regulizedWithDirectDelegate }.reduce { it1, it2 -> it1 + it2 }
            }
            is Repeated -> {
                // A->a*B
                // A->aA, A->B
                (from to (init.toRepeat concat from)).regulizedWithDirectDelegate + (from to last).regulizedWithDirectDelegate
            }
            else -> {
                throw UnknownError("Should never execute this!")
            }
        }
    }

    val alphabet = to.alphabet
    fun substituteWith(nextGeneratorToKill: Generator): Generator {
        return from to to.substituteWith(nextGeneratorToKill)
    }
}
package grammar

import regexParts.*

/**
 * 推导式
 */
class Generator(val from: NonTerminalChar, val to: RegexPart) {
    private val isDirectDelegate: Boolean
        get() = to is NonTerminalChar

    private operator fun contains(nonTerminalChar: NonTerminalChar): Boolean {
        return nonTerminalChar in to
    }

    /**
     * 与这个推导式等价的，包含直接推导到另外一个非终结符（如 A->B ）的正规化推导式集合
     */
    private val regulizedWithDirectDelegate: Set<Generator>
        get() {
            when (to) {
                is NonTerminalChar, is TerminalChar -> return setOf(this)
                is RegexPartConcated -> {
                    if (to.isRegular) {
                        return setOf(this)
                    }
                    val last = to.last
                    if (last is NonTerminalChar) {
                        // 即形如 A->...B 的推导式
                        // 此时应该针对最后一个字符之前的所有字符处理
                        val init = to.init
                        when (init) {
                            is RegexPartConcated -> {
                                val nextNonTerminal = NonTerminalChar.next()
                                return Generator(from, init.head concat nextNonTerminal).regulizedWithDirectDelegate +
                                        Generator(nextNonTerminal, init.tail concat last).regulizedWithDirectDelegate
                            }
                            is RegexPartOptioned -> {
                                return init.options.map { Generator(from, it concat last).regulizedWithDirectDelegate }.reduce { it1, it2 -> it1 + it2 }
                            }
                            is RegexPartRepeated -> {
                                return Generator(from, init.toRepeat concat from).regulizedWithDirectDelegate + Generator(from, last).regulizedWithDirectDelegate
                            }
                        }
                    }
                    val nextTerminal = NonTerminalChar.next()
                    return Generator(from, to.head concat nextTerminal).regulizedWithDirectDelegate + Generator(nextTerminal, to.tail).regulizedWithDirectDelegate
                }
                is RegexPartOptioned -> {
                    return to.options.map { Generator(from, it).regulizedWithDirectDelegate }.reduce { it1, it2 -> it1 + it2 }
                }
                is RegexPartRepeated -> {
                    return Generator(from, to.toRepeat concat from).regulizedWithDirectDelegate + Generator(from, to.toRepeat).regulizedWithDirectDelegate
                }
            }
            return setOf()
        }

    /**
     * 消除直接推导到另外一个非终结符（如 A->B ）的推导式
     */
    private fun eliminateDirectDelegate(): MutableSet<Generator> {
        val withDirectDelegate = regulizedWithDirectDelegate.toMutableSet()
        while (withDirectDelegate.any { it.isDirectDelegate }) {
            val nextDirectDelegate = withDirectDelegate.first { it.isDirectDelegate }
            withDirectDelegate.remove(nextDirectDelegate)
            val delegateFrom = nextDirectDelegate.from
            val delegateTo = withDirectDelegate.filter { it.from == nextDirectDelegate.to }
            withDirectDelegate.addAll(delegateTo.map { Generator(delegateFrom, it.to) })
        }
        return withDirectDelegate
    }

    /**
     * 移除不被其他推导式使用的推导式
     */
    private fun removeUselessGenerator(eliminated: MutableSet<Generator>): Set<Generator> {
        val usedCount = mutableMapOf<NonTerminalChar, Int>()
        for (ele in eliminated) {
            usedCount[ele.from] = eliminated.count { ele.from in it }
        }
        return eliminated.filter { usedCount[it.from] != 0 || it.from == eliminated.minBy { it.from }?.from }.toSet()
    }

    /**
     * 真正的正规化推导式集合
     */
    val regulized: Set<Generator>
        get() {
            val eliminated = eliminateDirectDelegate()
            return removeUselessGenerator(eliminated)
        }

    /**
     * 简化过的正规化推导式集合
     * 即将A->aB, A->bB 变为A->aB|bB
     */
    val simplfiedRegulized: Set<Generator>
        get() = regulized.groupBy { it.from }.map {
            Generator(it.key,
                    if (it.value.size == 1) {
                        it.value.first().to
                    } else {
                        RegexPartOptioned(it.value.map { it.to })
                    })
        }.toSet()

    fun substitute(generator: Generator): Generator {
        if (to is SubstitutableRegexPart)
            return Generator(from, to.substitute(generator))
        return this
    }

    override fun toString(): String {
        return from.toString() + "->" + to.toString()
    }

    override fun equals(other: Any?): Boolean {
        return other is Generator && from == other.from && to == other.to
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }
}
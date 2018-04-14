package grammar

import finiteAutomata.Nondeterministic
import regex.*

class Grammar(
        val nonTerminals: Set<NonTerminalCharacter>,
        val terminals: Set<TerminalCharacter>,
        val rules: Set<Generator>,
        val start: NonTerminalCharacter
) {
    constructor(rles: Collection<Generator>) : this(
            rles.map { it.from }.toSet(),
            rles.map { it.alphabet }.reduce { acc, set -> acc + set },
            rles.toSet(),
            rles.minBy { it.from }!!.from
    )

    constructor(regex: Regex) : this(
            (NonTerminalCharacter.next() to regex).regulized
    )

    val simplifiedRules = rules.groupBy { it.from }.map {
        Generator(it.key,
                if (it.value.size == 1) {
                    it.value.first().to
                } else {
                    Optioned(it.value.map { it.to })
                })
    }.toSet()

    /**
     * 简化一个Generator
     * 即将一个左侧包含右侧的Generator（如 A->aA, B->a|bA|bB）
     * 化简为左侧不含右侧的Generator（如A->a*, B->b*a|b*bA）
     */
    private fun simplify(generator: Generator): Generator {
        val from = generator.from
        val to = generator.to
        return if (from in to) {
            when (to) {
                is Concated -> simplify(Generator(from, to.init.repeat()))
                is Optioned -> {
                    val firstItem = to.components.find { generator.from in it } as Concated
                    simplify(Generator(from, firstItem.init.repeat() concat
                            Optioned(to.components.filter { it != firstItem })))
                }
                else -> throw NotImplementedError()
            }
        } else {
            generator
        }
    }

    /**
     * simplify一系列 Generator
     */
    private fun simplify(mutableSet: Collection<Generator>): MutableSet<Generator> {
        return mutableSet.map { simplify(it) }.toMutableSet()
    }

    /**
     * 将文法转化到正则表达式
     */
    fun toRegex(): Regex {
        var theSet = simplifiedRules.toMutableSet()
        theSet = simplify(theSet)
        while (theSet.size > 1) {
            val nextGeneratorToKill = theSet.findLast { it.from != start }
            theSet.remove(nextGeneratorToKill)
            theSet = theSet.map { it.substituteWith(nextGeneratorToKill!!) }.toMutableSet()
            theSet = simplify(theSet)
        }
        return Regex(theSet.first().to)
    }

    fun toNFA(): Nondeterministic {
        val accepedState = Nondeterministic.State("AC", true)
        val states = rules.map { Nondeterministic.State(it.from.toString()) }.toSet() + setOf(accepedState)
        val start = states.find { it.name == start.toString() }!!
        rules.forEach {
            val from = it.from
            val theState = states.find { it.name == from.toString() }
            when {
                it.to is TerminalCharacter -> {
                    val transitionRoute = it.to
                    theState?.addTransition(transitionRoute, accepedState)
                }
                it.to is Concated -> {
                    val transitionRoute = it.to.head as TerminalCharacter
                    val nextStateName = it.to.tail.toString()
                    val to = states.find { it.name == nextStateName }
                    theState?.addTransition(transitionRoute, to!!)
                }
                it.to == NullCharacter -> theState?.addTransition(NullCharacter, accepedState)
            }
        }
        return Nondeterministic(states, start)
    }
}
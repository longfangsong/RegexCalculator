package regexParts

import grammar.Generator

/**
 * 可以用其他Generator替换其中一个Nonterminal的RegexPart
 */
interface SubstitutableRegexPart : RegexPart {
    fun substitute(generator: Generator): RegexPart
}
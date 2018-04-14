package regex

import tools.*

object RegexComponentFactory {
    fun concated(component1: RegexComponent, component2: RegexComponent): RegexComponent {
        return if (component2 == NullCharacter) {
            component1
        } else if (component1 is Optioned && component2 is Optioned) {
            Optioned(
                    component1.components.map { optionFromComponent1 ->
                        component2.components.map { optionFromComponent2 ->
                            optionFromComponent1 concat optionFromComponent2
                        }.toSet()
                    }.reduce { acc, set -> acc + set }
            )
        } else if (component1 is Optioned) {
            Optioned(component1.components.map { it concat component2 })
        } else if (component2 is Optioned) {
            Optioned(component2.components.map { component1 concat it })
        } else if (component1 is Concated && component2 is Concated) {
            Concated(component1.components + component2.components)
        } else if (component1 is Concated) {
            Concated(component1.components + component2)
        } else if (component2 is Concated) {
            Concated(listOf(component1) + component2.components)
        } else {
            Concated(listOf(component1, component2))
        }
    }

    fun optioned(component1: RegexComponent, component2: RegexComponent): RegexComponent {
        val result = if (component1 is Optioned && component2 is Optioned) {
            Optioned(component1.components + component2.components)
        } else if (component1 is Optioned) {
            Optioned(component1.components + component2)
        } else if (component2 is Optioned) {
            Optioned(setOf(component1) + component2.components)
        } else {
            Optioned(setOf(component1, component2))
        }
        return if (result.components.size == 1) {
            result.components.first()
        } else {
            result
        }
    }

    fun repeated(component: RegexComponent): RegexComponent {
        return component as? Repeated ?: Repeated(component)
    }

    private fun addOmittedDotOperator(str: String): String {
        val theString = StringBuilder()
        for ((index, ch) in str.withIndex()) {
            theString.append(ch)
            if (index != str.length - 1) {
                if ((ch.isLowerCase() || ch.isDigit() || ch == ')') &&
                        (str[index + 1].isLowerCase() || str[index + 1].isDigit() || str[index + 1] == '(') ||
                        (ch == '*' && !isRegexOperator(str[index + 1]) && str[index + 1] != ')')) {
                    theString.append('.')
                }
            }
        }
        return theString.toString()
    }

    private fun fromFormalString(string: String): RegexComponent {
        if (string == "")
            return NullCharacter
        try {
            return when {
                string.startsWith('(') && pairedRightBracketIndex(string) == string.length - 1 ->
                    fromFormalString(eraseUselessBracketPairs(string))
                string.length == 1 ->
                    TerminalCharacter(string)
                string.firstLayerContain('|') ->
                    string.splitFirstLayerBy('|').map { fromFormalString(it) }.reduce { acc, regexPart -> acc or regexPart }
                string.firstLayerContain('.') ->
                    string.splitFirstLayerBy('.').map { fromFormalString(it) }.reduce { acc, regexPart -> acc concat regexPart }
                string.endsWith('*') ->
                    if (string.startsWith('(')) {
                        fromFormalString(string.slice(1 until string.length - 2)).repeat()
                    } else {
                        fromFormalString(string.slice(0 until string.length - 1)).repeat()
                    }
                else ->
                    throw IllegalArgumentException("Can not construct from string $string")
            }
        } catch (_: StringIndexOutOfBoundsException) {
            throw IllegalArgumentException("Can not construct from string $string")
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("Can not construct from string $string")
        }
    }

    /**
     * 从字符串中构造 RegexComponent
     */
    fun fromString(string: String): RegexComponent {
        return fromFormalString(addOmittedDotOperator(string))
    }
}
interface RegexPart {
    infix fun concat(other: RegexPart): RegexPart {
        if (this is RegexPartOptioned && other is RegexPartOptioned) {
            return RegexPartOptioned(this.options.map { thisOption ->
                RegexPartOptioned(other.options.map { otherOption ->
                    thisOption concat otherOption
                })
            })
        } else if (this is RegexPartOptioned) {
            return RegexPartOptioned(this.options.map { thisOption ->
                thisOption concat other
            })
        } else if (other is RegexPartOptioned) {
            return RegexPartOptioned(other.options.map { otherOption ->
                this concat otherOption
            })
        }
        return RegexPartConcated(this, other)
    }

    infix fun or(other: RegexPart): RegexPart {
        return RegexPartOptioned(this, other)
    }

    fun repeat(): RegexPart {
        return RegexPartRepeated(this)
    }

    operator fun contains(regexPart: RegexPart): Boolean

    override fun toString(): String

    companion object {
        private fun preprocess(str: String): String {
            val theString = StringBuilder()
            for ((index, ch) in str.withIndex()) {
                theString.append(ch)
                if (index != str.length - 1) {
                    if ((ch.isLowerCase() || ch.isDigit() || ch == ')') &&
                            (str[index + 1].isLowerCase() || str[index + 1].isDigit() || str[index + 1] == '(') ||
                            (ch == '*' && !isOperator(str[index + 1]) && str[index + 1] != ')')) {
                        theString.append('.')
                    }
                }
            }
            return theString.toString()
        }

        private fun fromPreprocessedString(string: String): RegexPart {
            return when {
                string.startsWith('(') && pairedRightBracketIndex(string) == string.length - 1 -> fromPreprocessedString(eraseUselessBracketPairs(string))
                string.length == 1 -> TerminalChar(string[0])
                string.firstLayerContain('|') -> RegexPartOptioned(string.splitFirstLayerBy('|').map { fromPreprocessedString(it) })
                string.firstLayerContain('.') -> RegexPartConcated(string.splitFirstLayerBy('.').map { fromPreprocessedString(it) })
                string.endsWith('*') -> if (string.startsWith('(')) {
                    RegexPartRepeated(fromPreprocessedString(string.slice(1 until string.length - 2)))
                } else {
                    RegexPartRepeated(fromPreprocessedString(string.slice(0 until string.length - 1)))
                }
                else -> {
                    throw IllegalArgumentException("Can not construct from string $string")
                }
            }
        }

        fun fromString(string: String): RegexPart {
            return fromPreprocessedString(preprocess(string))
        }
    }
}
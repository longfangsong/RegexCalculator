/**
 * 所有正则表达式中的元素都要实现的接口
 */
interface RegexPart {
    /**
     * 连接运算
     */
    infix fun concat(other: RegexPart): RegexPart {
        if (this is RegexPartOptioned && other is RegexPartOptioned) {
            // 两个 RegexPartOptioned 连接，要运用分配律
            // (a|b).(c|d) == ac | ad | bc | bd
            return RegexPartOptioned(this.options.map { thisOption ->
                RegexPartOptioned(other.options.map { otherOption ->
                    thisOption concat otherOption
                })
            })
        } else if (this is RegexPartOptioned) {
            // 当前是 RegexPartOptioned ，分配律
            return RegexPartOptioned(this.options.map { thisOption ->
                thisOption concat other
            })
        } else if (other is RegexPartOptioned) {
            // other 是 RegexPartOptioned ，分配律
            return RegexPartOptioned(other.options.map { otherOption ->
                this concat otherOption
            })
        }
        // 直接拼接
        return RegexPartConcated(this, other)
    }

    /**
     * 或运算
     */
    infix fun or(other: RegexPart): RegexPart {
        return RegexPartOptioned(this, other)
    }

    /**
     * 重复（取星闭包）运算
     */
    fun repeat(): RegexPart {
        return RegexPartRepeated(this)
    }

    /**
     * 检查表达式中是否包含 @arg nonTerminalChar
     */
    operator fun contains(nonTerminalChar: NonTerminalChar): Boolean

    /**
     * 检查表达式中是否包含 @arg terminalChar
     */
    operator fun contains(terminalChar: TerminalChar): Boolean

    override fun toString(): String

    companion object {
        private fun preprocess(str: String): String {
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

        /**
         * 从字符串中构造RegexPart
         */
        fun fromString(string: String): RegexPart {
            return fromPreprocessedString(preprocess(string))
        }
    }
}
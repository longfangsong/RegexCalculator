/**
 * 判断 @arg char 是否是正则表达式运算符
 */
fun isRegexOperator(char: Char) = char == '|' || char == '*' || char == '.'

/**
 * 找到和某个'('相匹配的')'
 */
fun pairedRightBracketIndex(str: String, leftBracketIndex: Int = 0): Int {
    var state = 1
    var nowIndex = leftBracketIndex + 1
    while (state != 0) {
        when (str[nowIndex]) {
            '(' -> ++state
            ')' -> --state
        }
        ++nowIndex
    }
    return nowIndex - 1
}

/**
 * 移除一个字符串两侧无用的括号
 */
fun eraseUselessBracketPairs(str: String): String {
    if (str.startsWith('(') && pairedRightBracketIndex(str) == str.length - 1) {
        return eraseUselessBracketPairs(str.slice(1 until str.length - 1))
    }
    return str
}

/**
 * 判断一个字符串的第一层是否有某个字符
 * "第一层"指所有括号之外
 */
fun String.firstLayerContain(char: Char): Boolean {
    var i = 0
    while (i != length - 1) {
        when (this[i]) {
            char -> return true
            '(' -> i = pairedRightBracketIndex(this, i)
            else -> ++i
        }
    }
    return false
}

/**
 * 将第一层用 @arg char split开
 */
fun String.splitFirstLayerBy(char: Char): List<String> {
    if (startsWith('(') && pairedRightBracketIndex(this) == length - 1) {
        return slice(1 until length - 1).splitFirstLayerBy(char)
    }
    val theList = mutableListOf<String>()
    var i = 0
    var lastSplitIndex = 0
    while (i != length - 1) {
        when (this[i]) {
            char -> {
                theList.add(substring(lastSplitIndex until i))
                lastSplitIndex = i + 1
                ++i
            }
            '(' -> {
                i = pairedRightBracketIndex(this, i)
            }
            else -> ++i
        }
    }
    theList.add(substring(lastSplitIndex))
    return theList.toList()
}

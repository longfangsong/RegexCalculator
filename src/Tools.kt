fun isOperator(char: Char) = char == '|' || char == '*' || char == '.'

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

fun eraseUselessBracketPairs(str: String): String {
    if (str.startsWith('(') && pairedRightBracketIndex(str) == str.length - 1) {
        return eraseUselessBracketPairs(str.slice(1 until str.length - 1))
    }
    return str
}

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

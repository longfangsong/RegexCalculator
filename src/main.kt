import grammar.Grammar

fun main(args: Array<String>) {
    println(Grammar(regex.Regex("1(1010*|1(010)*1)*0"))
            .toNFA()
            .toDFA()
            .minimized
            .graph
    )
}
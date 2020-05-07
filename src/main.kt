import grammar.Grammar

fun main(args: Array<String>) {
    println(Grammar(regex.Regex("(((1|2)(1|2)(1|2))|21|12|22)(1|2)*"))
            .toNFA()
            .toDFA()
            .minimized
            .graph
    )
}
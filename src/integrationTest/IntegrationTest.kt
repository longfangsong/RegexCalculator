package integrationTest

import grammar.Grammar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import regex.Regex

internal class IntegrationTest {
    @Test
    fun test() {
        assertEquals("digraph G {\n" +
                "    node[shape=circle];\n" +
                "    start[shape=none];\n" +
                "    start->A;\n" +
                "E[shape=doublecircle];\n" +
                "H[shape=doublecircle];\n" +
                "J[shape=doublecircle];\n" +
                "K[shape=doublecircle];\n" +
                "M[shape=doublecircle];A->B [label=1];\n" +
                "A->C [label=0];\n" +
                "B->D [label=1];\n" +
                "B->E [label=0];\n" +
                "C->C [label=1];\n" +
                "C->C [label=0];\n" +
                "D->B [label=1];\n" +
                "D->F [label=0];\n" +
                "E->C [label=1];\n" +
                "E->C [label=0];\n" +
                "F->G [label=1];\n" +
                "F->C [label=0];\n" +
                "G->D [label=1];\n" +
                "G->H [label=0];\n" +
                "H->I [label=1];\n" +
                "H->J [label=0];\n" +
                "I->I [label=1];\n" +
                "I->K [label=0];\n" +
                "J->L [label=1];\n" +
                "J->M [label=0];\n" +
                "K->G [label=1];\n" +
                "K->C [label=0];\n" +
                "L->B [label=1];\n" +
                "L->N [label=0];\n" +
                "M->D [label=1];\n" +
                "M->M [label=0];\n" +
                "N->O [label=1];\n" +
                "N->P [label=0];\n" +
                "O->D [label=1];\n" +
                "O->H [label=0];\n" +
                "P->Q [label=1];\n" +
                "P->C [label=0];\n" +
                "Q->C [label=1];\n" +
                "Q->R [label=0];\n" +
                "R->B [label=1];\n" +
                "R->P [label=0];}", Grammar(Regex("1(1010*|1(010)*1)*0"))
                .toNFA()
                .toDFA()
                .graph)
    }
}
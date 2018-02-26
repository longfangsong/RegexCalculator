package finiteAutomatas

import java.io.Writer

interface HasGraph {
    val graph: String
    fun saveGraph(writer: Writer) {
        writer.write(graph)
    }
}
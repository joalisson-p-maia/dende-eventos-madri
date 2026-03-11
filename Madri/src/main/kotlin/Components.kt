fun readInt(mensagem: String, mensagemErro: String, faixa: IntRange = 0..Int.MAX_VALUE): Int {
    while (true) {
        print(mensagem)
        val entradaUsuario = readln().toIntOrNull()
        if (entradaUsuario != null && entradaUsuario in faixa) {
            return entradaUsuario
        }
        println(mensagemErro)
    }
}

fun readDouble(mensagem: String, mensagemErro: String, valorMinimo: Double = 0.0, valorMaximo: Double = Double.MAX_VALUE): Double {
    while (true) {
        print(mensagem)
        val entradaUsuario = readln().toDoubleOrNull()
        if (entradaUsuario != null && entradaUsuario >= valorMinimo && entradaUsuario <= valorMaximo) {
            return entradaUsuario
        }
        println(mensagemErro)
    }
}

fun readString(mensagem: String, mensagemErro: String, tamanhoMinimo: Int = 0): String {
    while (true) {
        print(mensagem)
        val entradaUsuario = readln()
        if (entradaUsuario.length >= tamanhoMinimo) {
            return entradaUsuario
        }
        println(mensagemErro)
    }
}

fun printTable(cabecalho: String, itens: List<Any>) {
    println(cabecalho)
    itens.forEach { println(it) }
}
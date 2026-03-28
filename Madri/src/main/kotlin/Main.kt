import java.io.PrintStream

fun main() {
    System.setOut(PrintStream(System.`out`, true, "UTF-8"))

    var usuarioLogado: Usuario? = null
    var opcao: String = ""

    do {
        if (usuarioLogado == null) {
            println("\n--- DENDÊ EVENTOS ---")
            println("====================")
            println("1. Login / Reativar \n2. Cadastro Comum \n3. Cadastro Org \n0. Sair")
            println("====================")
            print("Opção: ")
            opcao = readln()

            when (opcao) {
                "1" -> usuarioLogado = reativarUsuario()
                "2" -> cadastrarUsuarioComum()
                "3" -> cadastrarUsuarioOrganizador()
                "0" -> print("--- ENCERRANDO SISTEMA ---")
                else -> println("Opção inválida.")
            }

        } else {
            println("\n--- MENU (${usuarioLogado.usuarioNome}) ---")
            println("====================")
            println("1. Perfil \n2. Alterar Perfil \n3. Inativar \n0. Logout")

            when (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                true -> {
                    println("4. Cadastrar Evento \n5. Listar Meus Eventos \n6. Alterar Evento \n7. Ativar Evento \n8. Desativar Evento")
                    println("====================")
                }

                false -> {
                    println("4. Feed de Eventos \n5. Comprar Ingresso \n6. Listar Meus Ingressos \n7. Cancelar Ingresso")
                    println("====================")
                }
            }

            print("Opção: ")
            when (readln()) {
                "0" -> usuarioLogado = null
                "1" -> visualizarPerfilUsuario(usuarioLogado)
                "2" -> alterarPerfilUsuario(usuarioLogado)
                "3" -> {
                    inativarUsuario(usuarioLogado)
                    if (!usuarioLogado.usuarioAtivo) usuarioLogado = null
                }
                "4" -> if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) cadastrarEvento(usuarioLogado) else feedEventos()
                "5" -> if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) listarEventosOrganizador(usuarioLogado) else comprarIngresso(usuarioLogado)
                "6" -> if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) alterarEvento(usuarioLogado) else listarIngressos(usuarioLogado)
                "7" -> if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) ativarEvento(usuarioLogado) else cancelarIngresso(usuarioLogado)
                "8" -> if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) desativarEvento(usuarioLogado) else println("Opção inválida.")
                else -> println("Opção inválida.")
            }
        }
    } while (opcao != "0")
}
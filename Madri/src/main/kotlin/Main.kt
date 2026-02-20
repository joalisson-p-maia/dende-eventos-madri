import java.io.PrintStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

//data class e enums
enum class TipoUsuario { COMUM, ORGANIZADOR }
enum class Modalidade { PRESENCIAL, REMOTO, HIBRIDO }
enum class TipoEvento {
    SOCIAL,
    CORPORATIVO,
    ACADEMICO,
    SHOW,
    FESTIVAL,
    OUTROS
}

//data class, permitem usar toString, hashcode, equals, por debaixo dos panos, servem para armazenar dados
data class Usuario(
    val usuarioEmail: String,
    var usuarioNome: String,
    var usuarioDataNascimento: LocalDate,
    var usuarioSexo: String,
    var usuarioSenha: String,
    var usuarioTipo: TipoUsuario,
    var usuarioAtivo: Boolean = true,
    var usuarioCnpj: String? = null,
    var usuarioRazaoSocial: String? = null,
    var usuarioNomeFantasia: String? = null
)

data class Evento(
    val eventoId: Int,
    var eventoNome: String,
    var eventoDescricao: String,
    var eventoPagina: String,
    var eventoDataInicio: LocalDateTime,
    var eventoDataFim: LocalDateTime,
    var eventoTipo: TipoEvento,
    var eventoModalidade: Enum<*>,
    var eventoLocal: String,
    var eventoPreco: Double,
    var eventoCapacidadeMax: Int,
    var eventoIngressosVendidos: Int = 0,
    var eventoAtivo: Boolean = false,
    var eventoOrganizadorEmail: String,
    var eventoIdEventoPrincipal: Int? = null,
    var eventoEstorna: Boolean = true,
    var eventoTaxaEstorno: Double = 0.0
)

data class Ingresso(
    val ingressoId: Int,
    val ingressoEventoId: Int,
    val ingressoUsuarioEmail: String,
    var ingressoStatus: String = "ATIVO",
    val ingressoValorPago: Double
)

fun main() {
    System.setOut(PrintStream(System.`out`, true, "UTF-8"))

    val usuariosListaMutavel = mutableListOf<Usuario>()
    val eventosListaMutavel = mutableListOf<Evento>()
    val ingressosListaMutavel = mutableListOf<Ingresso>()
    var usuarioLogado: Usuario? = null

    val formatarData = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val formatarDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    var menuContinuaAberto = true

    while (menuContinuaAberto) {
        if (usuarioLogado == null) {
            println("\n--- DENDÊ EVENTOS ---")
            println("1. Login / Reativar \n2. Cadastro Comum \n3. Cadastro Org \n0. Sair") //US = User History
            print("Opção: ")
            val opcoesMenu = readln()
            if (opcoesMenu == "0") menuContinuaAberto = false

            when (opcoesMenu) {
                "1" -> {
                    //LOGIN & REATIVAR
                    print("Email: "); val emailRespostaMenu = readln()
                    print("Senha: "); val senhaRespostaMenu = readln()

                    val usuarioLista = usuariosListaMutavel.find { it.usuarioEmail == emailRespostaMenu && it.usuarioSenha == senhaRespostaMenu }
                    if (usuarioLista != null) {
                        if (!usuarioLista.usuarioAtivo) {
                            print("Conta inativa. Reativar? (S/N): ")
                            if (readln().uppercase() == "S") {
                                usuarioLista.usuarioAtivo = true
                                usuarioLogado = usuarioLista
                                println("Reativada!")
                            }
                        } else {
                            usuarioLogado = usuarioLista
                            println("Olá, ${usuarioLista.usuarioNome}!")
                        }
                    } else println("Erro: Credenciais inválidas.")
                }
                "2", "3" -> {
                    //CADASTROS
                    print("Digite seu Email: ")
                    val emailRespostaMenu = readln()

                    //Ele verifica em qualquer usuário se tem o email que vem do menu
                    if (usuariosListaMutavel.any { it.usuarioEmail == emailRespostaMenu }) println("Erro: Email já cadastrado.")
                    else {
                        print("Digite seu Nome: ")
                        val nomeRespostaMenu = readln()

                        print("Digite sua Data de Nascimento no formato (ddMMyyyy): ")
                        val dataNascimentoRespostaMenu = readln() //TODO:se colocar 06102003 não vai
                        var dataNascimentoFormatada: LocalDate? = null

                        if (dataNascimentoRespostaMenu.length == 8) {
                            try {
                                // Converte o texto "06102003" em um objeto de data real
                                dataNascimentoFormatada =
                                    LocalDate.parse(dataNascimentoRespostaMenu, DateTimeFormatter.ofPattern("ddMMyyyy"))
                            } catch (e: Exception) {
                                println("Erro: Data inválida.")
                            }
                        } else {
                            println("Erro: Use o formato de 8 dígitos (Ex: 06102003).")
                        }

                        if (dataNascimentoFormatada != null) {
                            print("Digite seu Sexo: ")
                            val sexoRespostaMenu = readln()

                            print("Digite sua Senha: ")
                            val senhaRespostaMenu = readln()

                            val tipoRespostaMenu = if (opcoesMenu == "3") TipoUsuario.ORGANIZADOR else TipoUsuario.COMUM
                            val novoUsuario = Usuario(
                                emailRespostaMenu,
                                nomeRespostaMenu,
                                dataNascimentoFormatada,
                                sexoRespostaMenu,
                                senhaRespostaMenu,
                                tipoRespostaMenu
                            )
                            if (tipoRespostaMenu == TipoUsuario.ORGANIZADOR) {
                                print("Informar empresa? (S/N): ")
                                if (readln().uppercase() == "S") {
                                    print("CNPJ: ")
                                    novoUsuario.usuarioCnpj = readln()
                                    print("Razão Social: ")
                                    novoUsuario.usuarioRazaoSocial = readln()
                                    print("Nome Fantasia: ")
                                    novoUsuario.usuarioNomeFantasia = readln()
                                }
                            }
                            usuariosListaMutavel.add(novoUsuario); println("Cadastrado com sucesso!")
                        }else{
                            println("Cadastro cancelado por erro na data")
                        }
                    }
                }
            }
        } else {
            //MENU LOGADO
            println("\n--- MENU (${usuarioLogado.usuarioNome}) ---")
            println("1. Perfil \n2. Alterar Perfil \n3. Inativar \n0. Logout")

            if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                println("4. Criar Evento \n5. Listar Meus Eventos \n6. Alterar Evento \n7. Ativar/Desativar ")
            } else {
                println("4. Feed \n5. Comprar \n6. Meus Ingressos \n7. Cancelar Ingresso ")
            }

            print("Opção: ")
            when (readln()) {
                "0" -> usuarioLogado = null
                "1" -> {
                    //VISUALIZAR PERFIL
                    val verificaIdadeAtualUsuario = Period.between(usuarioLogado.usuarioDataNascimento, LocalDate.now())
                    println("Nome: ${usuarioLogado.usuarioNome} | Email: ${usuarioLogado.usuarioEmail}")
                    println("Idade: ${verificaIdadeAtualUsuario.years} anos, ${verificaIdadeAtualUsuario.months} meses, ${verificaIdadeAtualUsuario.days} dias")
                    usuarioLogado.usuarioCnpj?.let {
                        println("Empresa: $it - ${usuarioLogado.usuarioNomeFantasia}")
                    }
                }

                "2" -> {
                    //ALTERAR PERFIL
                    println("Deixe em branco para não alterar.")
                    print("Novo nome: ")

                    usuarioLogado.usuarioNome =
                        readln().takeIf {
                            it.isNotBlank()
                        }?.let {
                            usuarioLogado.usuarioNome = it
                        }.toString()

                    print("Nova senha: ")
                    usuarioLogado.usuarioSenha = readln().takeIf {
                        it.isNotBlank()
                    }?.let {
                        usuarioLogado.usuarioSenha = it
                    }.toString()

                    print("Novo sexo: ")
                    usuarioLogado.usuarioSexo = readln().takeIf {
                        it.isNotBlank()
                    }?.let {
                        usuarioLogado.usuarioSexo = it
                    }.toString()

                    print("Nova Data de Nascimento no formato (DD/MM/YYYY): ")
                    usuarioLogado.usuarioDataNascimento = LocalDate.parse(readln(), formatarData)

                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                        print("Novo Nome Fantasia: "); readln().takeIf {
                            it.isNotBlank()
                        }?.let {
                            usuarioLogado.usuarioNomeFantasia = it
                        }
                    }

                    println("Dados atualizados!")
                }

                "3" -> {
                    //INATIVAR
                    val temEventoAtivo = eventosListaMutavel.any {
                        it.eventoOrganizadorEmail == usuarioLogado.usuarioEmail && it.eventoAtivo && it.eventoDataFim.isAfter(
                            LocalDateTime.now()
                        )
                    }
                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR && temEventoAtivo) println("Erro: Você possui eventos ativos.")
                    else {
                        usuarioLogado.usuarioAtivo = false
                        usuarioLogado = null
                        println("Conta inativada.")
                    }
                }
            }
        }
    }
}

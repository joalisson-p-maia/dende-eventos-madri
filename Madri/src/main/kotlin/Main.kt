import java.io.PrintStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.String

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
    var proximoIdEvento = 1
    var proximoIdIngresso = 1
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
                        val dataNascimentoRespostaMenu = readln()
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

                    print("Novo nome (atual: ${usuarioLogado.usuarioNome}): ")
                    readln().takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioNome = it }

                    print("Nova senha: ")
                    readln().takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioSenha = it }

                    print("Novo sexo (atual: ${usuarioLogado.usuarioSexo}): ")
                    readln().takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioSexo = it }

                    print("Nova Data de Nascimento no formato (DD/MM/YYYY) (atual: ${usuarioLogado.usuarioDataNascimento.format(formatarData)}): ")
                    readln().takeIf { it.isNotBlank() }?.let {
                        try {
                            usuarioLogado.usuarioDataNascimento = LocalDate.parse(it, formatarData)
                        } catch (e: Exception) {
                            println("Erro: Data inválida. Mantida a data anterior.")
                        }
                    }

                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                        print("Novo Nome Fantasia (atual: ${usuarioLogado.usuarioNomeFantasia ?: "não informado"}): ")
                        readln().takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioNomeFantasia = it }
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

                "4" -> {
                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                        //CRIAR EVENTO
                        val formatarDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

                        print("Página do evento: ")
                        val paginaEvento = readln()

                        print("Nome do evento: ")
                        val nomeEvento = readln()

                        print("Descrição: ")
                        val descricaoEvento = readln()

                        print("Data e hora de início (DD/MM/YYYY HH:mm): ")
                        val dataInicioEvento = LocalDateTime.parse(readln(), formatarDataHora)

                        if (dataInicioEvento.isBefore(LocalDateTime.now())) {
                            println("Erro: A data de início não pode ser anterior à data atual.")
                        } else {
                            print("Data e hora de fim (DD/MM/YYYY HH:mm): ")
                            val dataFimEvento = LocalDateTime.parse(readln(), formatarDataHora)

                            when {
                                dataFimEvento.isBefore(LocalDateTime.now()) ->
                                    println("Erro: A data de fim não pode ser anterior à data atual.")
                                dataFimEvento.isBefore(dataInicioEvento) ->
                                    println("Erro: A data de fim não pode ser anterior à data de início.")
                                java.time.Duration.between(dataInicioEvento, dataFimEvento).toMinutes() < 30 ->
                                    println("Erro: O evento deve ter no mínimo 30 minutos de duração.")
                                else -> {
                                    println("Tipos de evento disponíveis: ${TipoEvento.entries.joinToString()}")
                                    print("Tipo do evento: ")
                                    val tipoEvento = TipoEvento.valueOf(readln().uppercase())

                                    print("ID do evento principal (deixe em branco se não houver): ")
                                    val eventoPrincipalId = readln().takeIf { it.isNotBlank() }?.toIntOrNull()

                                    if (eventoPrincipalId != null && eventosListaMutavel.none { it.eventoId == eventoPrincipalId }) {
                                        println("Erro: Evento principal não encontrado.")
                                    } else {
                                        println("Modalidades: ${Modalidade.entries.joinToString()}")
                                        print("Modalidade: ")
                                        val modalidadeEvento = Modalidade.valueOf(readln().uppercase())

                                        print("Capacidade máxima: ")
                                        val capacidadeEvento = readln().toInt()

                                        print("Local (endereço ou link): ")
                                        val localEvento = readln()

                                        print("Preço do ingresso: ")
                                        val precoEvento = readln().toDouble()

                                        print("Estorna em caso de cancelamento? (S/N): ")
                                        val estornaEvento = readln().uppercase() == "S"

                                        val taxaEstorno = if (estornaEvento) {
                                            print("Taxa de estorno (%): ")
                                            readln().toDouble()
                                        } else 0.0

                                        print("Evento ativo? (S/N): ")
                                        val ativoEvento = readln().uppercase() == "S"

                                        val novoEvento = Evento(
                                            eventoId = proximoIdEvento++,
                                            eventoNome = nomeEvento,
                                            eventoDescricao = descricaoEvento,
                                            eventoPagina = paginaEvento,
                                            eventoDataInicio = dataInicioEvento,
                                            eventoDataFim = dataFimEvento,
                                            eventoTipo = tipoEvento,
                                            eventoModalidade = modalidadeEvento,
                                            eventoLocal = localEvento,
                                            eventoPreco = precoEvento,
                                            eventoCapacidadeMax = capacidadeEvento,
                                            eventoIngressosVendidos = 0,
                                            eventoAtivo = ativoEvento,
                                            eventoOrganizadorEmail = usuarioLogado.usuarioEmail,
                                            eventoIdEventoPrincipal = eventoPrincipalId,
                                            eventoEstorna = estornaEvento,
                                            eventoTaxaEstorno = taxaEstorno
                                        )
                                        eventosListaMutavel.add(novoEvento)
                                        println("Evento '${novoEvento.eventoNome}' cadastrado com sucesso! ID: ${novoEvento.eventoId}")
                                    }
                                }
                            }
                        }

                    } else {
                        //FEED DE EVENTOS
                        val dataAtual = LocalDateTime.now()

                        val feed = eventosListaMutavel
                            .filter { evento ->
                                evento.eventoAtivo &&
                                        evento.eventoDataFim.isAfter(dataAtual) &&
                                        ingressosListaMutavel.count {
                                            it.ingressoEventoId == evento.eventoId && !"CANCELADO".equals(it.ingressoStatus)
                                        } < evento.eventoCapacidadeMax
                            }
                            .sortedWith(compareBy({ it.eventoDataInicio }, { it.eventoNome }))

                        if (feed.isEmpty()) {
                            println("Nenhum evento disponível no momento.")
                        } else {
                            println("\n--- FEED DE EVENTOS ---")
                            feed.forEach { evento ->
                                val ingressosVendidos = ingressosListaMutavel.count {
                                    it.ingressoEventoId == evento.eventoId && !"CANCELADO".equals(it.ingressoStatus)
                                }
                                val vagasRestantes = evento.eventoCapacidadeMax - ingressosVendidos
                                println(
                                    "[ID: ${evento.eventoId}] ${evento.eventoNome} | " +
                                            "${evento.eventoDataInicio.format(formatarDataHora)} até ${evento.eventoDataFim.format(formatarDataHora)} | " +
                                            "Local: ${evento.eventoLocal} | Preço: R${"%.2f".format(evento.eventoPreco)} | " +
                                            "Vagas: $vagasRestantes/${evento.eventoCapacidadeMax} | Tipo: ${evento.eventoTipo}"
                                )
                            }
                        }
                    }
                }

                "5" -> {
                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                        //LISTAR MEUS EVENTOS
                        val meusEventos = eventosListaMutavel.filter {
                            it.eventoOrganizadorEmail == usuarioLogado.usuarioEmail
                        }

                        if (meusEventos.isEmpty()) {
                            println("Você não possui eventos cadastrados.")
                        } else {
                            println("\n--- MEUS EVENTOS ---")
                            meusEventos.forEach { evento ->
                                println(
                                    "[ID: ${evento.eventoId}] ${evento.eventoNome} | " +
                                            "${evento.eventoDataInicio.format(formatarDataHora)} até ${
                                                evento.eventoDataFim.format(
                                                    formatarDataHora
                                                )
                                            } | " +
                                            "Local: ${evento.eventoLocal} | Preço: R${"%.2f".format(evento.eventoPreco)} | " +
                                            "Capacidade: ${evento.eventoCapacidadeMax} | Ativo: ${if (evento.eventoAtivo) "Sim" else "Não"}"
                                )
                            }
                        }

                    } else {
                        //COMPRAR INGRESSO
                        val dataAtual = LocalDateTime.now()

                        val feedDisponivel = eventosListaMutavel.filter { evento ->
                            evento.eventoAtivo &&
                                    evento.eventoDataFim.isAfter(dataAtual) &&
                                    ingressosListaMutavel.count {
                                        it.ingressoEventoId == evento.eventoId && !"CANCELADO".equals(it.ingressoStatus)
                                    } < evento.eventoCapacidadeMax
                        }

                        if (feedDisponivel.isEmpty()) {
                            println("Nenhum evento disponível para compra.")
                        } else {
                            feedDisponivel.forEach { evento ->
                                println("[ID: ${evento.eventoId}] ${evento.eventoNome} - R${"%.2f".format(evento.eventoPreco)}")
                            }

                            print("Informe o ID do evento desejado: ")
                            val idDesejado = readln().toIntOrNull()
                            val eventoDesejado = feedDisponivel.find { it.eventoId == idDesejado }

                            if (eventoDesejado == null) {
                                println("Erro: Evento não encontrado ou indisponível.")
                            } else {
                                var totalAPagar = eventoDesejado.eventoPreco
                                val eventosParaComprar = mutableListOf(eventoDesejado)

                                // Verifica se há evento principal vinculado
                                eventoDesejado.eventoIdEventoPrincipal?.let { principalId ->
                                    val eventoPrincipal = eventosListaMutavel.find { it.eventoId == principalId }
                                    if (eventoPrincipal != null) {
                                        totalAPagar += eventoPrincipal.eventoPreco
                                        eventosParaComprar.add(eventoPrincipal)
                                        println(
                                            "Este evento está vinculado ao evento principal: '${eventoPrincipal.eventoNome}' (R${
                                                "%.2f".format(
                                                    eventoPrincipal.eventoPreco
                                                )
                                            })"
                                        )
                                    }
                                }

                                println("Valor total a pagar: R${"%.2f".format(totalAPagar)}")
                                print("Confirmar compra? (S/N): ")

                                if (readln().uppercase() == "S") {
                                    eventosParaComprar.forEach { evento ->
                                        val novoIngresso = Ingresso(
                                            ingressoId = proximoIdIngresso++,
                                            ingressoEventoId = evento.eventoId,
                                            ingressoUsuarioEmail = usuarioLogado.usuarioEmail,
                                            ingressoValorPago = evento.eventoPreco
                                        )
                                        ingressosListaMutavel.add(novoIngresso)
                                        println("Ingresso #${novoIngresso.ingressoId} gerado para '${evento.eventoNome}'")
                                    }
                                    println("Compra realizada! Total pago: R${"%.2f".format(totalAPagar)}")
                                } else {
                                    println("Compra cancelada.")
                                }
                            }
                        }
                    }
                }

                "6" -> {
                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                        //ALTERAR EVENTO
                        val meusEventosAtivos = eventosListaMutavel.filter {
                            it.eventoOrganizadorEmail == usuarioLogado.usuarioEmail && it.eventoAtivo
                        }

                        if (meusEventosAtivos.isEmpty()) {
                            println("Você não possui eventos ativos para alterar.")
                        } else {
                            meusEventosAtivos.forEach { println("[ID: ${it.eventoId}] ${it.eventoNome}") }
                            print("ID do evento a alterar: ")
                            val idAlterar = readln().toIntOrNull()
                            val eventoAlterar = meusEventosAtivos.find { it.eventoId == idAlterar }

                            if (eventoAlterar == null) {
                                println("Erro: Evento não encontrado.")
                            } else {
                                println("Deixe em branco para não alterar.")

                                print("Nova página (atual: ${eventoAlterar.eventoPagina}): ")
                                readln().takeIf { it.isNotBlank() }?.let { eventoAlterar.eventoPagina = it }

                                print("Novo nome (atual: ${eventoAlterar.eventoNome}): ")
                                readln().takeIf { it.isNotBlank() }?.let { eventoAlterar.eventoNome = it }

                                print("Nova descrição (atual: ${eventoAlterar.eventoDescricao}): ")
                                readln().takeIf { it.isNotBlank() }?.let { eventoAlterar.eventoDescricao = it }

                                print("Nova data/hora de início (atual: ${eventoAlterar.eventoDataInicio.format(formatarDataHora)}): ")
                                val novaDataInicio = readln().takeIf { it.isNotBlank() }
                                    ?.let { LocalDateTime.parse(it, formatarDataHora) }

                                print("Nova data/hora de fim (atual: ${eventoAlterar.eventoDataFim.format(formatarDataHora)}): ")
                                val novaDataFim = readln().takeIf { it.isNotBlank() }
                                    ?.let { LocalDateTime.parse(it, formatarDataHora) }

                                val dataInicioFinal = novaDataInicio ?: eventoAlterar.eventoDataInicio
                                val dataFimFinal = novaDataFim ?: eventoAlterar.eventoDataFim

                                when {
                                    dataInicioFinal.isBefore(LocalDateTime.now()) ->
                                        println("Erro: Data de início não pode ser anterior à data atual.")
                                    dataFimFinal.isBefore(dataInicioFinal) ->
                                        println("Erro: Data de fim não pode ser anterior à data de início.")
                                    dataFimFinal.isBefore(LocalDateTime.now()) ->
                                        println("Erro: Data de fim não pode ser anterior à data atual.")
                                    java.time.Duration.between(dataInicioFinal, dataFimFinal).toMinutes() < 30 ->
                                        println("Erro: O evento deve ter no mínimo 30 minutos de duração.")
                                    else -> {
                                        eventoAlterar.eventoDataInicio = dataInicioFinal
                                        eventoAlterar.eventoDataFim = dataFimFinal

                                        println("Tipos: ${TipoEvento.entries.joinToString()}")
                                        print("Novo tipo (atual: ${eventoAlterar.eventoTipo}): ")
                                        readln().takeIf { it.isNotBlank() }
                                            ?.let { eventoAlterar.eventoTipo = TipoEvento.valueOf(it.uppercase()) }

                                        print("Novo ID de evento principal (atual: ${eventoAlterar.eventoIdEventoPrincipal ?: "nenhum"}): ")
                                        readln().takeIf { it.isNotBlank() }
                                            ?.let { eventoAlterar.eventoIdEventoPrincipal = it.toIntOrNull() }

                                        println("Modalidades: ${Modalidade.entries.joinToString()}")
                                        print("Nova modalidade (atual: ${eventoAlterar.eventoModalidade}): ")
                                        readln().takeIf { it.isNotBlank() }
                                            ?.let { eventoAlterar.eventoModalidade = Modalidade.valueOf(it.uppercase()) }

                                        print("Nova capacidade máxima (atual: ${eventoAlterar.eventoCapacidadeMax}): ")
                                        readln().takeIf { it.isNotBlank() }
                                            ?.let { eventoAlterar.eventoCapacidadeMax = it.toInt() }

                                        print("Novo local (atual: ${eventoAlterar.eventoLocal}): ")
                                        readln().takeIf { it.isNotBlank() }?.let { eventoAlterar.eventoLocal = it }

                                        print("Novo preço do ingresso (atual: R${"%.2f".format(eventoAlterar.eventoPreco)}): ")
                                        readln().takeIf { it.isNotBlank() }
                                            ?.let { eventoAlterar.eventoPreco = it.toDouble() }

                                        print("Estorna cancelamento? S/N (atual: ${if (eventoAlterar.eventoEstorna) "S" else "N"}): ")
                                        readln().takeIf { it.isNotBlank() }
                                            ?.let { eventoAlterar.eventoEstorna = it.uppercase() == "S" }

                                        if (eventoAlterar.eventoEstorna) {
                                            print("Nova taxa de estorno % (atual: ${eventoAlterar.eventoTaxaEstorno}): ")
                                            readln().takeIf { it.isNotBlank() }
                                                ?.let { eventoAlterar.eventoTaxaEstorno = it.toDouble() }
                                        }

                                        println("Evento atualizado com sucesso!")
                                    }
                                }
                            }
                        }

                    } else {
                        //MEUS INGRESSOS
                        val agora = LocalDateTime.now()

                        val meusIngressos = ingressosListaMutavel
                            .filter { it.ingressoUsuarioEmail == usuarioLogado.usuarioEmail }
                            .sortedWith(compareBy(
                                // Filtrando por ativos/futuros primeiro; cancelados/finalizados por último
                                { ingresso ->
                                    val evento = eventosListaMutavel.find { it.eventoId == ingresso.ingressoEventoId }
                                    val finalizado = evento == null || !evento.eventoAtivo || evento.eventoDataFim.isBefore(agora)
                                    if ("CANCELADO".equals(ingresso.ingressoStatus) || finalizado) 1 else 0
                                },
                                { ingresso ->
                                    eventosListaMutavel.find { it.eventoId == ingresso.ingressoEventoId }?.eventoDataInicio
                                        ?: LocalDateTime.MAX
                                },
                                { ingresso ->
                                    eventosListaMutavel.find { it.eventoId == ingresso.ingressoEventoId }?.eventoNome ?: ""
                                }
                            ))

                        if (meusIngressos.isEmpty()) {
                            println("Você não possui ingressos.")
                        } else {
                            println("\n--- MEUS INGRESSOS ---")
                            meusIngressos.forEach { ingresso ->
                                val evento = eventosListaMutavel.find { it.eventoId == ingresso.ingressoEventoId }
                                val statusIngresso = if ("CANCELADO".equals(ingresso.ingressoStatus)) "CANCELADO" else "ATIVO"
                                val nomeEvento = evento?.eventoNome ?: "Evento removido"
                                val dataEvento = evento?.eventoDataInicio?.format(formatarDataHora) ?: "-"
                                println(
                                    "[Ingresso #${ingresso.ingressoId}] $nomeEvento | " +
                                            "Início: $dataEvento | Pago: R${"%.2f".format(ingresso.ingressoValorPago)} | " +
                                            "Status: $statusIngresso"
                                )
                            }
                        }
                    }
                }

                "7" -> {
                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                        //ATIVAR / DESATIVAR EVENTO
                        val meusEventos = eventosListaMutavel.filter {
                            it.eventoOrganizadorEmail == usuarioLogado.usuarioEmail
                        }

                        if (meusEventos.isEmpty()) {
                            println("Você não possui eventos cadastrados.")
                        } else {
                            meusEventos.forEach {
                                println("[ID: ${it.eventoId}] ${it.eventoNome} | ${if (it.eventoAtivo) "ATIVO" else "INATIVO"}")
                            }

                            print("ID do evento para ativar/desativar: ")
                            val idToggle = readln().toIntOrNull()
                            val eventoToggle = meusEventos.find { it.eventoId == idToggle }

                            if (eventoToggle == null) {
                                println("Erro: Evento não encontrado.")
                            } else {
                                if (eventoToggle.eventoAtivo) {
                                    // Desativar: cancela todos os ingressos ativos e estorna
                                    val ingressosDoEvento = ingressosListaMutavel.filter {
                                        it.ingressoEventoId == eventoToggle.eventoId && !"CANCELADO".equals(it.ingressoStatus)
                                    }

                                    ingressosDoEvento.forEach { ingresso ->
                                        ingresso.ingressoStatus = "CANCELADO"
                                        if (eventoToggle.eventoEstorna) {
                                            val valorEstorno =
                                                ingresso.ingressoValorPago * (1 - eventoToggle.eventoTaxaEstorno / 100)
                                            println(
                                                "Ingresso #${ingresso.ingressoId} cancelado. " +
                                                        "Estorno de R${"%.2f".format(valorEstorno)} para ${ingresso.ingressoUsuarioEmail}"
                                            )
                                        } else {
                                            println("Ingresso #${ingresso.ingressoId} cancelado sem estorno.")
                                        }
                                    }

                                    eventoToggle.eventoAtivo = false
                                    println("Evento '${eventoToggle.eventoNome}' desativado. ${ingressosDoEvento.size} ingresso(s) cancelado(s).")
                                } else {
                                    // Ativar
                                    eventoToggle.eventoAtivo = true
                                    println("Evento '${eventoToggle.eventoNome}' ativado com sucesso!")
                                }
                            }
                        }

                    } else {
                        //CANCELAR INGRESSO
                        val meusIngressosAtivos = ingressosListaMutavel.filter {
                            it.ingressoUsuarioEmail == usuarioLogado.usuarioEmail && !"CANCELADO".equals(it.ingressoStatus)
                        }

                        if (meusIngressosAtivos.isEmpty()) {
                            println("Você não possui ingressos ativos para cancelar.")
                        } else {
                            meusIngressosAtivos.forEach { ingresso ->
                                val nomeEvento =
                                    eventosListaMutavel.find { it.eventoId == ingresso.ingressoEventoId }?.eventoNome
                                        ?: "Evento removido"
                                println(
                                    "[Ingresso #${ingresso.ingressoId}] $nomeEvento - Pago: R${
                                        "%.2f".format(
                                            ingresso.ingressoValorPago
                                        )
                                    }"
                                )
                            }

                            print("ID do ingresso a cancelar: ")
                            val idCancelar = readln().toIntOrNull()
                            val ingressoCancelar = meusIngressosAtivos.find { it.ingressoId == idCancelar }

                            if (ingressoCancelar == null) {
                                println("Erro: Ingresso não encontrado.")
                            } else {
                                val eventoDoIngresso =
                                    eventosListaMutavel.find { it.eventoId == ingressoCancelar.ingressoEventoId }

                                ingressoCancelar.ingressoStatus = "CANCELADO"

                                if (eventoDoIngresso != null && eventoDoIngresso.eventoEstorna) {
                                    val valorEstorno =
                                        ingressoCancelar.ingressoValorPago * (1 - eventoDoIngresso.eventoTaxaEstorno / 100)
                                    println(
                                        "Ingresso #${ingressoCancelar.ingressoId} cancelado. " +
                                                "Estorno de R${"%.2f".format(valorEstorno)} aplicado."
                                    )
                                } else {
                                    println("Ingresso #${ingressoCancelar.ingressoId} cancelado sem estorno.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
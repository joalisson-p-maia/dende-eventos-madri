import java.io.PrintStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.String

//data class e enums
enum class TipoUsuario { COMUM, ORGANIZADOR } // boa estratégia de enum para diferenciar tipos de usuários mapear
enum class Modalidade { PRESENCIAL, REMOTO, HIBRIDO }
enum class TipoEvento { // a gente tem muito mais tipos de eventos
    SOCIAL,
    CORPORATIVO,
    ACADEMICO,
    SHOW,
    FESTIVAL,
    OUTROS
}
// talvez fosse legal fazer um enum para o status do ingresso também, para evitar que o usuário digite algo diferente
// de "ATIVO" ou "CANCELADO", por exemplo, o que poderia causar um erro na hora de verificar a disponibilidade
// do evento ou na hora de cancelar o ingresso


//data class, permitem usar toString, hashcode, equals, por debaixo dos panos, servem para armazenar dados
data class Usuario(
    //email
    val usuarioEmail: String, // todos esses campos são do usuário, não faz sentido replicar o nome usuário
    var usuarioNome: String, // nome
    var usuarioDataNascimento: LocalDate,
    var usuarioSexo: String,
    var usuarioSenha: String,
    var usuarioTipo: TipoUsuario,
    var usuarioAtivo: Boolean = true,
    var usuarioCnpj: String? = null,// se vocês achassem legal, vocês poderia ter criado um data class Empresa para armazenar os dados da empresa, e aí o usuário organizador teria um campo do tipo Empresa,
    // ao invés de ter esses campos soltos dentro do usuário, o que deixaria o código mais organizado
    // aqui teria um campo do tipo Empresa? chamado empresa, e aí dentro do cadastro do organizador,
    // se ele informar que tem empresa, você cria um objeto Empresa com os dados e atribui para esse campo, ao invés de ter esses campos soltos dentro do usuário
    var usuarioRazaoSocial: String? = null,
    var usuarioNomeFantasia: String? = null
)

// a mesma coisa aqui, todos os campos tem a palavra evento, não faz sentido repetir isso,
// o nome da classe já deixa claro que são dados do evento, então podemos simplificar os nomes dos campos
data class Evento(
    val eventoId: Int,
    var eventoNome: String,
    var eventoDescricao: String,
    var eventoPagina: String,
    var eventoDataInicio: LocalDateTime,
    var eventoDataFim: LocalDateTime,
    var eventoTipo: TipoEvento,
    var eventoModalidade: Enum<*>,// aqui era para ser do tipo Modalidade
    var eventoLocal: String,
    var eventoPreco: Double,
    var eventoCapacidadeMax: Int,
    var eventoIngressosVendidos: Int = 0,
    var eventoAtivo: Boolean = false,
    var eventoOrganizadorEmail: String, // aqui era para ser um Usuario, pois o evento tem um usuário organizador
    var eventoIdEventoPrincipal: Int? = null, // aqui era para ser do tipo Evento?, pois o evento pode ter um evento principal vinculado
    var eventoEstorna: Boolean = true,
    var eventoTaxaEstorno: Double = 0.0
)

// mesmo caso dos outros, todos os campos tem a palavra ingresso, não faz sentido repetir isso
data class Ingresso(
    val ingressoId: Int,
    val ingressoEventoId: Int,// aqui era para ser do tipo Evento, pois o ingresso tem um evento vinculado
    val ingressoUsuarioEmail: String, // aqui era para ser do tipo Usuario, pois o ingresso tem um usuário comprador
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

    // isso não é uma boa prática de programação, pois você está fazendo loop infinito
    // qual a diferença de usar isso ou um while(true)?
    // o ideal seria o laço ser controlado pela condição de saída (opcoesMenu == "0") e não por uma variável booleana
    // que pode ser esquecida de atualizar, ou seja, o risco de criar um loop infinito acidentalmente é maior
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

                    // isso poderia ser reescrito para:
                    // if (usuarioLista?.usuarioAtivo == false) {
                    //     println("Conta inativa. Reativar? (S/N): ")
                    // o operador ?. já verifica se usuarioLista é nulo antes de acessar a propriedade usuarioAtivo,
                    // evitando a necessidade de um if separado para verificar se usuarioLista é nulo
                    if (usuarioLista != null) {
                        if (!usuarioLista.usuarioAtivo) {
                            print("Conta inativa. Reativar? (S/N): ")
                            //o operador uppercase() é uma boa prática para garantir que a comparação seja case-insensitive,
                            // ou seja, tanto "s" quanto "S" serão aceitos para reativar a conta
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
                    // boa, bom uso do any para verificar se já existe um email cadastrado, evitando a necessidade de criar uma variável booleana ou um loop para fazer essa verificação manualmente
                    if (usuariosListaMutavel.any { it.usuarioEmail == emailRespostaMenu }) println("Erro: Email já cadastrado.")
                    else {
                        print("Digite seu Nome: ")
                        val nomeRespostaMenu = readln()

                        // aqui talvez fosse legal usar o do while para continuar pedindo a data de nascimento até que
                        // o usuário digite uma data válida, ao invés de cancelar o cadastro na primeira tentativa
                        // tipo:
                        // var dataNascimentoFormatada: LocalDate? = null
                        // do {
                        //     print("Digite sua Data de Nascimento no formato (ddMMyyyy): ")
                        //     val dataNascimentoRespostaMenu = readln()
                        //     if (dataNascimentoRespostaMenu.length == 8) {
                        //         try {
                        //             dataNascimentoFormatada = LocalDate.parse(dataNascimentoRespostaMenu, DateTimeFormatter.ofPattern("ddMMyyyy"))
                        //         } catch (e: Exception) {
                        //             println("Erro: Data inválida. Tente novamente.")
                        //         }
                        //     } else {
                        //         println("Erro: Use o formato de 8 dígitos (Ex: 06102003). Tente novamente.")
                        //     }
                        // } while (dataNascimentoFormatada == null)
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

                            // não tem um confirmar senhar?
                            // seria interessante adicionar um campo para confirmar a senha,
                            // garantindo que o usuário não cometa um erro de digitação e acabe criando uma conta com
                            // uma senha diferente da que ele pretendia

                            // acho que isso poderia ficar no início do cadastro, antes de pedir os outros dados
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

                                // se informar qualquer outra coisa que não seja "S", ele vai entender
                                // que é um "N" e não vai pedir os dados da empresa, talvez fosse interessante validar
                                // a resposta para aceitar apenas "S" ou "N", evitando confusões
                                if (readln().uppercase() == "S") {
                                    print("CNPJ: ")
                                    novoUsuario.usuarioCnpj = readln() //não valida se tá vazio, formato, só pega o que o usuário digitar
                                    // você poderia ter usado o takeIf aqui também, para evitar a necessidade de um if separado
                                    // para verificar se o usuário digitou algo ou não, deixando o código mais limpo e direto
                                    // e validando o campo para não aceitar um CNPJ vazio, por exemplo:
                                    // readln().takeIf { it.isNotBlank() &&  it.length == 14 }?.let { novoUsuario.usuarioCnpj = it }
                                    print("Razão Social: ")
                                    novoUsuario.usuarioRazaoSocial = readln() //não valida se tá vazio, formato, só pega o que o usuário digitar
                                    print("Nome Fantasia: ")
                                    novoUsuario.usuarioNomeFantasia = readln() //não valida se tá vazio, formato, só pega o que o usuário digitar
                                }

                            }
                            usuariosListaMutavel.add(novoUsuario);
                            println("Cadastrado com sucesso!")
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

            // eu acho que aqui você poderia ter dividido tudo:
                // tudo que é de Organizador fica dentro desse if
                // tudo que é do Usuário Comum fica dentro do else, e aí cada um tem seu próprio menu,
                // ao invés de misturar as opções e ficar verificando o tipo do usuário a cada opção
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
                    // boa, pegando o período fica mais fácil de colocar a data no padrão desejado
                    // o nome poderia ser:
                    // val idadeUsuario = Period.between(usuarioLogado.usuarioDataNascimento, LocalDate.now())
                    val verificaIdadeAtualUsuario = Period.between(usuarioLogado.usuarioDataNascimento, LocalDate.now())
                    println("Nome: ${usuarioLogado.usuarioNome} | Email: ${usuarioLogado.usuarioEmail}")
                    println("Idade: ${verificaIdadeAtualUsuario.years} anos, ${verificaIdadeAtualUsuario.months} meses e ${verificaIdadeAtualUsuario.days} dias")
                    // se tudo ficasse dentro do if de organizador e do else de usuário comum, não precisaria ficar verificando
                    // o tipo do usuário a cada opção, o que deixaria o código mais limpo e fácil de entender
                    usuarioLogado.usuarioCnpj?.let {
                        println("Empresa: $it - ${usuarioLogado.usuarioNomeFantasia}")
                    }
                }

                "2" -> {
                    //ALTERAR PERFIL
                    println("Deixe em branco para não alterar.")

                    print("Novo nome (atual: ${usuarioLogado.usuarioNome}): ")
                    //bom uso do takeIf para evitar a necessidade de um if separado para verificar
                    // se o usuário digitou algo ou não, deixando o código mais limpo e direto
                    // era bom verificar se o nome tem um tamanho mínimo, por exemplo, para evitar que o usuário deixe
                    // o nome vazio ou com apenas um caractere, o que não seria muito útil para identificar a pessoa
                    readln().takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioNome = it }

                    print("Nova senha: ")
                    readln().takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioSenha = it }

                    // era bom quando pedir a senha, sempre pedir a confirmação da senha, para evitar que o usuário
                    // cometa um erro de digitação e acabe alterando a senha para algo diferente do que ele pretendia

                    print("Novo sexo (atual: ${usuarioLogado.usuarioSexo}): ")
                    // talvez fosse legal verificar se o usuário digitou um sexo válido, por exemplo, "Masculino",
                    // "Feminino", "Outro", etc, para evitar que ele digite algo que não faça sentido
                    readln().takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioSexo = it }

                    print("Nova Data de Nascimento no formato (DD/MM/YYYY) (atual: ${usuarioLogado.usuarioDataNascimento.format(formatarData)}): ")
                    readln().takeIf { it.isNotBlank() }?.let {
                        try {
                            usuarioLogado.usuarioDataNascimento = LocalDate.parse(it, formatarData)
                        } catch (e: Exception) {
                            // boa solução
                            // mas acho que um do while nas leituras sempre é legal
                            println("Erro: Data inválida. Mantida a data anterior.")
                        }
                    }

                    // reforço minha ponderação sobre colocar tudo do organizador dentro de um if e tudo do usuário comum dentro de um else, para evitar
                    // a necessidade de ficar verificando o tipo do usuário a cada opção, o que deixaria o código mais limpo e fácil de entender
                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                        print("Novo Nome Fantasia (atual: ${usuarioLogado.usuarioNomeFantasia ?: "não informado"}): ")
                        // era bom validar uma quantidade mínima de caracteres
                        readln().takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioNomeFantasia = it }
                    }

                    println("Dados atualizados!")
                }

                "3" -> {
                    //INATIVAR

                    val temEventoAtivo = eventosListaMutavel.any {
                        // aqui deveria ser:
                        // it.organizador.email == usuarioLogado.email && it.ativo && it.dataFim.isAfter(LocalDateTime.now())
                        it.eventoOrganizadorEmail == usuarioLogado.usuarioEmail && it.eventoAtivo && it.eventoDataFim.isAfter(
                            LocalDateTime.now()
                        )
                    }
                    // de novo outro if repetitivo para definir o que é organizador ou usuário comum
                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR && temEventoAtivo) {
                        println("Erro: Você possui eventos ativos.")
                    } else {
                        usuarioLogado.usuarioAtivo = false
                        usuarioLogado = null
                        println("Conta inativada.")
                    }
                }

                "4" -> {

                    // outra vez if repetitivo para definir o que é organizador ou usuário comum,
                    // reforçando a ideia de colocar tudo do organizador dentro de um if e tudo do usuário comum dentro
                    // de um else, para evitar a necessidade de ficar verificando o tipo do usuário a cada opção,
                    // o que deixaria o código mais limpo e fácil de entender
                    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
                        //CRIAR EVENTO
                        val formatarDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

                        print("Página do evento: ")
                        val paginaEvento = readln() // legal se tivesse o takeIf aqui para validar
                        // se o usuário digitou algo ou não, evitando criar um evento com a página vazia, por exemplo

                        print("Nome do evento: ")
                        val nomeEvento = readln()

                        print("Descrição: ")
                        val descricaoEvento = readln()

                        print("Data e hora de início (DD/MM/YYYY HH:mm): ")
                        val dataInicioEvento = LocalDateTime.parse(readln(), formatarDataHora)

                        // boa, validação boa
                        if (dataInicioEvento.isBefore(LocalDateTime.now())) {
                            println("Erro: A data de início não pode ser anterior à data atual.")
                        } else {
                            print("Data e hora de fim (DD/MM/YYYY HH:mm): ")
                            val dataFimEvento = LocalDateTime.parse(readln(), formatarDataHora)

                            // boa, um when sem argumento para definir as regras de validação da data de fim,
                            // deixando o código mais organizado e fácil de entender
                            when {
                                // boa
                                dataFimEvento.isBefore(LocalDateTime.now()) ->
                                    println("Erro: A data de fim não pode ser anterior à data atual.")
                                // boa
                                dataFimEvento.isBefore(dataInicioEvento) ->
                                    println("Erro: A data de fim não pode ser anterior à data de início.")
                                // booooa, muito bom, você poderia usar o period também
                                // Period.between(dataInicioEvento.toLocalDate(), dataFimEvento.toLocalDate()).minutes < 30
                                java.time.Duration.between(dataInicioEvento, dataFimEvento).toMinutes() < 30 ->
                                    println("Erro: O evento deve ter no mínimo 30 minutos de duração.")
                                else -> {
                                    println("Tipos de evento disponíveis: ${TipoEvento.entries.joinToString()}")
                                    print("Tipo do evento: ")
                                    // aqui seria bom validar se o usuário digitou um tipo de evento válido,
                                    // para evitar que ele digite algo que não faça sentido e cause um erro na hora de criar o evento
                                    val tipoEvento = TipoEvento.valueOf(readln().uppercase())

                                    print("ID do evento principal (deixe em branco se não houver): ")
                                    val eventoPrincipalId = readln().takeIf { it.isNotBlank() }?.toIntOrNull()

                                    // aqui você poderia fazer assim:
                                    /*
                                    val eventoPrincipal = readln().takeIf { it.isNotBlank() }?.toIntOrNull()?.let { id ->
                                        eventosListaMutavel.find { it.eventoId == id } ?: run {
                                            println("Erro: Evento principal não encontrado.")
                                            null
                                        }
                                    }**/

                                    if (eventoPrincipalId != null && eventosListaMutavel.none { it.eventoId == eventoPrincipalId }) {
                                        println("Erro: Evento principal não encontrado.")
                                    } else {
                                        println("Modalidades: ${Modalidade.entries.joinToString()}")
                                        print("Modalidade: ")
                                        // sempre bom usar o takeIf e o let para validar a entrada do usuário, evitando a necessidade de
                                        // ifs separados para verificar se ele digitou algo ou não, e se o valor é válido ou não,
                                        // deixando o código mais limpo e direto
                                        // val modalidade = readln().takeIf { it.isNotBlank() }?.let { Modalidade.valueOf(it.uppercase()) }
                                        val modalidadeEvento = Modalidade.valueOf(readln().uppercase())

                                        print("Capacidade máxima: ")
                                        // sempre usar o takeIf
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

                        // muito boa a manipulação das collections
                        val feed = eventosListaMutavel
                            .filter { evento ->
                                evento.eventoAtivo &&
                                        evento.eventoDataFim.isAfter(dataAtual) &&
                                        ingressosListaMutavel.count {
                                            // it.evento = evento
                                            it.ingressoEventoId == evento.eventoId && !"CANCELADO".equals(it.ingressoStatus)
                                        } < evento.eventoCapacidadeMax
                            }
                            .sortedWith(compareBy({ it.eventoDataInicio }, { it.eventoNome }))

                        // o que se segue aqui deve seguir as seguintes orientações:
                        // 1. O if de organizador e usuário comum deve ficar no início do menu,
                        // para evitar a necessidade de ficar verificando o tipo do usuário a cada opção, deixando o código mais limpo e fácil de entender
                        // 2. O takeIf e let são úteis para a maioria das leituras (você pode explorar isso em conjunto com o do..while)
                        // 3. As sugestões de alteração nos data classes impactam diretamente na escrita dos filtros nas collections
                        // 4. Cuidado com o casos de else que não dão possibilidade do usuário retentar
                        // 5. sempre dê preferência ao when sem argumento para organizar as regras de validação, deixando o código mais limpo e fácil de entender


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
import java.time.LocalDateTime

fun cadastrarEvento(usuarioLogado: Usuario) {
    val pagina = readString("Página do evento: ", "Página inválida.", 1)
    val nome = readString("Nome do evento: ", "Nome inválido.", 1)
    val descricao = readString("Descrição: ", "Descrição inválida.", 1)

    val dataInicioStr = readString("Data e hora de início (DD/MM/YYYY HH:mm): ", "Data inválida.", 1)
    val dataInicio = try {
        LocalDateTime.parse(dataInicioStr, formatarDataHora)
    } catch (e: Exception) {
        println("Erro: Formato de data de início inválido.")
        return
    }

    if (dataInicio.isBefore(LocalDateTime.now())) {
        println("Erro: A data de início não pode ser no passado.")
        return
    }

    val dataFimStr = readString("Data e hora de fim (DD/MM/YYYY HH:mm): ", "Data inválida.", 1)
    val dataFim = try {
        LocalDateTime.parse(dataFimStr, formatarDataHora)
    } catch (e: Exception) {
        println("Erro: Formato de data de fim inválido.")
        return
    }

    if (dataFim.isBefore(dataInicio)) {
        println("Erro: A data de término deve ser após a data de início.")
        return
    }

    if (java.time.Duration.between(dataInicio, dataFim).toMinutes() < 30) {
        println("Erro: O evento deve ter no mínimo 30 minutos de duração.")
        return
    }

    println("Tipos de evento disponíveis: ${TipoEvento.entries.joinToString()}")
    val tipo = try {
        TipoEvento.valueOf(readString("Tipo do evento: ", "Tipo inválido.", 1).uppercase())
    } catch (e: Exception) {
        println("Erro: Tipo inválido.")
        return
    }

    val eventoPrincipalIdStr = readString("ID do evento principal (deixe em branco se não houver): ", "")
    val eventoPrincipalId = eventoPrincipalIdStr.takeIf { it.isNotBlank() }?.toIntOrNull()
    if (eventoPrincipalId != null && encontrarEventoPorId(eventoPrincipalId) == null) {
        println("Erro: Evento principal não encontrado.")
        return
    }

    println("Modalidades: ${Modalidade.entries.joinToString()}")
    val modalidade = try {
        Modalidade.valueOf(readString("Modalidade: ", "Modalidade inválida.", 1).uppercase())
    } catch (e: Exception) {
        println("Erro: Modalidade inválida.")
        return
    }

    val capacidade = readInt("Capacidade máxima: ", "Capacidade deve ser no mínimo 1.", 1..Int.MAX_VALUE)
    val local = readString("Local (endereço ou link): ", "Local inválido.", 1)
    val preco = readDouble("Preço do ingresso: ", "O preço não pode ser negativo.", 0.0)

    val estorna = readString("Estorna em caso de cancelamento? (S/N): ", "Resposta inválida.").uppercase() == "S"
    val taxaEstorno = if (estorna) readDouble("Taxa de estorno (%): ", "Taxa inválida (0 a 100).", 0.0, 100.0) else 0.0
    val ativo = readString("Evento ativo? (S/N): ", "Resposta inválida.").uppercase() == "S"

    val novoEvento = Evento(
        eventoId = proximoIdEvento++,
        eventoNome = nome,
        eventoDescricao = descricao,
        eventoPagina = pagina,
        eventoDataInicio = dataInicio,
        eventoDataFim = dataFim,
        eventoTipo = tipo,
        eventoModalidade = modalidade,
        eventoLocal = local,
        eventoPreco = preco,
        eventoCapacidadeMax = capacidade,
        eventoAtivo = ativo,
        eventoOrganizadorEmail = usuarioLogado.usuarioEmail,
        eventoIdEventoPrincipal = eventoPrincipalId,
        eventoEstorna = estorna,
        eventoTaxaEstorno = taxaEstorno
    )
    salvarEvento(novoEvento)
    println("Evento '${novoEvento.eventoNome}' cadastrado com sucesso! ID: ${novoEvento.eventoId}")
}


fun alterarEvento(usuarioLogado: Usuario) {
    val meusEventosAtivos = listarEventosPorOrganizador(usuarioLogado.usuarioEmail).filter { it.eventoAtivo }
    if (meusEventosAtivos.isEmpty()) {
        println("Você não possui eventos ativos para alterar.")
        return
    }

    printTable("Eventos ativos:", meusEventosAtivos.map { "[ID: ${it.eventoId}] ${it.eventoNome}" })

    val idAlterar = readInt("ID do evento a alterar: ", "ID inválido.")
    val evento = meusEventosAtivos.find { it.eventoId == idAlterar }
    if (evento == null) {
        println("Erro: Evento não encontrado.")
        return
    }

    println("Deixe em branco para não alterar.")

    readString("Nova página (atual: ${evento.eventoPagina}): ", "").takeIf { it.isNotBlank() }?.let { evento.eventoPagina = it }
    readString("Novo nome (atual: ${evento.eventoNome}): ", "").takeIf { it.isNotBlank() }?.let { evento.eventoNome = it }
    readString("Nova descrição (atual: ${evento.eventoDescricao}): ", "").takeIf { it.isNotBlank() }?.let { evento.eventoDescricao = it }

    val novaDataInicioStr = readString("Nova data/hora de início (atual: ${evento.eventoDataInicio.format(formatarDataHora)}): ", "")
    val novaDataInicio = novaDataInicioStr.takeIf { it.isNotBlank() }?.let {
        try { LocalDateTime.parse(it, formatarDataHora) } catch (e: Exception) { println("Erro: Data de início inválida. Mantida a anterior."); null }
    }

    val novaDataFimStr = readString("Nova data/hora de fim (atual: ${evento.eventoDataFim.format(formatarDataHora)}): ", "")
    val novaDataFim = novaDataFimStr.takeIf { it.isNotBlank() }?.let {
        try { LocalDateTime.parse(it, formatarDataHora) } catch (e: Exception) { println("Erro: Data de fim inválida. Mantida a anterior."); null }
    }

    val dataInicioFinal = novaDataInicio ?: evento.eventoDataInicio
    val dataFimFinal = novaDataFim ?: evento.eventoDataFim

    if (dataInicioFinal.isBefore(LocalDateTime.now()) && novaDataInicio != null) {
        println("Erro: A nova data de início não pode ser no passado. Alteração de datas cancelada.")
        return
    }
    if (dataFimFinal.isBefore(dataInicioFinal)) {
        println("Erro: A data de fim não pode ser anterior à data de início. Alteração de datas cancelada.")
        return
    }
    if (java.time.Duration.between(dataInicioFinal, dataFimFinal).toMinutes() < 30) {
        println("Erro: O evento deve ter no mínimo 30 minutos de duração. Alteração de datas cancelada.")
        return
    }

    evento.eventoDataInicio = dataInicioFinal
    evento.eventoDataFim = dataFimFinal

    println("Tipos: ${TipoEvento.entries.joinToString()}")
    readString("Novo tipo (atual: ${evento.eventoTipo}): ", "").takeIf { it.isNotBlank() }?.let {
        try { evento.eventoTipo = TipoEvento.valueOf(it.uppercase()) } catch (e: Exception) { println("Erro: Tipo inválido.") }
    }

    readString("Novo ID de evento principal (atual: ${evento.eventoIdEventoPrincipal ?: "nenhum"}): ", "").takeIf { it.isNotBlank() }?.let {
        evento.eventoIdEventoPrincipal = it.toIntOrNull()
    }

    println("Modalidades: ${Modalidade.entries.joinToString()}")
    readString("Nova modalidade (atual: ${evento.eventoModalidade}): ", "").takeIf { it.isNotBlank() }?.let {
        try { evento.eventoModalidade = Modalidade.valueOf(it.uppercase()) } catch (e: Exception) { println("Erro: Modalidade inválida.") }
    }

    readString("Nova capacidade máxima (atual: ${evento.eventoCapacidadeMax}): ", "").takeIf { it.isNotBlank() }?.let {
        val cap = it.toIntOrNull()
        if (cap != null && cap >= 1) evento.eventoCapacidadeMax = cap
        else println("Erro: Capacidade inválida. Mantida a anterior.")
    }

    readString("Novo local (atual: ${evento.eventoLocal}): ", "").takeIf { it.isNotBlank() }?.let { evento.eventoLocal = it }

    readString("Novo preço do ingresso (atual: R${"%.2f".format(evento.eventoPreco)}): ", "").takeIf { it.isNotBlank() }?.let {
        val preco = it.toDoubleOrNull()
        if (preco != null && preco >= 0.0) evento.eventoPreco = preco
        else println("Erro: Preço inválido. Mantido o anterior.")
    }

    readString("Estorna cancelamento? S/N (atual: ${if (evento.eventoEstorna) "S" else "N"}): ", "").takeIf { it.isNotBlank() }?.let {
        evento.eventoEstorna = it.uppercase() == "S"
    }

    if (evento.eventoEstorna) {
        readString("Nova taxa de estorno % (atual: ${evento.eventoTaxaEstorno}): ", "").takeIf { it.isNotBlank() }?.let {
            val taxa = it.toDoubleOrNull()
            if (taxa != null && taxa in 0.0..100.0) evento.eventoTaxaEstorno = taxa
            else println("Erro: Taxa inválida. Mantida a anterior.")
        }
    }

    atualizarEvento(evento.eventoId, evento)
    println("Evento atualizado com sucesso!")
}

fun ativarEvento(usuarioLogado: Usuario) {
    val meusEventosInativos = listarEventosPorOrganizador(usuarioLogado.usuarioEmail).filter { !it.eventoAtivo }
    if (meusEventosInativos.isEmpty()) {
        println("Você não possui eventos inativos para ativar.")
        return
    }

    printTable("Eventos inativos:", meusEventosInativos.map { "[ID: ${it.eventoId}] ${it.eventoNome}" })

    val id = readInt("ID do evento a ativar: ", "ID inválido.")
    val evento = meusEventosInativos.find { it.eventoId == id }
    if (evento == null) {
        println("Erro: Evento não encontrado.")
        return
    }

    evento.eventoAtivo = true
    atualizarEvento(evento.eventoId, evento)
    println("Evento '${evento.eventoNome}' ativado com sucesso!")
}

fun desativarEvento(usuarioLogado: Usuario) {
    val meusEventosAtivos = listarEventosPorOrganizador(usuarioLogado.usuarioEmail).filter { it.eventoAtivo }
    if (meusEventosAtivos.isEmpty()) {
        println("Você não possui eventos ativos para desativar.")
        return
    }

    printTable("Eventos ativos:", meusEventosAtivos.map { "[ID: ${it.eventoId}] ${it.eventoNome}" })

    val id = readInt("ID do evento a desativar: ", "ID inválido.")
    val evento = meusEventosAtivos.find { it.eventoId == id }
    if (evento == null) {
        println("Erro: Evento não encontrado.")
        return
    }

    val ingressosDoEvento = listarIngressosPorEvento(evento.eventoId)
        .filter { it.ingressoStatus != StatusIngresso.CANCELADO }
    ingressosDoEvento.forEach { ingresso ->
        ingresso.ingressoStatus = StatusIngresso.CANCELADO
        atualizarIngresso(ingresso.ingressoId, ingresso)
        if (evento.eventoEstorna) {
            val valorEstorno = ingresso.ingressoValorPago * (1 - evento.eventoTaxaEstorno / 100)

            println("Ingresso #${ingresso.ingressoId} cancelado. " +
                    "Estorno de R${"%.2f".format(valorEstorno)} para ${ingresso.ingressoUsuarioEmail}")
        } else {
            println("Ingresso #${ingresso.ingressoId} cancelado para ${ingresso.ingressoUsuarioEmail} (Sem estorno conforme regra do evento).")
        }
    }

    evento.eventoAtivo = false
    atualizarEvento(evento.eventoId, evento)
    println("Evento '${evento.eventoNome}' desativado. ${ingressosDoEvento.size} ingresso(s) cancelado(s).")
}

fun listarEventosOrganizador(usuarioLogado: Usuario) {
    val meusEventos = listarEventosPorOrganizador(usuarioLogado.usuarioEmail)
    if (meusEventos.isEmpty()) {
        println("Você não possui eventos cadastrados.")
        return
    }

    val items = meusEventos.map {
        "[ID: ${it.eventoId}] ${it.eventoNome} | ${it.eventoDataInicio.format(formatarDataHora)} até ${it.eventoDataFim.format(formatarDataHora)} | Local: ${it.eventoLocal} | Preço: R${"%.2f".format(it.eventoPreco)} | Capacidade: ${it.eventoCapacidadeMax} | Ativo: ${if (it.eventoAtivo) "Sim" else "Não"}"
    }
    printTable("\n--- MEUS EVENTOS ---", items)
}

fun feedEventos() {
    val feed = obterFeedEventos()
    if (feed.isEmpty()) {
        println("Nenhum evento disponível no momento.")
        return
    }

    val items = feed.map { evento ->
        val ingressosVendidos = listarIngressosPorEvento(evento.eventoId).count { !"CANCELADO".equals(it.ingressoStatus) }
        val vagasRestantes = evento.eventoCapacidadeMax - ingressosVendidos
        "[ID: ${evento.eventoId}] ${evento.eventoNome} | ${evento.eventoDataInicio.format(formatarDataHora)} até ${evento.eventoDataFim.format(formatarDataHora)} | Local: ${evento.eventoLocal} | Preço: R${"%.2f".format(evento.eventoPreco)} | Vagas: $vagasRestantes/${evento.eventoCapacidadeMax} | Tipo: ${evento.eventoTipo}"
    }
    printTable("\n--- FEED DE EVENTOS ---", items)
}
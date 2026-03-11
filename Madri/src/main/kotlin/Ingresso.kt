import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun comprarIngresso(usuarioLogado: Usuario) {
    val feed = obterFeedEventos()
    if (feed.isEmpty()) {
        println("Nenhum evento disponível para compra.")
        return
    }

    val items = feed.map { "[ID: ${it.eventoId}] ${it.eventoNome} - R${"%.2f".format(it.eventoPreco)}" }
    printTable("Eventos disponíveis:", items)

    val idDesejado = readInt("Informe o ID do evento desejado: ", "ID inválido.")
    val evento = feed.find { it.eventoId == idDesejado }
    if (evento == null) {
        println("Erro: Evento não encontrado ou indisponível.")
        return
    }

    var totalAPagar = evento.eventoPreco
    val eventosParaComprar = mutableListOf(evento)

    evento.eventoIdEventoPrincipal?.let { principalId ->
        val eventoPrincipal = encontrarEventoPorId(principalId)
        if (eventoPrincipal != null) {
            totalAPagar += eventoPrincipal.eventoPreco
            eventosParaComprar.add(eventoPrincipal)
            println("Este evento está vinculado ao evento principal: '${eventoPrincipal.eventoNome}' (R${"%.2f".format(eventoPrincipal.eventoPreco)})")
        }
    }

    println("Valor total a pagar: R${"%.2f".format(totalAPagar)}")
    val confirmar = readString("Confirmar compra? (S/N): ", "Resposta inválida.").uppercase() == "S"
    if (!confirmar) {
        println("Compra cancelada.")
        return
    }

    eventosParaComprar.forEach { ev ->
        val novoIngresso = Ingresso(
            ingressoId = proximoIdIngresso++,
            ingressoEventoId = ev.eventoId,
            ingressoUsuarioEmail = usuarioLogado.usuarioEmail,
            ingressoValorPago = ev.eventoPreco
        )
        salvarIngresso(novoIngresso)
        println("Ingresso #${novoIngresso.ingressoId} gerado para '${ev.eventoNome}'")
    }
    println("Compra realizada! Total pago: R${"%.2f".format(totalAPagar)}")
}

fun cancelarIngresso(usuarioLogado: Usuario) {
    val meusIngressosAtivos = listarIngressosPorUsuario(usuarioLogado.usuarioEmail).filter { !"CANCELADO".equals(it.ingressoStatus) }
    if (meusIngressosAtivos.isEmpty()) {
        println("Você não possui ingressos ativos para cancelar.")
        return
    }

    val items = meusIngressosAtivos.map { ingresso ->
        val nomeEvento = encontrarEventoPorId(ingresso.ingressoEventoId)?.eventoNome ?: "Evento removido"
        "[Ingresso #${ingresso.ingressoId}] $nomeEvento - Pago: R${"%.2f".format(ingresso.ingressoValorPago)}"
    }
    printTable("Ingressos ativos:", items)

    val idCancelar = readInt("ID do ingresso a cancelar: ", "ID inválido.")
    val ingresso = meusIngressosAtivos.find { it.ingressoId == idCancelar }
    if (ingresso == null) {
        println("Erro: Ingresso não encontrado.")
        return
    }

    val evento = encontrarEventoPorId(ingresso.ingressoEventoId)
    ingresso.ingressoStatus = "CANCELADO"
    atualizarIngresso(ingresso.ingressoId, ingresso)

    if (evento != null && evento.eventoEstorna) {
        val valorEstorno = ingresso.ingressoValorPago * (1 - evento.eventoTaxaEstorno / 100)
        println("Ingresso #${ingresso.ingressoId} cancelado. Estorno de R${"%.2f".format(valorEstorno)} aplicado.")
    } else {
        println("Ingresso #${ingresso.ingressoId} cancelado sem estorno.")
    }
}

fun listarIngressos(usuarioLogado: Usuario) {
    val agora = LocalDateTime.now()
    val meusIngressos = listarIngressosPorUsuario(usuarioLogado.usuarioEmail).sortedWith(compareBy(
        { ingresso ->
            val evento = encontrarEventoPorId(ingresso.ingressoEventoId)
            val finalizado = evento == null || !evento.eventoAtivo || evento.eventoDataFim.isBefore(agora)
            if ("CANCELADO".equals(ingresso.ingressoStatus) || finalizado) 1 else 0
        },
        { ingresso ->
            encontrarEventoPorId(ingresso.ingressoEventoId)?.eventoDataInicio ?: LocalDateTime.MAX
        },
        { ingresso ->
            encontrarEventoPorId(ingresso.ingressoEventoId)?.eventoNome ?: ""
        }
    ))

    if (meusIngressos.isEmpty()) {
        println("Você não possui ingressos.")
        return
    }

    val items = meusIngressos.map { ingresso ->
        val evento = encontrarEventoPorId(ingresso.ingressoEventoId)
        val statusIngresso = if ("CANCELADO".equals(ingresso.ingressoStatus)) "CANCELADO" else "ATIVO"
        val nomeEvento = evento?.eventoNome ?: "Evento removido"
        val dataEvento = evento?.eventoDataInicio?.format(formatarDataHora) ?: "-"
        "[Ingresso #${ingresso.ingressoId}] $nomeEvento | Início: $dataEvento | Pago: R${"%.2f".format(ingresso.ingressoValorPago)} | Status: $statusIngresso"
    }
    printTable("\n--- MEUS INGRESSOS ---", items)
}
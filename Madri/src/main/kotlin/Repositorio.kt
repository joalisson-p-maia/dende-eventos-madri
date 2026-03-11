import java.time.LocalDateTime

val usuarios = mutableListOf<Usuario>()
val eventos = mutableListOf<Evento>()
val ingressos = mutableListOf<Ingresso>()

var proximoIdEvento = 1
var proximoIdIngresso = 1

// Usuario CRUD
fun salvarUsuario(usuario: Usuario) {
    usuarios.add(usuario)
}

fun encontrarUsuarioPorEmail(email: String): Usuario? {
    return usuarios.find { it.usuarioEmail == email }
}

fun listarUsuarios(): List<Usuario> {
    return usuarios.toList()
}

fun atualizarUsuario(email: String, novoUsuario: Usuario) {
    val index = usuarios.indexOfFirst { it.usuarioEmail == email }
    if (index != -1) {
        usuarios[index] = novoUsuario
    }
}

fun deletarUsuario(email: String) {
    usuarios.removeIf { it.usuarioEmail == email }
}

// Evento CRUD
fun salvarEvento(evento: Evento) {
    eventos.add(evento)
}

fun encontrarEventoPorId(id: Int): Evento? {
    return eventos.find { it.eventoId == id }
}

fun listarEventos(): List<Evento> {
    return eventos.toList()
}

fun listarEventosPorOrganizador(email: String): List<Evento> {
    return eventos.filter { it.eventoOrganizadorEmail == email }
}

fun atualizarEvento(id: Int, novoEvento: Evento) {
    val index = eventos.indexOfFirst { it.eventoId == id }
    if (index != -1) {
        eventos[index] = novoEvento
    }
}

fun deletarEvento(id: Int) {
    eventos.removeIf { it.eventoId == id }
}

// Ingresso CRUD
fun salvarIngresso(ingresso: Ingresso) {
    ingressos.add(ingresso)
}

fun encontrarIngressoPorId(id: Int): Ingresso? {
    return ingressos.find { it.ingressoId == id }
}

fun listarIngressos(): List<Ingresso> {
    return ingressos.toList()
}

fun listarIngressosPorUsuario(email: String): List<Ingresso> {
    return ingressos.filter { it.ingressoUsuarioEmail == email }
}

fun listarIngressosPorEvento(eventoId: Int): List<Ingresso> {
    return ingressos.filter { it.ingressoEventoId == eventoId }
}

fun atualizarIngresso(id: Int, novoIngresso: Ingresso) {
    val index = ingressos.indexOfFirst { it.ingressoId == id }
    if (index != -1) {
        ingressos[index] = novoIngresso
    }
}

fun deletarIngresso(id: Int) {
    ingressos.removeIf { it.ingressoId == id }
}

// Additional queries
fun obterFeedEventos(): List<Evento> {
    val agora = LocalDateTime.now()
    return eventos.filter { evento ->
        evento.eventoAtivo &&
                evento.eventoDataFim.isAfter(agora) &&
                listarIngressosPorEvento(evento.eventoId).count { !"CANCELADO".equals(it.ingressoStatus) } < evento.eventoCapacidadeMax
    }.sortedWith(compareBy({ it.eventoDataInicio }, { it.eventoNome }))
}
import java.time.LocalDate
import java.time.LocalDateTime

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
    println("Hello Kotlin")
}

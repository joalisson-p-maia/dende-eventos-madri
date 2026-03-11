import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

fun cadastrarUsuarioComum() {
    val email = readString("Digite seu Email: ", "Email inválido.")
    if (encontrarUsuarioPorEmail(email) != null) {
        println("Erro: Email já cadastrado.")
        return
    }

    val nome = readString("Digite seu Nome: ", "Nome inválido.", 1)
    val dataNascimentoStr = readString("Digite sua Data de Nascimento no formato (ddMMyyyy): ", "Data inválida.")
    val dataNascimento = try {
        LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ofPattern("ddMMyyyy"))
    } catch (e: Exception) {
        println("Erro: Data inválida.")
        return
    }
    val sexo = readString("Digite seu Sexo: ", "Sexo inválido.", 1)
    val senha = readString("Digite sua Senha: ", "Senha inválida.", 1)

    val novoUsuario = Usuario(
        usuarioEmail = email,
        usuarioNome = nome,
        usuarioDataNascimento = dataNascimento,
        usuarioSexo = sexo,
        usuarioSenha = senha,
        usuarioTipo = TipoUsuario.COMUM
    )
    salvarUsuario(novoUsuario)
    println("Cadastrado com sucesso!")
}

fun cadastrarUsuarioOrganizador() {
    val email = readString("Digite seu Email: ", "Email inválido.")
    if (encontrarUsuarioPorEmail(email) != null) {
        println("Erro: Email já cadastrado.")
        return
    }

    val nome = readString("Digite seu Nome: ", "Nome inválido.", 1)
    val dataNascimentoStr = readString("Digite sua Data de Nascimento no formato (ddMMyyyy): ", "Data inválida.")
    val dataNascimento = try {
        LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ofPattern("ddMMyyyy"))
    } catch (e: Exception) {
        println("Erro: Data inválida.")
        return
    }
    val sexo = readString("Digite seu Sexo: ", "Sexo inválido.", 1)
    val senha = readString("Digite sua Senha: ", "Senha inválida.", 1)

    val novoUsuario = Usuario(
        usuarioEmail = email,
        usuarioNome = nome,
        usuarioDataNascimento = dataNascimento,
        usuarioSexo = sexo,
        usuarioSenha = senha,
        usuarioTipo = TipoUsuario.ORGANIZADOR
    )

    val informarEmpresa = readString("Informar empresa? (S/N): ", "Resposta inválida.").uppercase() == "S"
    if (informarEmpresa) {
        novoUsuario.usuarioCnpj = readString("CNPJ: ", "CNPJ inválido.", 1)
        novoUsuario.usuarioRazaoSocial = readString("Razão Social: ", "Razão Social inválida.", 1)
        novoUsuario.usuarioNomeFantasia = readString("Nome Fantasia: ", "Nome Fantasia inválida.", 1)
    }

    salvarUsuario(novoUsuario)
    println("Cadastrado com sucesso!")
}

fun alterarPerfilUsuario(usuarioLogado: Usuario) {
    println("Deixe em branco para não alterar.")

    readString("Novo nome (atual: ${usuarioLogado.usuarioNome}): ", "").takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioNome = it }
    readString("Nova senha: ", "").takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioSenha = it }
    readString("Novo sexo (atual: ${usuarioLogado.usuarioSexo}): ", "").takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioSexo = it }

    readString("Nova Data de Nascimento no formato (DD/MM/YYYY) (atual: ${usuarioLogado.usuarioDataNascimento.format(formatarData)}): ", "").takeIf { it.isNotBlank() }?.let {
        try {
            usuarioLogado.usuarioDataNascimento = LocalDate.parse(it, formatarData)
        } catch (e: Exception) {
            println("Erro: Data inválida. Mantida a data anterior.")
        }
    }

    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
        readString("Novo Nome Fantasia (atual: ${usuarioLogado.usuarioNomeFantasia ?: "não informado"}): ", "").takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioNomeFantasia = it }
    }

    atualizarUsuario(usuarioLogado.usuarioEmail, usuarioLogado)
    println("Dados atualizados!")
}

fun visualizarPerfilUsuario(usuarioLogado: Usuario) {
    val idade = Period.between(usuarioLogado.usuarioDataNascimento, LocalDate.now())
    println("Nome: ${usuarioLogado.usuarioNome} | Email: ${usuarioLogado.usuarioEmail}")
    println("Idade: ${idade.years} anos, ${idade.months} meses, ${idade.days} dias")
    usuarioLogado.usuarioCnpj?.let {
        println("Empresa: $it - ${usuarioLogado.usuarioNomeFantasia}")
    }
}

fun inativarUsuario(usuarioLogado: Usuario) {
    val temEventoAtivo = listarEventosPorOrganizador(usuarioLogado.usuarioEmail).any {
        it.eventoAtivo && it.eventoDataFim.isAfter(java.time.LocalDateTime.now())
    }
    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR && temEventoAtivo) {
        println("Erro: Você possui eventos ativos.")
        return
    }
    usuarioLogado.usuarioAtivo = false
    atualizarUsuario(usuarioLogado.usuarioEmail, usuarioLogado)
    println("Conta inativada.")
}

fun reativarUsuario(): Usuario? {
    val email = readString("Email: ", "Email inválido.")
    val senha = readString("Senha: ", "Senha inválida.")
    val usuario = encontrarUsuarioPorEmail(email)
    if (usuario != null && usuario.usuarioSenha == senha) {
        if (!usuario.usuarioAtivo) {
            val reativar = readString("Conta inativa. Reativar? (S/N): ", "Resposta inválida.").uppercase() == "S"
            if (reativar) {
                usuario.usuarioAtivo = true
                atualizarUsuario(email, usuario)
                println("Reativada!")
                return usuario
            }
        } else {
            println("Olá, ${usuario.usuarioNome}!")
            return usuario
        }
    } else {
        println("Erro: Credenciais inválidas.")
    }
    return null
}
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

fun cadastrarUsuarioComum() {
    val email = readString("Digite seu Email: ", "Email inválido.",1)
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

    println("Escolha o Sexo: 1 - MASCULINO, 2 - FEMININO, 3 - OUTROS")
    val opcaoSexo = readInt("Opção: ", "Opção inválida.")
    val sexo = when(opcaoSexo) {
        1 -> SEXO.MASCULINO
        2 -> SEXO.FEMININO
        else -> SEXO.OUTROS
    }

    var senha = ""
    while (senha.length !in 8..32) {
        senha = readString("Digite sua Senha (8 a 32 caracteres): ", "Senha inválida.")
        if (senha.length !in 8..32) println("Erro: A senha deve ter entre 8 e 32 dígitos.")
    }

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

    val idade = Period.between(dataNascimento, LocalDate.now()).years
    if (idade < 18) {
        println("Você precisa ter 18 anos ou mais para se cadastrar.")
        return
    }

    println("Escolha o Sexo: 1 - MASCULINO, 2 - FEMININO, 3 - OUTROS")
    val opcaoSexo = readInt("Opção: ", "Opção inválida.")
    val sexo = when(opcaoSexo) {
        1 -> SEXO.MASCULINO
        2 -> SEXO.FEMININO
        else -> SEXO.OUTROS
    }

    var senha = ""
    while (senha.length !in 8..32) {
        senha = readString("Digite sua Senha (8 a 32 caracteres): ", "Senha inválida.")
        if (senha.length !in 8..32) println("Erro: A senha deve ter entre 8 e 32 dígitos.")
    }

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

    val novaSenha = readString("Nova senha (8-32 caracteres): ", "")
    if (novaSenha.isNotBlank()) {
        if (novaSenha.length in 8..32) {
            usuarioLogado.usuarioSenha = novaSenha
        } else {
            println("Erro: Senha não alterada (deve ter entre 8 e 32 dígitos).")
        }
    }

    println("Novo sexo (atual: ${usuarioLogado.usuarioSexo}): 1-MASCULINO, 2-FEMININO, 3-OUTROS (ou 0 para manter)")
    when (readInt("Opção: ", "Opção inválida.")) {
        1 -> usuarioLogado.usuarioSexo = SEXO.MASCULINO
        2 -> usuarioLogado.usuarioSexo = SEXO.FEMININO
        3 -> usuarioLogado.usuarioSexo = SEXO.OUTROS
    }

    readString("Nova Data de Nascimento no formato (DD/MM/YYYY) (atual: ${usuarioLogado.usuarioDataNascimento.format(formatarData)}): ", "").takeIf { it.isNotBlank() }?.let { dataEntrada ->
        try {
            val novaData = LocalDate.parse(dataEntrada, formatarData)
            val idade = Period.between(novaData, LocalDate.now()).years

            if (idade < 18) {
                println("Erro: Alteração negada. O usuário deve ter 18 anos ou mais.")
            } else {
                usuarioLogado.usuarioDataNascimento = novaData
                println("Data de nascimento atualizada com sucesso!")
            }
        } catch (e: Exception) {
            println("Erro: Data inválida. Mantida a data anterior.")
        }
    }

    if (usuarioLogado.usuarioTipo == TipoUsuario.ORGANIZADOR) {
        readString("Novo Nome Fantasia (atual: ${usuarioLogado.usuarioNomeFantasia ?: "não informado"}): ", "").takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioNomeFantasia = it }
        readString("Nova Razao Social (atual: ${usuarioLogado.usuarioRazaoSocial ?: "não informado"}): ", "").takeIf { it.isNotBlank() }?.let { usuarioLogado.usuarioRazaoSocial = it }
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
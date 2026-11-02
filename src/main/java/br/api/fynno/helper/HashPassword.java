package br.api.fynno.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Utilização do final para que a classe seja chamada apenas
// uma vez durante o ciclo de vida da aplicação. Neste caso
// este helper não será instanciado por chamar apenas métodos
// estáticos.
public final class HashPassword {

    // Assina apenas uma vez o crypt que será utilizado
    // no ciclo de vida do método estático que será invocado.
    private static final BCryptPasswordEncoder CRYPT =  new BCryptPasswordEncoder();

    // Evita que esta classe seja instanciada. Mesmo conceito
    // de singleton, mas neste caso apenas se trata de uma classe
    // que não possui métodos que seriam chamados por uma instância,
    // e sim todos estáticos.
    private HashPassword() {}

    // Utilização do public para que os outros pacotes
    // possam enxergar este método.
    public static String encode(String password) {
        return CRYPT.encode(password);
    }

    // Idem o comentário de cima.
    public static boolean check(String password, String passwordHash) {
        return CRYPT.matches(password, passwordHash);
    }
}

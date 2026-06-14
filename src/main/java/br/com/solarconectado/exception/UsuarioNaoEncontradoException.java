package br.com.solarconectado.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(String identificador) {
        super("Usuário não encontrado: " + identificador);
    }
}

package io.github.joseprandj.msAvaliadorCredito.exception;

public class DadosClienteNotFoundException extends Exception {
    public DadosClienteNotFoundException() {
        super("Dados do cliente não foi encontrado para o CPF informado.");
    }
}

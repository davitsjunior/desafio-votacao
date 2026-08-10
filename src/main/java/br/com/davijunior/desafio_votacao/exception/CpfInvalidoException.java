package br.com.davijunior.desafio_votacao.exception;

public class CpfInvalidoException extends RuntimeException {

    public CpfInvalidoException(String message) {
        super(message);
    }
}
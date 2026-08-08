package br.com.davijunior.desafio_votacao.exception;

public class PautaNaoEncontradaException extends RuntimeException {

    public PautaNaoEncontradaException(String message) {
        super(message);
    }
}
package br.com.davijunior.desafio_votacao.exception;

public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException(String message) {
        super(message);
    }
}
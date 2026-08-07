package br.com.davijunior.desafio_votacao.exception;

public class SessaoJaExisteException extends RuntimeException {

    public SessaoJaExisteException(String message) {
        super(message);
    }
}
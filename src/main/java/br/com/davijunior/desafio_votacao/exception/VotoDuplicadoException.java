package br.com.davijunior.desafio_votacao.exception;

public class VotoDuplicadoException extends RuntimeException {

    public VotoDuplicadoException(String message) {
        super(message);
    }
}
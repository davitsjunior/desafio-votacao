package br.com.davijunior.desafio_votacao.exception;

public class SessaoFechadaException extends RuntimeException {

    public SessaoFechadaException(String message) {
        super(message);
    }
}
package br.com.davijunior.desafio_votacao.exception;

import br.com.davijunior.desafio_votacao.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacao(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn(message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({PautaNaoEncontradaException.class, SessaoNaoEncontradaException.class,
            CpfInvalidoException.class, AssociadoNaoAptoException.class})
    public ResponseEntity<ErrorResponse> handleNaoEncontrada(RuntimeException ex) {
        log.warn(ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({SessaoJaExisteException.class, SessaoFechadaException.class, VotoDuplicadoException.class})
    public ResponseEntity<ErrorResponse> handleConflito(RuntimeException ex) {
        log.warn(ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleViolacaoIntegridade(DataIntegrityViolationException ex) {
        log.warn(ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "Violação de restrição de integridade dos dados");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenerica(Exception ex) {
        log.error("Erro inesperado", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(Instant.now(), status.value(), message);
        return ResponseEntity.status(status).body(body);
    }
}

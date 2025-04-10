package bovisApp.firstApp.exception;

import bovisApp.firstApp.exception.transacao.TransacaoInvalidaException;
import bovisApp.firstApp.exception.transacao.TransacaoNaoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomResponseError handleIllegalArgument(IllegalArgumentException iae) {
        return new CustomResponseError(iae.getMessage());
    }

    @ExceptionHandler(TransacaoNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomResponseError handleTransacaoNaoEncontrada(TransacaoNaoEncontradaException e) {
        return new CustomResponseError(e.getMessage());
    }

    @ExceptionHandler(TransacaoInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomResponseError handleTransacaoInvalida(TransacaoInvalidaException e) {
        return new CustomResponseError(e.getMessage());
    }

}

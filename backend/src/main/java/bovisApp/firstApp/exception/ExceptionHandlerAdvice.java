package bovisApp.firstApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomResponseError handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException e) {
        return new CustomResponseError(e.getMessage());
    }

    @ExceptionHandler(EntidadeInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomResponseError handleEntidadeInvalida(EntidadeInvalidaException e) {
        return new CustomResponseError(e.getMessage());
    }

    @ExceptionHandler(EntidadeJaExisteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CustomResponseError handleEntidadeJaExiste(EntidadeJaExisteException e) {
        return new CustomResponseError(e.getMessage());
    }

}

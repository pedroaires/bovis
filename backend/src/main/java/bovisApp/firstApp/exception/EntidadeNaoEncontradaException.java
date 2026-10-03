package bovisApp.firstApp.exception;

public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String entidade, Long id){
        super(entidade + " com ID " + id + " não foi encontrada.");
    }
}

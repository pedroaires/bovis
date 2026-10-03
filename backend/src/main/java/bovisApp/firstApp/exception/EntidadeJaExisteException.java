package bovisApp.firstApp.exception;

public class EntidadeJaExisteException extends RuntimeException {
    public EntidadeJaExisteException(String entidade, Long id){
        super("Entidade " + entidade + " com ID " + id + "já existe");
    }
}

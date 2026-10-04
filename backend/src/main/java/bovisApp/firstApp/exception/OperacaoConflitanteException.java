package bovisApp.firstApp.exception;

public class OperacaoConflitanteException extends RuntimeException {
    public OperacaoConflitanteException(String mensagem) {
        super(mensagem);
    }
}

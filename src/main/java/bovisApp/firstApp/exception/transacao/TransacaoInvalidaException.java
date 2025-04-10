package bovisApp.firstApp.exception.transacao;

public class TransacaoInvalidaException extends RuntimeException {
    public TransacaoInvalidaException(String message){
        super(message);
    }
}

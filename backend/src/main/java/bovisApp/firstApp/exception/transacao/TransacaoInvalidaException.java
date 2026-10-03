package bovisApp.firstApp.exception.transacao;

import bovisApp.firstApp.exception.EntidadeInvalidaException;

public class TransacaoInvalidaException extends EntidadeInvalidaException {
    public TransacaoInvalidaException(String message){
        super(message);
    }
}

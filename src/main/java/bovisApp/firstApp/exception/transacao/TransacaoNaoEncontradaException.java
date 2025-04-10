package bovisApp.firstApp.exception.transacao;

import javax.persistence.EntityNotFoundException;

public class TransacaoNaoEncontradaException extends EntityNotFoundException {
    public TransacaoNaoEncontradaException(Long id){
        super(String.format("Transacao com ID: %d não encontrada", id));
    }
}

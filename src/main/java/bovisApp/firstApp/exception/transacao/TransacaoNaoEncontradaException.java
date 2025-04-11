package bovisApp.firstApp.exception.transacao;

import bovisApp.firstApp.exception.EntidadeNaoEncontradaException;

import javax.persistence.EntityNotFoundException;

public class TransacaoNaoEncontradaException extends EntidadeNaoEncontradaException {
    public TransacaoNaoEncontradaException(Long id){
        super("Transação", id);
    }
}

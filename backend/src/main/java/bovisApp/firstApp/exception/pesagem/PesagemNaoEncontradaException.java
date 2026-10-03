package bovisApp.firstApp.exception.pesagem;

import bovisApp.firstApp.exception.EntidadeNaoEncontradaException;

public class PesagemNaoEncontradaException extends EntidadeNaoEncontradaException {
    public PesagemNaoEncontradaException(Long id){
        super("Pesagem", id);
    }
}

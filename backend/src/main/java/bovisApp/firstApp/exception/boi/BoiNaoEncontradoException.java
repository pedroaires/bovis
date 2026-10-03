package bovisApp.firstApp.exception.boi;

import bovisApp.firstApp.exception.EntidadeNaoEncontradaException;


public class BoiNaoEncontradoException extends EntidadeNaoEncontradaException {
    public BoiNaoEncontradoException(Long id){
        super("Boi", id);
    }
}

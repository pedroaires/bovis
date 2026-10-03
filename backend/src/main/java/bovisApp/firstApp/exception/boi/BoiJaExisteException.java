package bovisApp.firstApp.exception.boi;

import bovisApp.firstApp.exception.EntidadeJaExisteException;

public class BoiJaExisteException extends EntidadeJaExisteException {
    public BoiJaExisteException(Long id){
        super("Boi", id);
    }
}

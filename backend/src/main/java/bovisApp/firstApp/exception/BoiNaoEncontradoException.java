package bovisApp.firstApp.exception;

import javax.persistence.EntityNotFoundException;

public class BoiNaoEncontradoException extends EntityNotFoundException {
    public BoiNaoEncontradoException(String message){
        super(message);
    }
}

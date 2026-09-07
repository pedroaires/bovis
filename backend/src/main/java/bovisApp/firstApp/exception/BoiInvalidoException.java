package bovisApp.firstApp.exception;

import org.aspectj.bridge.Message;

public class BoiInvalidoException extends IllegalArgumentException{
    public BoiInvalidoException(String message){
        super(message);
    }
}

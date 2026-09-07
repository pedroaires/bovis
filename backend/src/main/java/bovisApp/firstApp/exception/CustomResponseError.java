package bovisApp.firstApp.exception;

public class CustomResponseError {
    private String message;

    public CustomResponseError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

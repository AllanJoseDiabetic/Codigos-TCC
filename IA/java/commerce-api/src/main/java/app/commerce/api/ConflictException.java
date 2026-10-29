package app.commerce.api;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
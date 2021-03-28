package model.exceptions;

public class ContactAlreadyExistException extends Exception {
    public ContactAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}

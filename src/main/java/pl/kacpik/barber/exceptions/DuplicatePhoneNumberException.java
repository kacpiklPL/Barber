package pl.kacpik.barber.exceptions;

public class DuplicatePhoneNumberException extends RuntimeException {

    public DuplicatePhoneNumberException(String message) {
        super(message);
    }
}

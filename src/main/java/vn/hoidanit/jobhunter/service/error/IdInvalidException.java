package vn.hoidanit.jobhunter.service.error;

public class IdInvalidException extends Exception {

    // https://stackoverflow.com/questions/8423700/how-to-create-a-custom-exception-type-in-java
    public IdInvalidException(String message) {
        super(message);
    }
}

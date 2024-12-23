package vn.hoidanit.jobhunter.util.error;

public class PermissionException extends Exception {
    // https://stackoverflow.com/questions/8423700/how-to-create-a-custom-exception-type-in-java
    public PermissionException(String message) {
        super(message);
    }
}

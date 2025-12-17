package exception;

public class InvalidEventException extends Exception {

    private Integer code;

    public InvalidEventException(String message, Integer code) {
        super(message);
        this.code=code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code=code;
    }
}

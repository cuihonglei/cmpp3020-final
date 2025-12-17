package exception;

public class CapacityExceededException extends Exception{

    private Integer code;

    public CapacityExceededException(String message, Integer code) {
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

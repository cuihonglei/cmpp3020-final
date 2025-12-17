package exception;

public class RoomUnavailableException extends Exception {

    private Integer code;

    public RoomUnavailableException(String message, Integer code) {
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


package been;

public class HttpResult<T> {
    private T data;
    private int code;
    private String msg;

    public HttpResult(T data, int code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }


    public HttpResult( int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package cn.shiroblue.http;

import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 * 用于通知停止处理
 * ======================
 * by WhiteBlue
 * on 15/10/30
 */
public class HaltException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int statusCode = HttpServletResponse.SC_OK;
    private String body = null;

    public HaltException() {
        super();
    }

    public HaltException(int statusCode) {
        this.statusCode = statusCode;
    }

    public HaltException(String body) {
        this.body = body;
    }

    public HaltException(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }


    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
}

package cn.shiroblue.http;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/10/29
 */
public class Response {
    private HttpServletResponse response;
    private String body;

    //For wrapper
    protected Response() {
    }

    public Response(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * 设置http状态码
     *
     * @param statusCode 状态码
     */
    public void status(int statusCode) {
        response.setStatus(statusCode);
    }

    /**
     * 设置content-typpe
     *
     * @param contentType content-type
     */
    public void type(String contentType) {
        response.setContentType(contentType);
    }

    /**
     * 设置响应本体
     *
     * @param body 本体
     */
    public void body(String body) {
        this.body = body;
    }

    /**
     * 得到响应内容
     *
     * @return the body
     */
    public String body() {
        return this.body;
    }

    /**
     * @return the raw response object handed in by Jetty
     */
    public HttpServletResponse raw() {
        return response;
    }

    /**
     * 发送重定向
     *
     * @param location Where to redirect
     */
    public void redirect(String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * 发送重定向(带状态码)
     *
     * @param location       Where to redirect permanently
     * @param httpStatusCode the http status code
     */
    public void redirect(String location, int httpStatusCode) {
        response.setStatus(httpStatusCode);
        response.setHeader("Location", location);
        response.setHeader("Connection", "close");
        try {
            response.sendError(httpStatusCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置header
     *
     * @param header the header
     * @param value  the value
     */
    public void header(String header, String value) {
        response.addHeader(header, value);
    }

}

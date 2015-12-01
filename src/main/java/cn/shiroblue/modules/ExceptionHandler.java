package cn.shiroblue.modules;

import cn.shiroblue.http.Request;
import cn.shiroblue.http.Response;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/6
 */
public interface ExceptionHandler {

    void handle(Exception exception, Request request, Response response);

}

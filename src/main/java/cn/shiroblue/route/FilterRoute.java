package cn.shiroblue.route;

import cn.shiroblue.http.Request;
import cn.shiroblue.http.Response;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/4
 */
public interface FilterRoute {

    void handle(Request request, Response response) throws Exception;

}

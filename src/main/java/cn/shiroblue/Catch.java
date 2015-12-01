package cn.shiroblue;

import cn.shiroblue.core.ExceptionMapperFactory;
import cn.shiroblue.http.Request;
import cn.shiroblue.http.Response;
import cn.shiroblue.modules.ExceptionHandler;
import cn.shiroblue.modules.ExceptionHandlerImpl;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/6
 */
public class Catch {

    private Catch() {
    }


    public static void exception(Class<? extends Exception> exceptionClass, final ExceptionHandler handler) {
        ExceptionHandlerImpl wrapper = new ExceptionHandlerImpl(exceptionClass) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                handler.handle(exception, request, response);
            }
        };

        ExceptionMapperFactory.get().map(exceptionClass, wrapper);
    }
}

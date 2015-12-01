package cn.shiroblue.modules;

import cn.shiroblue.http.Request;
import cn.shiroblue.http.Response;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/10/30
 */
public abstract class ExceptionHandlerImpl {

    //Exception class will be handle
    protected Class<? extends Exception> exceptionClass;


    //constructor
    public ExceptionHandlerImpl(Class<? extends Exception> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }


    public Class<? extends Exception> exceptionClass() {
        return this.exceptionClass;
    }


    public void exceptionClass(Class<? extends Exception> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }


    public abstract void handle(Exception exception, Request request, Response response);
}

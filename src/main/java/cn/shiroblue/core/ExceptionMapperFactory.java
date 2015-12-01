package cn.shiroblue.core;

/**
 * Description:
 * ======================
 * by WhiteBlue
 * on 15/11/8
 */
public class ExceptionMapperFactory {

    private static ExceptionMapper exceptionMapper = null;

    private ExceptionMapperFactory() {
    }

    public static ExceptionMapper get() {
        if (exceptionMapper == null) {
            exceptionMapper = new ExceptionMapper();
        }
        return exceptionMapper;
    }
}

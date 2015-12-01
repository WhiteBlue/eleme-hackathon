package cn.shiroblue;

import cn.shiroblue.http.HaltException;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/5
 */
public class Action {

    private Action() {
    }

    public static void halt() {
        throw new HaltException();
    }

    public static void halt(int status) {
        throw new HaltException(status);
    }

    public static void halt(String body) {
        throw new HaltException(body);
    }

    public static void halt(int status, String body) {
        throw new HaltException(status, body);
    }
}

package cn.shiroblue.utils;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/10/30
 */
public class TinyUtils {

    private TinyUtils() {

    }

    public static boolean isParam(String routePart) {
        return routePart.startsWith(":");
    }
}

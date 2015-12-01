package cn.shiroblue.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/10/29
 */
public class UrlUtils {

    /**
     * 路由 => list
     *
     * @param route 路由
     * @return List
     */
    public static List<String> convertRouteToList(String route) {
        String[] pathArray = route.split("/");
//        List<String> path = new ArrayList<>();
//        for (String p : pathArray) {
//            if (p.length() > 0) {
//                path.add(p);
//            }
//        }
        return Arrays.asList(pathArray);
    }


    /**
     * path格式化
     *
     * @return String
     */
    public static String pathFormat(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }
}

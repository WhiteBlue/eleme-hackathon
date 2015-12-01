package cn.shiroblue.route;

import cn.shiroblue.modules.Render;
import cn.shiroblue.utils.UrlUtils;

import java.util.List;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/10/29
 */
public class RouteEntry {
    public HttpMethod httpMethod;

    //匹配路径
    public String matchPath;

    //目标Route对象
    public Object route;

    public Render render;

    /**
     * 构造函数(执行格式化)
     *
     * @param httpMethod httpMethod
     * @param matchUrl   未处理url
     * @param route      Route
     */
    public RouteEntry(HttpMethod httpMethod, String matchUrl, Object route) {
        this.httpMethod = httpMethod;
        this.matchPath = UrlUtils.pathFormat(matchUrl);
        this.route = route;
        this.render = null;
    }


    public RouteEntry(HttpMethod httpMethod, String matchUrl, Object route, Render render) {
        this.httpMethod = httpMethod;
        this.matchPath = UrlUtils.pathFormat(matchUrl);
        this.route = route;
        this.render = render;
    }

    /**
     * 路径匹配
     *
     * @param requestMethod request httpMethod
     * @param path          clean url
     * @return boolean
     */
    public boolean matches(HttpMethod requestMethod, String path) {
        boolean match = matchPath(path);

        if (match) {
            if (!((this.httpMethod == HttpMethod.before || this.httpMethod == HttpMethod.after) || (this.httpMethod == requestMethod))) {
                match = false;
            }
        }

        return match;
    }

    /**
     * 路径组件匹配
     *
     * @param url 处理过的url(去掉slash)
     * @return boolean
     */
    private boolean matchPath(String url) {
        //完全一致则返回
        if (this.matchPath.equals(url)) {
            return true;
        }

        //路径分割
        List<String> thisPathList = UrlUtils.convertRouteToList(this.matchPath);
        List<String> pathList = UrlUtils.convertRouteToList(url);

        int thisPathSize = thisPathList.size();
        int pathSize = pathList.size();

        //对称
        if (thisPathSize == pathSize) {
            for (int i = 0; i < thisPathSize; i++) {
                //=>mathPath
                String thisPathPart = thisPathList.get(i);
                //=>url
                String pathPart = pathList.get(i);

                if ((i == thisPathSize - 1) && (thisPathPart.equals("*") && this.matchPath.endsWith("*"))) {
                    return true;
                }

                if ((!thisPathPart.startsWith(":")) && !thisPathPart.equals(pathPart) && !thisPathPart.equals("*")) {
                    return false;
                }
            }
            return true;
            //非对称
        } else {
            //结尾为全匹配则逐个比对
            if (this.matchPath.endsWith("*")) {
                if (thisPathSize < pathSize) {
                    for (int i = 0; i < thisPathSize; i++) {
                        String thisPathPart = thisPathList.get(i);
                        String pathPart = pathList.get(i);

                        if ((i == thisPathSize - 1) && thisPathPart.equals("*") && this.matchPath.endsWith("*")) {
                            return true;
                        }

                        if (!thisPathPart.startsWith(":") && !thisPathPart.equals(pathPart) && !thisPathPart.equals("*")) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public String toString() {
        return httpMethod.name() + ", " + matchPath + ", " + route;
    }
}

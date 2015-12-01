package cn.shiroblue.route;

import cn.shiroblue.modules.Render;

/**
 * Description:
 * 匹配后的路径对象
 * ======================
 * by WhiteBlue
 * on 15/10/30
 */
public class RouteMatch {
    private HttpMethod httpMethod;

    private String matchPath;

    private String url;

    private Object target;

    private Render render;

    /**
     * @param httpMethod request method
     * @param matchPath  route match path
     * @param url        request url
     * @param target     action target
     */
    public RouteMatch(HttpMethod httpMethod, String matchPath, String url, Object target, Render render) {
        this.httpMethod = httpMethod;
        this.matchPath = matchPath;
        this.url = url;
        this.target = target;
        this.render = render;
    }


    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getMatchPath() {
        return matchPath;
    }

    public String getUrl() {
        return url;
    }

    public Object getTarget() {
        return target;
    }

    public Render getRender() {
        return render;
    }
}

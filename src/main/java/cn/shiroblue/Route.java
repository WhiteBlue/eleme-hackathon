package cn.shiroblue;

import cn.shiroblue.core.RouteMatcherFactory;
import cn.shiroblue.modules.Render;
import cn.shiroblue.route.FilterRoute;
import cn.shiroblue.route.HandlerRoute;
import cn.shiroblue.route.HttpMethod;

/**
 * Description:
 * 路由注入
 * ======================
 * by WhiteBlue
 * on 15/10/30
 */
public class Route {

    private Route() {
    }

    public static void get(String path, HandlerRoute route) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.GET, path, route);
    }

    public static void get(String path, HandlerRoute route, Render render) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.GET, path, route, render);
    }

    public static void post(String path, HandlerRoute route) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.POST, path, route);
    }

    public static void post(String path, HandlerRoute route, Render render) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.POST, path, route, render);
    }

    public static void put(String path, HandlerRoute route) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.PUT, path, route);
    }

    public static void put(String path, HandlerRoute route, Render render) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.PUT, path, route, render);
    }

    public static void patch(String path, HandlerRoute route) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.PATCH, path, route);
    }

    public static void patch(String path, HandlerRoute route, Render render) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.PATCH, path, route, render);
    }

    public static void delete(String path, HandlerRoute route) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.DELETE, path, route);
    }

    public static void delete(String path, HandlerRoute route, Render render) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.DELETE, path, route, render);
    }


    public static void before(String path, FilterRoute route) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.before, path, route);
    }

    public static void after(String path, FilterRoute route) {
        RouteMatcherFactory.get().putRouteEntry(HttpMethod.after, path, route);
    }

}

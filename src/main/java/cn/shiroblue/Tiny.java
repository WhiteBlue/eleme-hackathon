package cn.shiroblue;

import cn.shiroblue.server.JettyServerFactory;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/8
 */
public class Tiny {

    private Tiny() {
    }

    public static void server(String host, int port) {
        JettyServerFactory.newInstance(host, port);
    }

    public static void server() {
        JettyServerFactory.newInstance();
    }

    public static void server(String host, int port, int maxThreads, int minThreads, int threadTimeoutMillis) {
        JettyServerFactory.newInstance(host, port, maxThreads, minThreads, threadTimeoutMillis);
    }

    public static void executorServer(String host, int port) {
        JettyServerFactory.newExecutorInstance(host, port);
    }
}

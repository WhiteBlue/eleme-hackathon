package cn.shiroblue.server;

import cn.shiroblue.core.TinyHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/8
 */
public class JettyServerFactory {

    private static String DEFAULT_HOST = "127.0.0.1";
    private static int DEFAULT_PORT = 8080;

    private static int DEFAULT_MIN_THREAD = 8;
    private static int DEFAULT_MAX_THREAD = 200;
    private static int DEFAULT_THREAD_TIMEOUT = 60000;

    public static void newInstance() {
        newInstance(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_MAX_THREAD, DEFAULT_MIN_THREAD, DEFAULT_THREAD_TIMEOUT);
    }

    public static void newInstance(String host, int port) {
        newInstance(host, port, DEFAULT_MAX_THREAD, DEFAULT_MIN_THREAD, DEFAULT_THREAD_TIMEOUT);
    }

    public static void newInstance(String host, int port, int maxThreads, int minThreads) {
        newInstance(host, port, maxThreads, minThreads, DEFAULT_THREAD_TIMEOUT);
    }

    public static void newInstance(String host, int port, int maxThreads, int minThreads, int threadTimeoutMillis) {
        TinyHandler tinyHandler = TinyHandler.get();
        JettyHandler jettyHandler = new JettyHandler(tinyHandler);
        QueuedThreadPool queuedThreadPool = new QueuedThreadPool(minThreads, maxThreads, threadTimeoutMillis);
        queuedThreadPool.setDetailedDump(false);
        Server server = new Server(queuedThreadPool);
        ServerConnector connector = getConnector(server, host, port);
        connector.setAcceptQueueSize(1000);
        server = connector.getServer();
        server.setConnectors(new Connector[]{connector});
        server.setHandler(jettyHandler);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }


    public static void newExecutorInstance(String host, int port) {
        TinyHandler tinyHandler = TinyHandler.get();
        JettyHandler jettyHandler = new JettyHandler(tinyHandler);
        Server server = new Server(new ExecutorThreadPool(Executors.newWorkStealingPool()));
        ServerConnector connector = getConnector(server, host, port);
        server = connector.getServer();
        server.setConnectors(new Connector[]{connector});
        server.setHandler(jettyHandler);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }


    private static ServerConnector getConnector(Server server, String host, int port) {
        ServerConnector connector = new ServerConnector(server);

        connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
        connector.setSoLingerTime(-1);
        connector.setHost(host);
        connector.setPort(port);

        return connector;
    }

}
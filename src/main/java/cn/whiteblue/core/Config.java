package cn.whiteblue.core;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/12
 */
public class Config {
    private static String APP_HOST;
    private static int APP_PORT;

    private static String DB_HOST;
    private static int DB_PORT;
    private static String DB_NAME;
    private static String DB_USER;
    private static String DB_PASS;

    private static String REDIS_HOST;
    private static int REDIS_PORT;

    public static void configHOST(String host, int port) {
        APP_HOST = host;
        APP_PORT = port;
    }

    public static void configDB(String host, int port, String database, String name, String password) {
        DB_HOST = host;
        DB_PORT = port;
        DB_NAME = database;
        DB_USER = name;
        DB_PASS = password;
    }

    public static void configRedis(String host, int port) {
        REDIS_HOST = host;
        REDIS_PORT = port;
    }

    public static String getAppHost() {
        return APP_HOST;
    }

    public static void setAppHost(String appHost) {
        APP_HOST = appHost;
    }

    public static int getAppPort() {
        return APP_PORT;
    }

    public static void setAppPort(int appPort) {
        APP_PORT = appPort;
    }

    public static String getDbHost() {
        return DB_HOST;
    }

    public static void setDbHost(String dbHost) {
        DB_HOST = dbHost;
    }

    public static int getDbPort() {
        return DB_PORT;
    }

    public static void setDbPort(int dbPort) {
        DB_PORT = dbPort;
    }

    public static String getDbName() {
        return DB_NAME;
    }

    public static void setDbName(String dbName) {
        DB_NAME = dbName;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static void setDbUser(String dbUser) {
        DB_USER = dbUser;
    }

    public static String getDbPass() {
        return DB_PASS;
    }

    public static void setDbPass(String dbPass) {
        DB_PASS = dbPass;
    }

    public static String getRedisHost() {
        return REDIS_HOST;
    }

    public static void setRedisHost(String redisHost) {
        REDIS_HOST = redisHost;
    }

    public static int getRedisPort() {
        return REDIS_PORT;
    }

    public static void setRedisPort(int redisPort) {
        REDIS_PORT = redisPort;
    }
}

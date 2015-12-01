package cn.whiteblue.core;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import java.util.Properties;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/9
 */
public class DataSourceFactory {

    private static BasicDataSource dataSource = null;

    //数据库连接驱动
    private String driverClassName;
    //基本连接信息
    private String connectUrl;
    private String username;
    private String password;

    //并发数
    private String maxActive = "400";
    //最大空闲数
    private String maxIdle = "20";
    //最大建立连接等待时间
    private String maxWait = "1100";
    //回收废弃连接
    private String removeAbandoned = "true";
    //回收时间
    private String removeAbandonedTimeout = "3";


    private DataSourceFactory(String driverClassName, String connectUrl, String username, String password) {
        this.driverClassName = driverClassName;
        this.connectUrl = connectUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * 简单初始化
     *
     * @param url      连接url
     * @param username 用户名
     * @param password 密码
     * @return DataSourceFactory
     */
    public static DataSourceFactory configuration(String url, String username, String password) {
        return new DataSourceFactory("com.mysql.jdbc.Driver", url, username, password);
    }


    /**
     * 初始化连接池
     */
    public void init() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Exception ignored) {
            }
            dataSource = null;
        }

        try {
            Properties p = new Properties();
            p.setProperty("driverClassName", this.driverClassName);
            p.setProperty("url", this.connectUrl);
            p.setProperty("username", this.username);
            p.setProperty("password", this.password);

            p.setProperty("maxActive", this.maxActive);
            p.setProperty("maxIdle", this.maxIdle);
            p.setProperty("maxWait", this.maxWait);
            p.setProperty("removeAbandoned", this.removeAbandoned);
            p.setProperty("removeAbandonedTimeout", this.removeAbandonedTimeout);
            p.setProperty("logAbandoned", "false");

//            p.setProperty("testOnBorrow", "true");连接验证..先关掉吧

            dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static BasicDataSource get() {
        return dataSource;
    }
}

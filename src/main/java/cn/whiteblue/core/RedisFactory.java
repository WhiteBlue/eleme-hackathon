package cn.whiteblue.core;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/9
 */
public class RedisFactory {

    private static JedisPool pool;

    private RedisFactory() {
    }

    public static void init(String host, int port) {
        JedisPoolConfig config = new JedisPoolConfig();

        // 设置最大阻塞时间，毫秒数milliseconds
        config.setMaxWaitMillis(1000);

        config.setMaxTotal(-1);

        config.setMaxIdle(50);

        pool = new JedisPool(config, host, port);
    }

    public static Jedis get() {
        return pool.getResource();
    }

}

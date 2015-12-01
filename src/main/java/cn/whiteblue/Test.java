package cn.whiteblue;

import cn.whiteblue.core.DataSourceFactory;
import cn.whiteblue.core.RedisFactory;
import cn.whiteblue.utils.TokenUtil;
import redis.clients.jedis.Jedis;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/25
 */
public class Test {

    public static void main(String args[]) {

        DataSourceFactory dataSourceFactory = DataSourceFactory.configuration("jdbc:mysql://localhost/eleme", "root", "");
        dataSourceFactory.init();
        RedisFactory.init("localhost", 6379);


        try (Jedis jedis = RedisFactory.get()) {
//            String script = "if 3<2 then return 0 end";
//
//            long back = (long) jedis.eval(script);

            String back = jedis.get("233");

            System.out.println(back == null);

        }


        System.out.println(TokenUtil.generate("dasd"));

    }

}

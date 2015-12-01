package cn.whiteblue;

import cn.whiteblue.core.Const;
import cn.whiteblue.core.DataSourceFactory;
import cn.whiteblue.core.RedisFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/13
 */
public class CoreService {

    QueryRunner queryRunner;

    private Map<Integer, String> scriptSHA;
    private Map<Integer, String> foodPrice;
    private Map<String, User> userCache;

    private CoreService(QueryRunner queryRunner) {
        this.queryRunner = queryRunner;
        this.scriptSHA = new HashMap<>();
        this.foodPrice = new HashMap<>();
        this.userCache = new HashMap<>();
    }

    public static CoreService get() {
        return new CoreService(new QueryRunner(DataSourceFactory.get()));
    }


    /**
     * 脚本缓存
     */
    public void generateScript() {
        try (Jedis jedis = RedisFactory.get()) {
            for (int i = 1; i <= 3; i++) {
                StringBuilder selectBuilder = new StringBuilder("if");
                StringBuilder updateBuilder = new StringBuilder(" then");
                for (int j = 1; j <= i; j++) {
                    selectBuilder.append(" tonumber(redis.call('get',KEYS[").append(j).append("])) >= tonumber(ARGV[").append(j).append("]) and");
                    updateBuilder.append(" redis.call('decrby',KEYS[").append(j).append("],ARGV[").append(j).append("])");
                }
                updateBuilder.append(" return 0 end");
                String script = selectBuilder.substring(0, selectBuilder.length() - 3) + updateBuilder.substring(0);
                String sha = jedis.scriptLoad(script);
                this.scriptSHA.put(i, sha);
            }
        }
    }


    /**
     * 清空并刷新缓存
     *
     * @throws SQLException
     */
    public void refreshCache() throws SQLException {
        //清空redis
        try (Jedis jedis = RedisFactory.get()) {
            jedis.flushAll();

            List<Map<String, Object>> foodsMap = this.getFoods();
            List<User> userList = this.getUsers();

            for (User user : userList) {
                this.userCache.put(user.getName(), user);
            }

            for (Map<String, Object> map : foodsMap) {
                jedis.set(Const.FOOD_STOCKS + map.get("id").toString(), map.get("stock").toString());
                this.foodPrice.put(Integer.valueOf(map.get("id").toString()), map.get("price").toString());
//                jedis.set(Const.FOOD_STOCKS_CACHE + map.get("id").toString(), map.get("stock").toString());
            }
        }
    }

    /**
     * 登陆 By Mysql
     *
     * @param username 用户名
     * @param password 密码
     * @return userId
     * @throws SQLException
     */
    public String auth(String username, String password) throws SQLException {
        DataSource dataSource = DataSourceFactory.get();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM user WHERE name = ? AND password = ? LIMIT 1");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.first()) {
                return String.valueOf(resultSet.getInt(1));
            } else {
                return null;
            }
        }
    }


    public String authByCache(String username, String password) {
        User user = this.userCache.get(username);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                return String.valueOf(user.getId());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 旧方法订单处理 => JDBC事务+批量更新
     *
     * @param bracketMap bracket
     * @return boolean
     * @throws Exception
     */
    @Deprecated
    public boolean doOrder(Map<String, String> bracketMap) throws Exception {
        DataSource dataSource = DataSourceFactory.get();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE food SET stock = stock - ? WHERE id= ? AND stock >= ? LIMIT 1;");
            for (String key : bracketMap.keySet()) {
                int foodId = Integer.valueOf(key);
                int take = Integer.valueOf(bracketMap.get(key));

                updateStatement.setInt(1, take);
                updateStatement.setInt(2, foodId);
                updateStatement.setInt(3, take);
                updateStatement.addBatch();

            }
            int[] result = updateStatement.executeBatch();
            for (int each : result) {
                if (each == 0) {
                    throw new SQLException();
                }
            }
            connection.commit();
            return true;
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            return false;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }


    /**
     * 订单处理,基于redis script(sha)
     *
     * @param bracketMap bracket
     * @param jedis      redis连接
     * @return boolean
     */
    public boolean doOrderRedis(Map<String, String> bracketMap, Jedis jedis) {
        int size = bracketMap.size();
        String sha = this.scriptSHA.get(size);

        List<String> keyList = new ArrayList<>();
        List<String> paramList = new ArrayList<>();

        for (String key : bracketMap.keySet()) {
            keyList.add(Const.FOOD_STOCKS + key);
            paramList.add(bracketMap.get(key));
        }

        Object back = jedis.evalsha(sha, keyList, paramList);

        return back != null;
    }


    /**
     * 当无竞争情况时下单
     *
     * @param bracketMap bracket
     * @param jedis      redis连接
     */
    public void doOrderNotSafe(Map<String, String> bracketMap, String userId, String bracketId, Jedis jedis) {
        int size = bracketMap.size();
        String sha;
        if (this.scriptSHA.get(size) == null) {
            StringBuilder updateBuilder = new StringBuilder();
            for (int i = 1; i <= size; i++) {
                updateBuilder.append(" redis.call('decrby',KEYS[").append(i).append("],ARGV[").append(i).append("])");
            }
            sha = jedis.scriptLoad(updateBuilder.substring(1));
            this.scriptSHA.put(3 + size, sha);
        } else {
            sha = this.scriptSHA.get(3 + size);
        }
        List<String> keyList = new ArrayList<>();
        List<String> paramList = new ArrayList<>();

        for (String key : bracketMap.keySet()) {
            keyList.add(Const.FOOD_STOCKS + key);
            paramList.add(bracketMap.get(key));
        }
        Pipeline pipeline = jedis.pipelined();
        pipeline.evalsha(sha, keyList, paramList);
        pipeline.hdel(Const.BRACKET_ID_BUFFER, bracketId);
        pipeline.hset(Const.ORDER_ID_BUFFER, userId, bracketId);
        pipeline.sync();
    }

    /**
     * 从Mysql得到Food
     *
     * @param foodId id
     * @return map
     * @throws SQLException
     */
    @Deprecated
    public Map<String, Object> getFood(int foodId) throws SQLException {
        return queryRunner.query("select * from food where id = ? LIMIT 1", new MapHandler(), foodId);
    }


    /**
     * 从Mysql取得Food list
     *
     * @return List<Map>
     * @throws SQLException
     */
    public List<Map<String, Object>> getFoods() throws SQLException {
        return queryRunner.query("select * from food", new MapListHandler());
    }

    public List<User> getUsers() throws SQLException {
        return queryRunner.query("select * from user", new BeanListHandler<>(User.class));
    }


    /**
     * 从Redis取得Food List(拼接处理耗费高)
     *
     * @param jedis redis连接
     * @return List
     */
    public List<Map<String, Object>> getFoodsRedis(Jedis jedis) {
        List<Map<String, Object>> returnList = new ArrayList<>();

        for (int key : this.foodPrice.keySet()) {
            Map<String, Object> mapInner = new HashMap<>();
            mapInner.put("id", key);
            mapInner.put("stock", Integer.valueOf(jedis.get(Const.FOOD_STOCKS + key)));
            mapInner.put("price", Integer.valueOf(this.foodPrice.get(key)));
            returnList.add(mapInner);
        }
        return returnList;
    }


    /**
     * 从本地缓存取出食物价格
     *
     * @param foodId foodId
     * @return int
     */
    public String getFoodPrice(int foodId) {
        return this.foodPrice.get(foodId);
    }


}

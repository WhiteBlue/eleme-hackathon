package cn.whiteblue;

import cn.shiroblue.Catch;
import cn.shiroblue.Route;
import cn.shiroblue.Tiny;
import cn.shiroblue.core.RenderFactory;
import cn.shiroblue.http.Request;
import cn.whiteblue.core.Config;
import cn.whiteblue.core.Const;
import cn.whiteblue.core.DataSourceFactory;
import cn.whiteblue.core.RedisFactory;
import cn.whiteblue.exceptions.AuthException;
import cn.whiteblue.exceptions.NullRequestException;
import cn.whiteblue.utils.HttpUtil;
import cn.whiteblue.utils.JsonUtil;
import cn.whiteblue.utils.TokenUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.xml.bind.ValidationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/8
 */
public class Main {

    public static void main(String args[]) {
        init();

        CoreService coreService = CoreService.get();

        try {
            coreService.refreshCache();
            coreService.generateScript();
        } catch (SQLException e) {
            System.out.println("Error : Cache refresh failed...exit");
            System.exit(0);
        }

        Route.before("/*", ((request, response) -> response.type("application/json")));

        Route.get("/", (request, response) -> JsonUtil.makeMsg("success", "project hackathon by 'Tiny-Service1.1'"));

        //登陆
        Route.post("/login", (request, response) -> {
            JSONObject jsonObject = HttpUtil.parseJson(request);
            String username = HttpUtil.getStrOrDie(jsonObject, "username");
            String password = HttpUtil.getStrOrDie(jsonObject, "password");

            String userId = coreService.authByCache(username, password);
            if (userId != null) {
                String token = TokenUtil.generate(userId);
                try (Jedis jedis = RedisFactory.get()) {
                    jedis.set(Const.TOKENS + token, userId);
                }
                JSONObject returnObject = new JSONObject();
                returnObject.put("user_id", Integer.valueOf(userId));
                returnObject.put("username", username);
                returnObject.put("access_token", token);
                return returnObject;
            } else {
                response.status(403);
                return JsonUtil.makeMsg("USER_AUTH_FAIL", "用户名或密码错误");
            }
        });

        //所有食物(缓存3sec)
        Route.get("/foods", (request, response) -> {
            try (Jedis jedis = RedisFactory.get()) {
                authByToken(request, jedis);

                String value = jedis.get(Const.FOOD_LIST_CACHE);
                if (value == null) {
                    List<Map<String, Object>> foodMap = coreService.getFoods();
                    value = JSON.toJSONString(foodMap);
                    jedis.setex(Const.FOOD_LIST_CACHE, 3, value);
                }
                return value;
            }
        }, Object::toString);

        //新建篮子
        Route.post("/carts", ((request, response) -> {
            String bracketId = null;

            try (Jedis jedis = RedisFactory.get()) {
                String userId = authByToken(request, jedis);
                bracketId = TokenUtil.generate();

                jedis.hset(Const.BRACKET_ID_BUFFER, bracketId, userId);
            }
            JSONObject returnObject = new JSONObject();
            returnObject.put("cart_id", bracketId);
            return returnObject;
        }));

        //添加食物(最优6条redis查询)
        Route.patch("/carts/:cart_id", ((request, response) -> {
            String bracketId = request.pathParam("cart_id");
            JSONObject jsonObject = HttpUtil.parseJson(request);
            int foodId = HttpUtil.getIntOrDie(jsonObject, "food_id");
            int count = HttpUtil.getIntOrDie(jsonObject, "count");

            String foodPrice = coreService.getFoodPrice(foodId);
            if (foodPrice == null) {
                response.status(404);
                return JsonUtil.makeMsg("FOOD_NOT_FOUND", "食物不存在");
            }

            try (Jedis jedis = RedisFactory.get()) {
                String userId = authByToken(request, jedis);
                String foodIdStr = String.valueOf(foodId);

                Pipeline pipelineBefore = jedis.pipelined();
                pipelineBefore.hget(Const.BRACKET_ID_BUFFER, bracketId);
                pipelineBefore.get(Const.BRACKET_SIZES + bracketId);

                List<Object> back = pipelineBefore.syncAndReturnAll();

                String bracketUserId = (String) back.get(0);
                if (bracketUserId == null) {
                    response.status(404);
                    return JsonUtil.makeMsg("CART_NOT_FOUND", "篮子不存在");
                }
                if (!userId.equals(bracketUserId)) {
                    response.status(401);
                    return JsonUtil.makeMsg("NOT_AUTHORIZED_TO_ACCESS_CART", "无权限访问指定的篮子");
                }

                int bracketCount = (back.get(1) == null) ? 0 : Integer.valueOf((String) back.get(1));
                if (bracketCount + count > 3) {
                    response.status(403);
                    return JsonUtil.makeMsg("FOOD_OUT_OF_LIMIT", "篮子中食物数量超过了三个");
                }

                //二阶管道
                Pipeline pipeline = jedis.pipelined();
                pipeline.hincrBy(Const.BRACKETS + bracketId, foodIdStr, count);
                pipeline.incrBy(Const.ORDER_PRICES + bracketId, count * Integer.valueOf(foodPrice));
                pipeline.incrBy(Const.BRACKET_SIZES + bracketId, count);
                pipeline.sync();

                //若竞争则添加set
//                long countBack = jedis.decrBy(Const.FOOD_STOCKS_CACHE + foodIdStr, count);
//                if (countBack <= Const.dangerLimit) {
//                    jedis.sadd(Const.OVER_FOODS_BUFFER, foodIdStr);
//                }
            }

            response.status(204);
            return null;
        }));

        //下单(最优6+n条普通redis查询)
        Route.post("/orders", (request, response) -> {
            JSONObject jsonObject = HttpUtil.parseJson(request);
            String bracketId = HttpUtil.getStrOrDie(jsonObject, "cart_id");

            try (Jedis jedis = RedisFactory.get()) {
                String userId = authByToken(request, jedis);
                //前置管道
                Pipeline pipelineBefore = jedis.pipelined();
                pipelineBefore.hexists(Const.ORDER_ID_BUFFER, userId);
                pipelineBefore.hget(Const.BRACKET_ID_BUFFER, bracketId);
                pipelineBefore.hgetAll(Const.BRACKETS + bracketId);
//                pipelineBefore.smembers(Const.OVER_FOODS_BUFFER);

                List<Object> returnBefore = pipelineBefore.syncAndReturnAll();

                if ((boolean) returnBefore.get(0)) {
                    response.status(403);
                    return JsonUtil.makeMsg("ORDER_OUT_OF_LIMIT", "每个用户只能下一单");
                }

                String bracketUserId = (String) returnBefore.get(1);
                if (bracketUserId == null) {
                    response.status(404);
                    return JsonUtil.makeMsg("CART_NOT_FOUND", "篮子不存在");
                }
                if (!bracketUserId.equals(userId)) {
                    response.status(401);
                    return JsonUtil.makeMsg("NOT_AUTHORIZED_TO_ACCESS_CART", "无权限访问指定的篮子");
                }

                Map<String, String> bracketMap = (Map<String, String>) returnBefore.get(2);
//                Set<String> dangers = (Set<String>) returnBefore.get(3);

                //取交集判断
//                dangers.retainAll(bracketMap.keySet());

                //安全时下单
//                    coreService.doOrderNotSafe(bracketMap, userId, bracketId, jedis);
//
//                    JSONObject returnObject = new JSONObject();
//                    returnObject.put("id", bracketId);
//                    return returnObject;
                //竞争时下单
                if (coreService.doOrderRedis(bracketMap, jedis)) {
                    Pipeline pipeline = jedis.pipelined();
                    pipeline.hdel(Const.BRACKET_ID_BUFFER, bracketId);
                    pipeline.hset(Const.ORDER_ID_BUFFER, userId, bracketId);
                    pipeline.sync();

                    JSONObject returnObject = new JSONObject();
                    returnObject.put("id", bracketId);
                    return returnObject;
                } else {
                    response.status(403);
                    return JsonUtil.makeMsg("FOOD_OUT_OF_STOCK", "食物库存不足");
                }
            }
        });

        //订单查询
        Route.get("/orders", (request, response) -> {
            JSONArray returnArray = new JSONArray();

            try (Jedis jedis = RedisFactory.get()) {
                String userId = authByToken(request, jedis);

                if (jedis.hexists(Const.ORDER_ID_BUFFER, userId)) {
                    JSONObject returnObject = new JSONObject();
                    String bracketId = jedis.hget(Const.ORDER_ID_BUFFER, userId);

                    Map<String, String> bracketMap = jedis.hgetAll(Const.BRACKETS + bracketId);

                    JSONArray itemArray = getItemForBracket(bracketMap);

                    String price = jedis.get(Const.ORDER_PRICES + bracketId);
                    returnObject.put("id", bracketId);
                    returnObject.put("items", itemArray);
                    returnObject.put("total", Integer.valueOf(price));
                    returnArray.add(returnObject);
                }
            }
            return returnArray;
        });

        //管理员订单查询
        Route.get("/admin/orders", (request, response) -> {
            JSONArray returnArray = new JSONArray();

            try (Jedis jedis = RedisFactory.get()) {
                String userId = authByToken(request, jedis);
                if (!userId.equals("1")) {
                    throw new AuthException();
                }

                Map<String, String> orderMap = jedis.hgetAll(Const.ORDER_ID_BUFFER);

                for (String ownerId : orderMap.keySet()) {
                    JSONObject returnObject = new JSONObject();
                    String bracketId = jedis.hget(Const.ORDER_ID_BUFFER, ownerId);

                    Map<String, String> bracketMap = jedis.hgetAll(Const.BRACKETS + bracketId);

                    JSONArray itemArray = getItemForBracket(bracketMap);

                    String price = jedis.get(Const.ORDER_PRICES + bracketId);
                    returnObject.put("id", bracketId);
                    returnObject.put("user_id", Integer.valueOf(ownerId));
                    returnObject.put("items", itemArray);
                    returnObject.put("total", Integer.valueOf(price));
                    returnArray.add(returnObject);
                }
            }
            return returnArray;
        });

        Catch.exception(JSONException.class, (e, request, response) -> {
            response.status(400);
            response.body(JSON.toJSONString(JsonUtil.makeMsg("MALFORMED_JSON", "格式错误")));
        });

        Catch.exception(ValidationException.class, (e, request, response) -> {
            response.status(400);
            response.body(JSON.toJSONString(JsonUtil.makeMsg("MALFORMED_JSON", "格式错误")));
        });

        Catch.exception(NullRequestException.class, (e, request, response) -> {
            response.status(400);
            response.body(JSON.toJSONString(JsonUtil.makeMsg("EMPTY_REQUEST", "请求体为空")));
        });

        Catch.exception(AuthException.class, (e, request, response) -> {
            response.status(401);
            response.body(JSON.toJSONString(JsonUtil.makeMsg("INVALID_ACCESS_TOKEN", "无效的令牌")));
        });

        Catch.exception(Exception.class, (e, request, response) -> {
            e.printStackTrace();
            response.status(500);
            response.body(JSON.toJSONString(JsonUtil.makeMsg("SERVER_ERROR", e.getMessage())));
        });

        Tiny.server(Config.getAppHost(), Config.getAppPort(), 500, 30, 2500);
    }

    /**
     * 应用初始化
     */
    private static void init() {
        try {
            //初始化环境变量
            getPath();

            //数据源初始化
            DataSourceFactory dataSourceFactory = DataSourceFactory.configuration("jdbc:mysql://" + Config.getDbHost() + "/" + Config.getDbName(), Config.getDbUser(), Config.getDbPass());
            dataSourceFactory.init();

            //Redis初始化
            RedisFactory.init(Config.getRedisHost(), Config.getRedisPort());

            //默认render
            RenderFactory.setDefaultRender(JSON::toJSONString);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

    /**
     * 取环境变量
     */
    public static void getPath() {
        String APP_HOST = System.getenv("APP_HOST");
        String APP_PORT = System.getenv("APP_PORT");

        String DB_HOST = System.getenv("DB_HOST");
        String DB_PORT = System.getenv("DB_PORT");
        String DB_NAME = System.getenv("DB_NAME");
        String DB_USER = System.getenv("DB_USER");
        String DB_PASS = System.getenv("DB_PASS");

        String REDIS_HOST = System.getenv("REDIS_HOST");
        String REDIS_PORT = System.getenv("REDIS_PORT");

        Config.configHOST(APP_HOST, Integer.valueOf(APP_PORT));
        Config.configDB(DB_HOST, Integer.valueOf(DB_PORT), DB_NAME, DB_USER, DB_PASS);
        Config.configRedis(REDIS_HOST, Integer.valueOf(REDIS_PORT));
    }


    /**
     * 订单对象拼接(只是不想写两遍orz)
     *
     * @param bracketMap bracket
     * @return JSONArray
     */
    static JSONArray getItemForBracket(Map<String, String> bracketMap) {
        JSONArray itemArray = new JSONArray();
        for (String foodId : bracketMap.keySet()) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("food_id", Integer.valueOf(foodId));
            itemObject.put("count", Integer.valueOf(bracketMap.get(foodId)));
            itemArray.add(itemObject);
        }
        return itemArray;
    }


    /**
     * Token认证
     *
     * @param request 请求对象
     * @param jedis   Jedis
     * @return String
     * @throws AuthException
     */
    static String authByToken(Request request, Jedis jedis) throws AuthException {
        String token_param = request.queryParam("access_token");
        String token = (token_param == null) ? request.headers("Access-Token") : token_param;
        if (token == null) {
            throw new AuthException();
        }
        String userId = jedis.get(Const.TOKENS + token);

        if (userId == null) {
            throw new AuthException();
        } else {
            return userId;
        }
    }
}

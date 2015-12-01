package cn.whiteblue.core;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/10
 */
public class Const {
    //认证Token(key=>value)
    public static final String TOKENS = "tokens:";
    //购物篮ID(set)
    public static final String BRACKET_ID_BUFFER = "bracket_id";
    //订单ID(set)
    public static final String ORDER_ID_BUFFER = "order_id";
    //购物篮/订单价格计算(key=>value)
    public static final String ORDER_PRICES = "order_prices:";
    //购物篮/订单容量计算(key=>value)
    public static final String BRACKET_SIZES = "bracket_size:";
    //购物篮(hashs)
    public static final String BRACKETS = "brackets:";
    //食物库存(key=>value)
    public static final String FOOD_STOCKS = "food_stocks:";
    //食物库存缓存[用于判断竞争情况](key=>value)
//    public static final String FOOD_STOCKS_CACHE = "food_stocks_cache:";
    //有竞争情况的食物id(set)
//    public static final String OVER_FOODS_BUFFER = "over_foods";
    //食物列表缓存
    public static final String FOOD_LIST_CACHE = "food_list";

    //库存低过这个值视为危险
    public static int dangerLimit = 100;
}

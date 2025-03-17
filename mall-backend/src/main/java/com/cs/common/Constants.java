package com.cs.common;

// 常量
public class Constants {
    public final static String FILE_UPLOAD_DIC = "/Users/chengsen/Desktop/code/Java/mall-backend/src/main/resources/upload/";//上传文件的默认url前缀，根据部署设置自行修改
//    public final static String FILE_UPLOAD_DIC = "";//上传文件的默认url前缀，根据部署设置自行修改

    public final static int INDEX_CAROUSEL_NUMBER = 5;//首页轮播图数量(可根据自身需求修改)

    public final static int INDEX_CATEGORY_NUMBER = 10;//首页一级分类的最大数量

    public final static int INDEX_GOODS_HOT_NUMBER = 4;//首页热卖商品数量
    public final static int INDEX_GOODS_NEW_NUMBER = 5;//首页新品数量
    public final static int INDEX_GOODS_RECOMMOND_NUMBER = 10;//首页推荐商品数量

    public final static int SHOPPING_CART_ITEM_TOTAL_NUMBER = 50;//购物车中商品的最大数量(可根据自身需求修改)

    public final static int SHOPPING_CART_ITEM_LIMIT_NUMBER = 5;//购物车中单个商品的最大购买数量(可根据自身需求修改)

    public final static int GOODS_SEARCH_PAGE_LIMIT = 10;//搜索分页的默认条数(每页10条)

    public final static int SHOPPING_CART_PAGE_LIMIT = 5;//购物车分页的默认条数(每页5条)

    public final static int ORDER_SEARCH_PAGE_LIMIT = 5;//我的订单列表分页的默认条数(每页5条)

    public final static int SELL_STATUS_UP = 0;//商品上架状态
    public final static int SELL_STATUS_DOWN = 1;//商品下架状态

    public final static int TOKEN_LENGTH = 32;//token字段长度

    public final static String USER_INTRO = "随新所欲，蜂富多彩";//默认简介


    /**
     * 秒杀下单盐值
     */
    public static final String SECKILL_ORDER_SALT = "asdk23423@jjjas";

    public static final String REDIS_KEY_PREFIX = "cs-mall:";

    /**
     * 秒杀商品库存缓存
     */
    public static final String SECKILL_GOODS_STOCK_KEY = REDIS_KEY_PREFIX + "seckill_goods_stock:";

    /**
     * 秒杀商品缓存
     */
    public static final String SECKILL_KEY = REDIS_KEY_PREFIX + "seckill:";
    /**
     * 秒杀商品详情页面缓存
     */
    public static final String SECKILL_GOODS_DETAIL = REDIS_KEY_PREFIX + "seckill_goods_detail:";
    /**
     * 秒杀商品列表页面缓存
     */
    public static final String SECKILL_GOODS_LIST = REDIS_KEY_PREFIX + "seckill_goods_list";

    /**
     * 秒杀成功的用户set缓存
     */
    public static final String SECKILL_SUCCESS_USER_ID = REDIS_KEY_PREFIX + "seckill_success_user_id:";

}

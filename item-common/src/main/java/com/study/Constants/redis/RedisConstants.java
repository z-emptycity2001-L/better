package com.study.Constants.redis;

public class RedisConstants {

    /*===========================================redis lua 限流方案=========================================================*/
    public final static String ZSET_ONE_TIME_WINDOW_LIMIT_PRE_KEY="zaccess_1min:";//基于zset方式的时间滑动窗口限流方案key
    public final static String ZSET_Three_TIME_WINDOW_LIMIT_PRE_KEY="zaccess_3min:";//基于zset方式的时间滑动窗口限流方案key


    public final static String CONNECTOR_LIMITER_TOKEN_BUCKET_CURRENT_TOKEN_NUMBER_PRE_KEY="token_bucket_token_number:";//基于zset方式的时间滑动窗口限流方案key
    public final static String CONNECTOR_LIMITER_TOKEN_BUCKET_LAST_TOKEN_UPDATE_TIME_PRE_KEY="token_bucket_token_update_time:";//基于zset方式的时间滑动窗口限流方案key


    public final static String CONNECTOR_LIMITER_TOKEN_BUCKET_PRE_KEY="bucket_token_pre_key:";


    /*===========================================redis lua 限流方案=========================================================*/

    public final static String PRODUCT_OF_SHOP_PRE_KEY="shop:";//商铺详情缓存key
    public final static Long PRODUCT_OF_SHOP_EXPIRATION=1000*60*60L;//商铺详情缓存key的过期时间

    public final static String PRODUCT_DETAIL_PRE_KEY="product:";//商铺详情缓存key
    public final static Long PRODUCT_DETAIL_PRE_EXPIRATION=1000*60*60L;//商铺详情缓存key的过期时间
    public final static String  READ_AND_WRITER_FOR_SHOP="read-writer-lock-shop:";//商铺读写锁key

    public final static Long READ_AND_WRITER_FOR_SHOP_EXPIRATION=1000*60L*5;//商铺读写锁key的过期时间,五分钟

    public final static String PRODUCT_OF_SHOP_PRE_KEY_EMPTY="{}";//当数据库中不存在该商铺详情数据，为避免缓存击穿问题，设置空值

    public final static String PRODUCT_SEARCH_CACHE_KEY="setnx:shop";//商铺详情缓存分布式key
    public final static Long PRODUCT_SEARCH_CACHE_EXPIRATION=1000 * 60L;//商铺详情缓存key的过期时间

    public final static String  MAIL_FOR_CODE_KEY_PRE="mail-for-code:";//邮箱验证码
    public final static Long  MAIL_FOR_CODE_KEY_PRE_EXPIRE=1000*60*2L;//60s

    public final static String PASSWORD_IF_CAN_BE_CHANGE_KEY="passwordChangeIfValid:";
    public final static Long  PASSWORD_IF_CAN_BE_CHANGE_EXPIRE=1000*60*30L;//半小时

    public final static String PRODUCT_INFO_READ_WRITER_LOCK_KEY="readWriterLock:product:";//商品删改查 读写锁key
    public final static Long PRODUCT_INFO_READ_WRITER_LOCK_EXPIRE=1000*60*3L;//三分钟


    public final static String PRODUCT_INFO_CACHE_KEY="product:";//商品缓存key
    public final static Long PRODUCT_INFO_CACHE_EXPIRE=1000*60*60*24L;//一天

    public final static String PRODUCT_INFO_SEARCH_LOCK_KEY="lock:product:";//商品缓存key
    public final static Long PRODUCT_INFO_SEARCH_LOCK_EXPIRE=1000*60*3L;//三分钟

    public final static String REMARK_CACHE_KEY="remark:";//评论缓存key
    public final static Long REMARK_CACHE_KEY_EXPIRE=1000*60*5L;//五分钟

    public final static String REMARK_CACHE_READ_WRITE_LOCK_KEY="remark_read_write:";//评论读写锁key
    public final static Long REMARK_CACHE_READ_WRITE_EXPIRE=1000*60*3L;//三分钟


    /*=============================================product=========================================================*/
    public final static String MEDICAL_ITEM_PRE_KEY="medical_item:";//医疗体检项目key，存放在redis中作为缓存，不设置过期时间
    public final static String MEDICAL_ITEM_SETNX_PRE_KEY="medical_item_setnx:";//医疗体检项目分布式锁，当redis中找不到医疗体检项目时，为防止缓存击穿，需要使用锁
    public final static Long MEDICAL_ITEM_SETNX_EXPIRE=1000*60L;//医疗体检项目分布式锁的过期时间

    public final static String MEDICAL_PACKAGE_PRE_KEY="medical_package:";//医疗体检套餐key，存放在redis中作为缓存，不设置过期时间
    public final static String MEDICAL_PACKAGE_SETNX_PRE_KEY="medical_package_setnx:";//医疗体检套餐分布式锁，当redis中找不到医疗体检项目时，为防止缓存击穿，需要使用锁

    public final static String MEDICAL_PACKAGE_STOCK_PRE_KEY="medical_package_stock:";//医疗体检套餐库存key，存放在redis中作为缓存，不设置过期时间
    public final static String MEDICAL_PACKAGE_STOCK_WRITE_READ_LOCK_PRE_KEY="medical_package_stock_write_read_lock:";//医疗体检套餐库存读写锁，当redis中找不到医疗体检项目时，为防止缓存击穿，需要使用锁
    public final static String MEDICAL_PACKAGE_STOCK_LOCK_PRE_KEY="medical_package_stock_lock:";//医疗体检套餐库存分布式锁key，为防止缓存击穿，需要使用锁
    public final static Long MEDICAL_PACKAGE_STOCK_WRITE_READ_LOCK_EXPIRE=1000*60L;//医疗体检套餐库存读写锁，当redis中找不到医疗体检项目时，为防止缓存击穿，需要使用锁

    public final static String SEC_KILL_COUPON_STOCK_PREHEAT_KEY="stock:coupon:";//秒杀优惠券库存预热缓存key

    public final static String SEC_KILL_COUPON_USER_RECORD_KEY="coupon:user:record:";//一人一单的下单记录
    public static final String SEC_KILL_COUPON_INFO_PREHEAT_KEY = "coupon:user:info:";//秒杀优惠券的信息

    /*=============================================remark=========================================================*/

    public static final String REMARK_CACHE_FAVOR_PRE_KEY = "remark:";
    public static final String REMARK_CACHE_FAVOR_READ_WRITE_LOCK_PRE_KEY = "remark:readwrite:";
    public static final Long REMARK_CACHE_FAVOR_READ_WRITE_LOCK_EXPIRE = 1000*60L;
    public static final String REMARK_HOT_CACHE_PRE_KEY = "remark_hot:";
    public static final Long REMARK_HOT_CACHE_EXPIRE = 1000*60*60L;//1h



    /*=============================================order=========================================================*/
    public static final String PRE_ORDER_CREATE_KEY = "pre_order:";
    public static final Long PRE_ORDER_CREATE_EXPIRE = 1000*60*30L;//30min
    public static final String ORDER_ID_PRE_KEY = "order_id:";
    public static final Long ORDER_ID_EXPIRE = 1000*60*30L;//30min

    //订单幂等锁
    public static final Long PRE_ORDER_CREATE_DUPLICATE_EXPIRE = 1000L;
    //    用户购物车数据
    public static final String SHOPPING_TROLLEY_PRE_KEY = "shopping_trolley:";

    //    疾病分类信息分布式锁
    public static final String DISEASE_INFO_WITH_CATEGORY_ID_SETNX_PRE_KEY = "disease_info_category_id_setnx:";
    public static final Long DISEASE_INFO_WITH_CATEGORY_ID_SETNX_EXPIRE = 1000*60L;

    //    疾病分类信息
    public static final String DISEASE_INFO_WITH_CATEGORY_ID_PRE_KEY = "disease_info_with_category_id:";
    public static final Long DISEASE_INFO_WITH_CATEGORY_ID_EXPIRE = 1000*60*60*24L;//1天

    public static final String COUNT_REMARK_HOT_SCORE_PRE_KEY = "count_remark_hot_score:";
    public static final String HOT_REMARK_FLAG_PRE_KEY = "hot_remark_flag:";
    public static final String HOT_REMARK_CACHE_PRE_KEY = "hot_remark_cache:";
    public static final String HOT_REMARK_CACHE_LOCK_PRE_KEY = "hot_remark_cache_lock:";
    public static final Long HOT_REMARK_CACHE_LOCK_EXPIRE = 1000*30L;//三分钟过期时间

    /*=============================================follow=========================================================*/

    public static final String CON_FOLLOW_USER="follow:user:";
    public static final String USER_PUBLISH_ARTICLE_FOLLOWER = "user:article:publish:follower:";
}

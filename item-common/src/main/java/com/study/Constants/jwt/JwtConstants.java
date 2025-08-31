package com.study.Constants.jwt;

public class JwtConstants {
    //    jwt存储用户信息的hash key
    public static final String CLAIM_KEY_USER="user";

    //    jwt存储用户权限的hash key
    public static final String CLAIM_KEY_ROLE="role";
    //    jwt剩余时间的hash key
    public static final String CLAIM_KEY_CREATED="created";
    //    jwt生成所需的密钥
    public static final String SECRET ="STUDY";


    public final static String TOKEN_KEY="token: ";
    public final static Long TOKEN_EXPIRATION=1000*60*60*24*3L;//三天
    public final static Long TOKEN_REDIS_EXPIRATION=1000*60*60*3L;//3小时

    public final static String BLACK_LIST_TOKEN_PRE_KEY="token_black_list:";//token黑名单key

    public final static Long BLACK_LIST_TOKEN_EXPIRE=1000*60*60*24*2L;//两天
}

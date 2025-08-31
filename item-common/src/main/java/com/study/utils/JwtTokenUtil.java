package com.study.utils;

import com.study.Constants.jwt.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Component
    @Slf4j
    public class JwtTokenUtil {

        /*
         * 根据用户信息生成token
         * */
        public String generateToken(Long userId, Long expiration){
//        创建荷载
            Map<String,Object> claims= new HashMap<>();

            claims.put(JwtConstants.CLAIM_KEY_USER,userId);
//        token创建时间
            claims.put(JwtConstants.CLAIM_KEY_CREATED,new Date());

            //    根据荷载生成 JWT Token
            return generateToken(claims,expiration);
        }

        public String generateToken(Long userId, Long roleId, Long expiration){
//            创建荷载
            Map<String,Object> claims= new HashMap<>();
//            用户信息
            claims.put(JwtConstants.CLAIM_KEY_USER,userId);
//            用户权限信息
            claims.put(JwtConstants.CLAIM_KEY_ROLE,roleId);
//            token创建时间
            claims.put(JwtConstants.CLAIM_KEY_CREATED,new Date());

            //    根据荷载生成 JWT Token
            return generateToken(claims,expiration);
        }
        //    重载 generateToken 方法，生成token
        private String generateToken(Map<String,Object> claims,Long expiration){
            //    @Value("${jwt.expiration}")
            return Jwts.builder()
                    .setClaims(claims)//填入荷载
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))//设置生效时间
                    .signWith(SignatureAlgorithm.HS512, JwtConstants.SECRET )//设置密钥
                    .compact();
        }

        public Long getUserIdFromToken(String token){
            Long userId;
            try {
                Claims claimsFromToken = getClaimsFromToken(token);
                userId = Long.parseLong(claimsFromToken.get(JwtConstants.CLAIM_KEY_USER).toString());
            }catch (Exception e){
                throw new RuntimeException(e);
            }
            return userId;
        }


        /*
         * 通过 token 获取 荷载
         * */
        public Claims getClaimsFromToken(String token) {
            Claims claims=null;
            try {
                claims = Jwts.parser()
                        .setSigningKey(JwtConstants.SECRET)
                        .parseClaimsJws(token)
                        .getBody();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return claims;
        }


        /**
         * 判断token是否有效
         **/
        public Boolean validateToken(String token,Long userId){
            Long userIdFromToken = getUserIdFromToken(token);
            if(!userIdFromToken.equals(userId))
                return false;
//        1、获取荷载
            Claims claims = getClaimsFromToken(token);
            if(Objects.isNull(claims)){
                throw new RuntimeException("登录过期啦！！！");
            }
//        2、通过荷载拿到token设置时的时间
            Date setExpirationTime = claims.getExpiration();

//        3、如果传入的用户名和token里的用户名一致并且没有过期   返回true
            return !setExpirationTime.before(new Date());
        }

        /*
         * 刷新token
         * */
        public String refresh(String token){
            Claims claims = getClaimsFromToken(token);
//          重新生成 access_token
            claims.put(JwtConstants.CLAIM_KEY_CREATED,new Date());
            String new_token = generateToken(claims, JwtConstants.TOKEN_EXPIRATION);
            log.warn("刷新后的token与之前的旧token是否一致： "+ token.equals(new_token));
            return new_token;
        }

        /*
         判断 token 是否可刷新
         */
        public Boolean canRefresh(String token){
            Date expiration = getClaimsFromToken(token).getExpiration();
            return !expiration.before(new Date());
        }
    }


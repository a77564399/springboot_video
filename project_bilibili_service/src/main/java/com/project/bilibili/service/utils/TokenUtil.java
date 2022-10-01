package com.project.bilibili.service.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.bilibili.exception.ConditionException;

import java.util.Calendar;

public class TokenUtil {
//    设置签发者
    private static final String ISSUER = "签发者";
    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        System.out.println(userId);
//        使用userid创建每个用户的token
        return JWT.create().withKeyId(String.valueOf(userId))
//               设置签发者
                .withIssuer(ISSUER)
//               设置过期时间（当前系统时间+30s）
                .withExpiresAt(calendar.getTime())
//                使用RSA加密签发JWT
                .sign(algorithm);
    }

//  生成刷新Token
    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
//      失效时间改为7天
        calendar.add(Calendar.DAY_OF_MONTH,7);
        System.out.println(userId);
//        使用userid创建每个用户的token
        return JWT.create().withKeyId(String.valueOf(userId))
//               设置签发者
                .withIssuer(ISSUER)
//               设置过期时间（当前系统时间+30s）
                .withExpiresAt(calendar.getTime())
//                使用RSA加密签发JWT
                .sign(algorithm);
    }

    public static Long vertifyToken(String token) {
//        验证token时会有token过期等正常的异常情况，需要进行友好提示
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
//           构造验证方法
            JWTVerifier verifier = JWT.require(algorithm).build();
//           解密JWT
            DecodedJWT jwt = verifier.verify(token);
//          通过jwt获取用户ID
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
//      捕获jwt提供的token过期异常
        }catch (TokenExpiredException e){
//            token过期，友好提示
            throw new ConditionException("555","token过期异常！");
        }
        catch (Exception e)
        {
            throw new ConditionException("非法用户token！");

        }
    }


}

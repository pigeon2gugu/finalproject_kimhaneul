package com.example.mutsa_sns.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

    private static Claims extractClaims(String token, String key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date()); // 현재보다 전인지 check. true : 만료됨
    }

    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName").toString();
    }
    public static String createToken(String userName, String key, long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName); //map형태에 userName저장

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) //생성 일자
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs)) //만료 일자
                .signWith(SignatureAlgorithm.HS256, key) //secret key
                .compact();
    }
}

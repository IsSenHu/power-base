package com.cdsen.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * @author HuSen
 * create on 2019/10/15 10:04
 */
public class JwtParseUtils {

    public static String getUsernameFromToken(String secret, String token) {
        String username = null;
        try {
            Claims claims = getClaimsFromToken(secret, token);
            // claims 可以获得Token的过期时间
            username = claims.getSubject();
        } catch (Exception ignored) {}
        return username;
    }

    private static Claims getClaimsFromToken(String secret, String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception ignored) {}
        return claims;
    }
}

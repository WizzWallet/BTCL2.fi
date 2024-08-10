package com.wizz.fi.sso;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizz.fi.util.DesUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

/**
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的类型）：
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、创建时间、生成时间）：
 * {"sub":"wang","created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 * Created by macro on 2018/4/26.
 */

@Component
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.signatureAlgorithm}")
    private String signatureAlgorithm;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 根据负载生成JWT的token
     */
    @SneakyThrows
    public String generateToken(Map<String, Object> claims) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", signatureAlgorithm);
        header.put("typ", "JWT");
        final String key = objectMapper.writeValueAsString(claims);
        final Map<String, Object> encrypt = new HashMap<>();
        encrypt.put("k", DesUtils.encrypt(key, secret));
        return Jwts.builder()
                .setHeader(header)
                .setClaims(encrypt)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.forName(signatureAlgorithm), TextCodec.BASE64.encode(secret)) //,secret  TextCodec.BASE64.encode("my-secret")
                .compact();
    }

    /**
     * 从token中获取JWT中的负载
     */
    public Map<String, Object> getClaimsFromToken(String token) {
        Claims claims;
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(TextCodec.BASE64.encode(secret)),
                SignatureAlgorithm.forName(signatureAlgorithm).getJcaName());
        try {
            claims = Jwts.parser()
                    .setSigningKey(hmacKey)
                    .parseClaimsJws(token)
                    .getBody();
            if (isTokenValid(claims)) {
                final String k = (String) claims.get("k");
                final String decrypt = DesUtils.decrypt(k, secret);
                return objectMapper.readValue(decrypt, new TypeReference<Map<String, Object>>() {
                });
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("exception is :{}", e.getMessage());
            LOGGER.info("JWT格式验证失败:{}", token);
            return null;
        }
    }


    /**
     * 生成token的过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }


    /**
     * 判断token是否已经失效
     */
    private boolean isTokenValid(Claims claims) {
        Date expiredDate = claims.getExpiration();
        return expiredDate.after(new Date());
    }
}

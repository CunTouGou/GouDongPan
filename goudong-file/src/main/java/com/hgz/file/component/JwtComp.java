package com.hgz.file.component;

import com.hgz.common.util.math.CalculatorUtils;
import com.hgz.file.config.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

/**
 * jwt处理组件
 *
 * @author CunTouGou
 * @date 2022/4/21 15:01
 */
@Component
public class JwtComp {

    @Resource
    JwtProperties jwtProperties;


    /**
     * 由字符串生成加密key
     *
     * @return SecretKey
     */
    private SecretKey generalKey() {
        // 本地的密码解码
        byte[] encodedKey = Base64.decodeBase64(jwtProperties.getSecret());
        // 根据给定的字节数组使用AES加密算法构造一个密钥
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    /**
     * 创建jwt
     *
     * @param subject 主题
     * @return String
     * @throws Exception Exception
     */
    public String createJWT(String subject) throws Exception {

        // 生成JWT的时间
        long nowTime = System.currentTimeMillis();
        Date nowDate = new Date(nowTime);
        // 生成签名的时候使用的秘钥secret，切记这个秘钥不能外露，是你服务端的私钥，在任何场景都不应该流露出去，一旦客户端得知这个secret，那就意味着客户端是可以自我签发jwt的
        SecretKey key = generalKey();
        Double expireTime = CalculatorUtils.conversion(jwtProperties.getPayload().getRegisterdClaims().getExp());

        // 为payload添加各种标准声明和私有声明
        DefaultClaims defaultClaims = new DefaultClaims();
        defaultClaims.setIssuer(jwtProperties.getPayload().getRegisterdClaims().getIss());
        defaultClaims.setExpiration(new Date(System.currentTimeMillis() + expireTime.longValue()));
        defaultClaims.setSubject(subject);
        defaultClaims.setAudience(jwtProperties.getPayload().getRegisterdClaims().getAud());

        // 表示new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                .setClaims(defaultClaims)
                // iat(issuedAt)：jwt的签发时间
                .setIssuedAt(nowDate)
                // 设置签名，使用的是签名算法和签名使用的秘钥
                .signWith(SignatureAlgorithm.forName(jwtProperties.getHeader().getAlg()), key);

        return builder.compact();
    }

    /**
     * 解密jwt
     *
     * @param jwt jwt
     * @return Claims
     * @throws Exception Exception
     */
    public Claims parseJWT(String jwt) throws Exception {
        // 签名秘钥，和生成的签名的秘钥一模一样
        SecretKey key = generalKey();
        // 得到DefaultJwtParser
        return Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(key)
                // 设置需要解析的jwt
                .parseClaimsJws(jwt).getBody();
    }
}
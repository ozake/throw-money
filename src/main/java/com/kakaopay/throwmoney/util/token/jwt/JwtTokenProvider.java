package com.kakaopay.throwmoney.util.token.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.throwmoney.common.exception.ApiException;
import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.dto.type.token.TokenType;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider { // JWT 토큰을 생성 및 검증 모듈

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private long tokenPickUPValidMilisecond = 1000 * 60 * 10; // 받기 10분 유효 기간 설정
    private long tokenReadVaildMilisecond = 1000 * 60 * 60 * 24 * 7; // 조회7일 유효기간 설정

    @Autowired
    private final ObjectMapper mapper;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 받기 Jwt 토큰 생성
    public String createTokenForPickUp(String keyToken, Identify identify, Long throwId) {
        return createJWT(keyToken, identify.getUserId(), identify.getRoomId(), tokenPickUPValidMilisecond, throwId, TokenType.PICK_UP);
    }

    // 조회용 jwt 토큰 생성
    public String createTokenForThrowRead(String keyToken, Identify identify, Long throwId) {
        return createJWT(keyToken, identify.getUserId(), identify.getRoomId(), tokenReadVaildMilisecond, throwId, TokenType.THROW_READ);
    }

    @SneakyThrows
    private String createJWT(String keyToken, Long userId, Long roomId, long expiredTime, Long throwId, TokenType tokenType) {
        String subject = keyToken + "-" + userId.toString() + "-" + roomId.toString();
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("keyToken", keyToken);
        claims.put("type", tokenType.name());
        claims.put("throwId", throwId);
        claims.put("userId", userId);
        claims.put("roomId", roomId);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiredTime);
        return Jwts.builder()
            .setClaims(claims) // 데이터
            .setIssuedAt(now) // 토큰 발행일자
            .setExpiration(expiration) // set Expire Time
            .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
            .compact();
    }

    public Long getThrowId(String jwtToken) {
        try {
            Jws<Claims> claims = parseClaimsJws(jwtToken);
            return claims.getBody().get("throwId", Long.class);
        } catch (ExpiredJwtException e) {
            throw new ApiException("기간 만료된 토큰입니다.", HttpStatus.BAD_REQUEST, 400);
        } catch (Exception e) {
            throw new ApiException("토큰 조회 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 500);
        }
    }


    // Jwt 만료일자 확인
    public boolean validateToken(String jwtToken) {
        boolean flag = true;
        try {
            Jws<Claims> claims = parseClaimsJws(jwtToken);

            if (claims.getBody().getExpiration().before(new Date())) {
                flag = false;
            }

        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public Jws<Claims> parseClaimsJws(String jwtToken) throws ExpiredJwtException {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
    }
}

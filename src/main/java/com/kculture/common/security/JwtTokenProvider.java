package com.kculture.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.Clock;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HEADER = BASE64_URL_ENCODER.encodeToString(
            "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8)
    );

    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long expirationSeconds;
    private final Clock clock;

    @Autowired
    public JwtTokenProvider(
            ObjectMapper objectMapper,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-seconds}") long expirationSeconds
    ) {
        this(objectMapper, secret, expirationSeconds, Clock.systemUTC());
    }

    JwtTokenProvider(ObjectMapper objectMapper, String secret, long expirationSeconds, Clock clock) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("jwt.secret은 32바이트 이상이어야 합니다.");
        }
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("jwt.expiration-seconds는 0보다 커야 합니다.");
        }
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
        this.clock = clock;
    }

    public String createToken(Long userId) {
        try {
            long issuedAt = Instant.now(clock).getEpochSecond();
            Map<String, Object> claims = Map.of(
                    "sub", userId.toString(),
                    "iat", issuedAt,
                    "exp", issuedAt + expirationSeconds
            );
            String payload = BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(claims));
            String signingInput = HEADER + "." + payload;
            return signingInput + "." + sign(signingInput);
        } catch (Exception e) {
            throw new IllegalStateException("JWT를 생성할 수 없습니다.", e);
        }
    }

    public Long getUserId(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3 || !HEADER.equals(parts[0])) {
                throw new IllegalArgumentException("JWT 형식이 올바르지 않습니다.");
            }

            String signingInput = parts[0] + "." + parts[1];
            byte[] expected = BASE64_URL_DECODER.decode(sign(signingInput));
            byte[] actual = BASE64_URL_DECODER.decode(parts[2]);
            if (!MessageDigest.isEqual(expected, actual)) {
                throw new IllegalArgumentException("JWT 서명이 올바르지 않습니다.");
            }

            Map<String, Object> claims = objectMapper.readValue(
                    BASE64_URL_DECODER.decode(parts[1]), new TypeReference<>() {}
            );
            Number expiresAt = (Number) claims.get("exp");
            if (expiresAt == null || expiresAt.longValue() <= Instant.now(clock).getEpochSecond()) {
                throw new IllegalArgumentException("만료된 JWT입니다.");
            }

            Long userId = Long.valueOf(String.valueOf(claims.get("sub")));
            if (userId <= 0) {
                throw new IllegalArgumentException("JWT 사용자 ID가 올바르지 않습니다.");
            }
            return userId;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("JWT를 검증할 수 없습니다.", e);
        }
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private String sign(String signingInput) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
        return BASE64_URL_ENCODER.encodeToString(
                mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8))
        );
    }
}

package com.kculture.common.security;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderTest {

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(
            new ObjectMapper(),
            "test-secret-that-is-at-least-32-bytes-long",
            3600
    );

    @Test
    void createdTokenContainsUserId() {
        String token = tokenProvider.createToken(42L);

        assertEquals(42L, tokenProvider.getUserId(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = tokenProvider.createToken(42L);
        int signatureIndex = token.lastIndexOf('.') + 2;
        char replacement = token.charAt(signatureIndex) == 'a' ? 'b' : 'a';
        String tampered = token.substring(0, signatureIndex)
                + replacement
                + token.substring(signatureIndex + 1);

        assertThrows(IllegalArgumentException.class, () -> tokenProvider.getUserId(tampered));
    }
}

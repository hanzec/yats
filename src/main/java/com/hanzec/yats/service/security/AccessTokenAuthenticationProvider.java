package com.hanzec.yats.service.security;

import com.hanzec.yats.service.security.token.AccessTokenAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AccessTokenAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

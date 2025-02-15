package com.hanzec.yats.service.security.token;

import com.google.gson.Gson;
import com.hanzec.yats.model.security.TailscaleJWTPayload;
import com.nimbusds.jose.JWSObject;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.text.ParseException;
import java.util.Collection;

public class APIAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final JWSObject jwt;
    private final TailscaleJWTPayload payload;

    public APIAuthenticationToken(String jwt) throws ParseException {
        super(null);
        this.jwt = JWSObject.parse(jwt);
        this.principal = null;
        this.payload = new Gson().fromJson(this.jwt.getPayload().toString(), TailscaleJWTPayload.class);
    }

    public APIAuthenticationToken(Object principal,
                                  String credential,
                                  Collection<? extends GrantedAuthority> authorities) throws ParseException {
        super(authorities);
        this.principal = principal;
        this.jwt = JWSObject.parse(credential);
        this.payload = new Gson().fromJson(this.jwt.getPayload().toString(), TailscaleJWTPayload.class);
    }

    /**
     *  This factory method can be safely used by any code that wishes to create a
     *  authenticated <code>UsernamePasswordAuthenticationToken</code>.
     * @param principal
     * @param credential
     * @param authorities
     * @return
     * @throws ParseException
     */
    public static APIAuthenticationToken authenticated(Object principal,
                                                       String credential,
                                                       Collection<? extends GrantedAuthority> authorities) throws ParseException {
        return new APIAuthenticationToken(principal, credential, authorities);
    }

    @Override
    public JWSObject getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        if (principal != null) {
            return principal;
        }
        return payload.getUserID();
    }
}

package com.hanzec.yats.service.security.token;

import com.google.gson.Gson;
import com.hanzec.yats.model.security.TailscaleJWTPayload;
import com.nimbusds.jose.JWSObject;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.text.ParseException;

public class APIAuthenticationToken extends AbstractAuthenticationToken {

    private final JWSObject jwt;
    private final TailscaleJWTPayload payload;

    public APIAuthenticationToken(String jwt) throws ParseException {
        super(null);
        this.jwt = JWSObject.parse(jwt);
        this.payload = new Gson().fromJson(this.jwt.getPayload().toString(), TailscaleJWTPayload.class);
    }

    @Override
    public JWSObject getCredentials() {
        return jwt;
    }

    @Override
    public String getPrincipal() {
        return payload.getUserID();
    }
}

package com.hanzec.yats.model.security;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class TailscaleJWTPayload {
    public TailscaleJWTPayload(String clientID) {
        this.user = clientID;
    }

    @Expose
    private String user;

    @Expose
    private String subject = "SIGNED FOR CLIENT LOGIN";

    @Expose
    private String userID = UUID.randomUUID().toString();
}

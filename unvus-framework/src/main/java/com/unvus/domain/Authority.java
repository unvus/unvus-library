package com.unvus.domain;

import java.io.Serializable;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

/**
 * An authority (a security role) used by Spring Security.
 */
@Data
public class Authority implements Serializable, GrantedAuthority {

    private String authority;

    public Authority() {

    }

    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}

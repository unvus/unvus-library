package com.unvus.domain;

import com.unvus.domain.audit.AbstractAuditingEntity;

public interface UnvusSecureUser extends AbstractAuditingEntity {

    // 아이디
    Long getId();
    void setId(Long id);

    // 계정 아이디(email)
    String getLogin();
    void setLogin(String login);

    // 계정 패스워드
    String getPassword();
    void setPassword(String password);

    // 사용자명
    String getName();
    void setName(String name);

    boolean isEnabled();

    void setEnabled(boolean activated);

}


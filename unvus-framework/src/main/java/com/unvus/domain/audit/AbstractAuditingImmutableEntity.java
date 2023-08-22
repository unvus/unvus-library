package com.unvus.domain.audit;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface AbstractAuditingImmutableEntity extends Serializable {

    Long getCreatedBy();
    void setCreatedBy(Long createdBy);

    LocalDateTime getCreatedDt();
    void setCreatedDt(LocalDateTime createdDt);
}

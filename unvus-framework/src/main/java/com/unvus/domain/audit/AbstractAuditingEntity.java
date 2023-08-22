package com.unvus.domain.audit;

import java.time.LocalDateTime;

/**
 * Created by guava on 7/30/16.
 */
public interface AbstractAuditingEntity extends AbstractAuditingImmutableEntity {
    Long getModifiedBy();
    void setModifiedBy(Long modifiedBy);

    LocalDateTime getModifiedDt();
    void setModifiedDt(LocalDateTime modifiedDt);

}

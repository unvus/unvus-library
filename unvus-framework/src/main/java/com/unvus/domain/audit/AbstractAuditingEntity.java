package com.unvus.domain.audit;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by guava on 7/30/16.
 */
@Data
public class AbstractAuditingEntity extends AbstractAuditingImmutableEntity {

    private Long modifiedBy;

    private LocalDateTime modifiedDt = LocalDateTime.now();
}

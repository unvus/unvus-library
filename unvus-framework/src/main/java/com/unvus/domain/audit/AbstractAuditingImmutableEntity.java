package com.unvus.domain.audit;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AbstractAuditingImmutableEntity implements Serializable {

    private Long createdBy;

    private LocalDateTime createdDt = LocalDateTime.now();
}

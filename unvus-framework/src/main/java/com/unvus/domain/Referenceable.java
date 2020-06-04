package com.unvus.domain;

import java.io.Serializable;

public interface Referenceable extends Serializable {

    EnumCode getRefTarget();
    void setRefTarget(EnumCode refTarget);

    Long getRefTargetKey();
    void setRefTargetKey(Long refTargetKey);
}

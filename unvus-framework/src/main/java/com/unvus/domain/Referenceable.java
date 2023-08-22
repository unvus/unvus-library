package com.unvus.domain;

import java.io.Serializable;

public interface Referenceable<T extends EnumCode>  extends Serializable {

    T getRefTarget();
    void setRefTarget(T refTarget);

    Long getRefTargetKey();
    void setRefTargetKey(Long refTargetKey);

    String getRefTargetType();
    void setRefTargetType(String refTargetType);
}

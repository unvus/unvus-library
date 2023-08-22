package com.unvus.domain;

/**
 * myBatis 를 통해 값을 저장 및 조회
 * Created by guava on 16.8.8.
 */
public interface EnumCode {
    /**
     * database 에 저장될 코드값.
     *
     * @return
     */
    String getCode();

    static EnumCode getByCode(String code) {
        return null;
    }

    default String getLabel() {
        return null;
    }
}

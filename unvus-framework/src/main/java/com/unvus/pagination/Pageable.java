package com.unvus.pagination;

import com.unvus.config.UnvusConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Pageable {
    String countMapperId() default "";
    boolean useMergeQuery() default false;
    String mergeMapperId() default "";
    String mergeParamId() default "ids";
	String usePagingKey() default UnvusConstants.USE_PAGING;
}

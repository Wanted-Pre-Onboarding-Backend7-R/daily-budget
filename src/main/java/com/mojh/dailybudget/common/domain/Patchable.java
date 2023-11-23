package com.mojh.dailybudget.common.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Patchable {
    /**
     * null인 필드는 무시, 기본 값 true
     * @return
     */
    boolean ignoreNull() default true;
}
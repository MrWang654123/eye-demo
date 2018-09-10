package com.cheersmind.cheersgenie.main.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图注解类
 * <p>Created by johnny wu on 2014-9-26</p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {

    /**
     * 视图id
     *
     * @return
     */
    int id() ;
}

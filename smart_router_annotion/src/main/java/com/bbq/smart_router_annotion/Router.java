package com.bbq.smart_router_annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Router {
    /**
     * @return 路由地址
     */
    String[] path();

    /**
     * @return 路由分组(暂时保留,留待以后扩展)
     */
    String group() default "";

    /**
     * 要添加的interceptors
     */
    Class[] interceptors() default {};
}

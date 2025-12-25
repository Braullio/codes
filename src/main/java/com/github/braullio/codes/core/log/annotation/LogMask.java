package com.github.braullio.codes.core.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* Campo aparece no log de forma mascarada */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LogMask {

    /* Ex: CPF -> *****123 */
    int visibleLast() default 3;
}

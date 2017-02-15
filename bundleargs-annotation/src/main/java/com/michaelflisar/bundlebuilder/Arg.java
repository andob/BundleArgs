package com.michaelflisar.bundlebuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) @Retention(RetentionPolicy.CLASS)
public @interface Arg
{
    String value() default "";
    boolean optional() default false;
}

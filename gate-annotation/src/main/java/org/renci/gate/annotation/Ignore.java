package org.renci.gate.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author jdr0887
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Ignore {

}

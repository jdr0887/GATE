package org.renci.gate.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Package {

    String name();

    String version();

    JobManagerType jobManagerType() default JobManagerType.LRM;

    String sourceURL() default "http://beluga.renci.org/scienceapps";

    String archiveNameTemplate() default "${lowerCaseName}-${version}.tar.gz";

}

package cn.carronailo.framework.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource
{
	int type() default -1;
	String file() default "";
	String category() default "";
}

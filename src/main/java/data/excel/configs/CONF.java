package data.excel.configs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CONF
{
	int TYPE() default -1;
	String FILE() default "";
	String INFO() default "";
}

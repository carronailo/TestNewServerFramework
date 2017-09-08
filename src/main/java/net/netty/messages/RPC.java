package net.netty.messages;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by CarroNailo on 2017/9/8 16:58 for TestNewServerFramework.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RPC
{
	int CID() default -1;
	int MID() default -1;
}

/**
 * Autogenerated by Thrift Compiler (0.10.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package net.thrift.test;


import java.util.Map;
import java.util.HashMap;

import org.apache.thrift.TEnum;

/**
 * 你还可以定义枚举类型, 其被指定为32位整型。域的值是可以自定义的，而且
 * 当不提供域的值时，默认会从1开始编号并递增。
 */
public enum Operation implements org.apache.thrift.TEnum
{
	ADD(1),
	SUBTRACT(2),
	MULTIPLY(3),
	DIVIDE(4);

	private final int value;

	private Operation(int value)
	{
		this.value = value;
	}

	/**
	 * Get the integer value of this enum value, as defined in the Thrift IDL.
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * Find a the enum type by its integer value, as defined in the Thrift IDL.
	 * @return null if the value is not found.
	 */
	public static Operation findByValue(int value)
	{
		switch (value)
		{
			case 1:
				return ADD;
			case 2:
				return SUBTRACT;
			case 3:
				return MULTIPLY;
			case 4:
				return DIVIDE;
			default:
				return null;
		}
	}
}

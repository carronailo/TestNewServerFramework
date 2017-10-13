package cn.carronailo.framework.data.excel;

import cn.carronailo.framework.data.Array;
import cn.carronailo.framework.data.DataSource;
import cn.carronailo.framework.data.DataType;
import cn.carronailo.framework.data.excel.exceptions.ExcelTableCellParseException;
import cn.carronailo.framework.data.excel.tables.ConfigTableMap;
import cn.carronailo.framework.data.excel.exceptions.ExcelTableNotExistsException;
import cn.carronailo.framework.data.excel.exceptions.UnsupportedExcelTableColumnException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExcelTableReader
{
	private String configPath = "";

	private Map<Class, Object> configTableContents = new HashMap<>();

	private DecimalFormat integerFormatter = new DecimalFormat("#");

	public ExcelTableReader(String configPath)
	{
		this.configPath = configPath;
	}

	public void readAll()
	{
		Iterator<Map.Entry<String, Map<String, Class>>> it = ConfigTableMap.getInstance().getAll();
		while(it.hasNext())
		{
			Map.Entry<String, Map<String, Class>> entry = it.next();
			for (Map.Entry<String, Class> _entry : entry.getValue().entrySet())
			{
				Object content = readOne(configPath.concat(entry.getKey()), _entry.getKey(), _entry.getValue());
				if (content != null)
					configTableContents.put(_entry.getValue(), content);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T[] getConfig(Class<T> clazz)
	{
		Object configContents = configTableContents.get(clazz);
		if(configContents == null)
		{
			DataSource dataSourceAnnotation = clazz.getAnnotation(DataSource.class);
			if(dataSourceAnnotation != null && dataSourceAnnotation.type() == DataType.EXCEL
				&& !dataSourceAnnotation.file().isEmpty() && !dataSourceAnnotation.category().isEmpty())
			{
				configContents = readOne(configPath.concat(dataSourceAnnotation.file()), dataSourceAnnotation.category(), clazz);
			}
		}
		return (T[])configContents;
	}

	private Object readOne(String file, String sheet, Class<?> tableClazz)
	{
		Object content = null;
		File excelFile = new File(file);
		try
		{
			Workbook wb = WorkbookFactory.create(excelFile);
			Sheet sht = wb.getSheet(sheet);
			if(sht != null)
			{
				System.out.println(String.format("读取表单[%s](in [%s]), 共[%d]行（包含表头）", sheet, file, sht.getLastRowNum() + 1));
				content = readContent(sht, tableClazz);
				configTableContents.put(tableClazz, content);
			}
			else
				throw new ExcelTableNotExistsException(String.format("%s[%s]", file, sheet));
			wb.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return content;
	}

	private Object readContent(Sheet sheet, Class<?> tableClazz) throws Exception
	{
		int total = sheet.getLastRowNum();
		Object contentArray = java.lang.reflect.Array.newInstance(tableClazz, total);
		for(int i = 0 ; i < total ; ++i)
		{
			Row row = sheet.getRow(i + 1);
			if(row != null)
			{
				Iterator<Cell> it = row.cellIterator();
				Object content = readRowContent(tableClazz, it);
				if(content != null)
					java.lang.reflect.Array.set(contentArray, i, content);
				else
					System.out.println(String.format("配置表读取错误：表[%s]第[%d]行", sheet.getSheetName(), i + 1));
			}
			else
				System.out.println(String.format("配置表读取错误：表[%s]第[%d]行", sheet.getSheetName(), i + 1));
		}
		return contentArray;
	}

	private Object readRowContent(Class<?> clazz, Iterator<Cell> cellIterator)
	{
		Object rowContent = null;
		try
		{
			rowContent = readObject(clazz, cellIterator);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return rowContent;
	}

	private Object readObject(Class<?> clazz, Iterator<Cell> cellIterator) throws Exception
	{
		Object obj = clazz.newInstance();
		Field[] fs = clazz.getFields();        // 获取所有 PUBLIC 变量，包含自己定义的和继承的
		for (Field f : fs)
			readField(obj, f, cellIterator);
		return obj;
	}

	private Object readPrimitive(Class<?> clazz, Iterator<Cell> cellIterator) throws Exception
	{
		Cell c = null;
		if(cellIterator.hasNext())
			c = cellIterator.next();
		if (clazz == boolean.class)
			return c != null && readCellBoolValue(c);
		else if (clazz == byte.class)
			return c != null ? (byte)readCellLongValue(c) : (byte)0;
		else if (clazz == char.class)
			return c != null ? (char)readCellLongValue(c) : (char)0;
		else if (clazz == short.class)
			return c != null ? (short)readCellLongValue(c) : (short)0;
		else if (clazz == int.class)
			return c != null ? (int)readCellLongValue(c) : 0;
		else if (clazz == long.class)
			return c != null ? readCellLongValue(c) : 0L;
		else if (clazz == float.class)
			return c != null ? (float)readCellDoubleValue(c) : 0.0f;
		else if (clazz == double.class)
			return c != null ? readCellDoubleValue(c) : 0.0;
		else if (clazz == String.class)
			return c != null ? readCellStringValue(c) : "";
		else if (clazz == void.class)
			return null;
		else
			return null;
	}

	private void readField(Object obj, Field field, Iterator<Cell> cellIterator) throws Exception
	{
		try
		{
			Class<?> fieldClazz = field.getType();
			if (fieldClazz.isPrimitive() || fieldClazz == String.class)
				readPrimitiveField(obj, field, cellIterator);
			else if (fieldClazz.isArray())
				readArrayField(obj, field, cellIterator);
			else if (fieldClazz.isEnum())
				throw new UnsupportedExcelTableColumnException(obj, field.getName());
			else
				readCustomField(obj, field, cellIterator);
		}
		catch (ExcelTableCellParseException | UnsupportedExcelTableColumnException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new ExcelTableCellParseException(obj, field.getName(), ex);
		}
	}

	private void readPrimitiveField(Object obj, Field field, Iterator<Cell> cellIterator) throws Exception
	{
		Cell c = null;
		if(cellIterator.hasNext())
			c = cellIterator.next();
		Class<?> fieldClazz = field.getType();
		if (fieldClazz == boolean.class)
			field.setBoolean(obj, c != null && readCellBoolValue(c));
		else if (fieldClazz == byte.class)
			field.setByte(obj, c != null ? (byte)readCellLongValue(c) : (byte)0);
		else if (fieldClazz == char.class)
			field.setChar(obj, c != null ? (char)readCellLongValue(c) : (char)0);
		else if (fieldClazz == short.class)
			field.setShort(obj, c != null ? (short)readCellLongValue(c) : (short)0);
		else if (fieldClazz == int.class)
			field.setInt(obj, c != null ? (int)readCellLongValue(c) : 0);
		else if (fieldClazz == long.class)
			field.setLong(obj, c != null ? readCellLongValue(c) : 0L);
		else if (fieldClazz == float.class)
			field.setFloat(obj, c != null ? (float)readCellDoubleValue(c) : 0.0f);
		else if (fieldClazz == double.class)
			field.setDouble(obj, c != null ? readCellDoubleValue(c) : 0.0);
		else if (fieldClazz == void.class)
			throw new UnsupportedExcelTableColumnException(obj, field.getName());
		else if (fieldClazz == String.class)
			field.set(obj, c != null ? readCellStringValue(c) : "");
		else
			throw new UnsupportedExcelTableColumnException(obj, field.getName());
	}

	private void readArrayField(Object obj, Field field, Iterator<Cell> cellIterator) throws Exception
	{
		Class<?> elemClazz = field.getType().getComponentType();
		Array arrayAnnotation = field.getAnnotation(Array.class);
		if(arrayAnnotation != null)
		{
			short len = (short) arrayAnnotation.size();
			Object newArray = java.lang.reflect.Array.newInstance(elemClazz, len);
			if(elemClazz.isPrimitive() || elemClazz == String.class)
			{
				for (int i = 0; i < len; ++i)
				{
					Object elem = readPrimitive(elemClazz, cellIterator);
					if (elem == null)
						throw new ExcelTableCellParseException(obj, field.getName());
					java.lang.reflect.Array.set(newArray, i, elem);
				}
			}
			else
			{
				for (int i = 0; i < len; ++i)
				{
					Object elem = readObject(elemClazz, cellIterator);
					if (elem == null)
						throw new ExcelTableCellParseException(obj, field.getName());
					java.lang.reflect.Array.set(newArray, i, elem);
				}
			}
			field.set(obj, newArray);
		}
		else
			throw new UnsupportedExcelTableColumnException(obj, field.getName());
	}

	private void readCustomField(Object obj, Field field, Iterator<Cell> cellIterator) throws Exception
	{
		Object fieldObj = readObject(field.getType(), cellIterator);
		if (fieldObj == null)
			throw new ExcelTableCellParseException(obj, field.getName());
		field.set(obj, fieldObj);
	}

	private String readCellStringValue(Cell c)
	{
		switch (c.getCellTypeEnum())
		{
			case BOOLEAN:
				return Boolean.toString(c.getBooleanCellValue());
			case STRING:
				return c.getStringCellValue();
			case FORMULA:
				return Double.toString(c.getNumericCellValue());
			case ERROR:
				return FormulaError.forInt(c.getErrorCellValue()).toString();
			case NUMERIC:
				return Double.toString(c.getNumericCellValue());
			default:
				return "";
		}
	}

	private boolean readCellBoolValue(Cell c)
	{
		switch (c.getCellTypeEnum())
		{
			case BOOLEAN:
				return c.getBooleanCellValue();
			case STRING:
				return c.getStringCellValue().equalsIgnoreCase("true");
			case FORMULA:
				return c.getNumericCellValue() > 0;
			case ERROR:
				return false;
			case NUMERIC:
				return c.getNumericCellValue() > 0;
			default:
				return false;
		}
	}

	private long readCellLongValue(Cell c)
	{
		switch (c.getCellTypeEnum())
		{
			case BOOLEAN:
				return c.getBooleanCellValue() ? 1 : 0;
			case STRING:
				return Long.parseLong(c.getStringCellValue());
			case FORMULA:
				return (long)c.getNumericCellValue();
			case ERROR:
				return 0;
			case NUMERIC:
				return (long)c.getNumericCellValue();
			default:
				return 0;
		}
	}

	private double readCellDoubleValue(Cell c)
	{
		switch (c.getCellTypeEnum())
		{
			case BOOLEAN:
				return c.getBooleanCellValue() ? 1.0 : 0.0;
			case STRING:
				return Double.parseDouble(c.getStringCellValue());
			case FORMULA:
				return c.getNumericCellValue();
			case ERROR:
				return 0.0;
			case NUMERIC:
				return c.getNumericCellValue();
			default:
				return 0.0;
		}
	}
}

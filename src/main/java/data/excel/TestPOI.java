package data.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;

/**
 * Created by CarroNailo on 2017/5/16.
 */
public class TestPOI
{
	public static void ProcessHSSFWorkbookContent(HSSFWorkbook wb)
	{
		int sheetsCount = wb.getNumberOfSheets();
		for(int i = 0 ; i < sheetsCount; ++i)
		{
			HSSFSheet sheet = wb.getSheetAt(i);
			System.out.println(sheet.getSheetName());
			System.out.println("一共有" + (sheet.getLastRowNum() + 1) + "行");
			HSSFRow firstRow = sheet.getRow(0);
			if(firstRow != null)
			{
				for(Cell c : firstRow)
				{
					HSSFCell cell = (HSSFCell)c;
					String cellContent = null;
					switch (cell.getCellTypeEnum())
					{
						case BOOLEAN:
							cellContent = String.format("b:%1$b", cell.getBooleanCellValue());
							break;
						case STRING:
							cellContent = String.format("s:%1$s", cell.getStringCellValue());
							break;
						case FORMULA:
							cellContent = String.format("f:%1$s", cell.getCellFormula());
							break;
						case ERROR:
							cellContent = String.format("e:%1$s", FormulaError.forInt(cell.getErrorCellValue()));
							break;
						case NUMERIC:
							cellContent = String.format("n:%1$f", cell.getNumericCellValue());
							break;
						default:
							break;
					}
					System.out.print(cellContent + "\t");
				}
				System.out.println();
			}
		}
	}

	public static void ProcessXSSFWorkbookContent(XSSFWorkbook wb)
	{
		int sheetsCount = wb.getNumberOfSheets();
		for(int i = 0 ; i < sheetsCount; ++i)
		{
			XSSFSheet sheet = wb.getSheetAt(i);
			System.out.println(sheet.getSheetName());
			System.out.println("一共有" + (sheet.getLastRowNum() + 1) + "行");
			XSSFRow firstRow = sheet.getRow(0);
			if(firstRow != null)
			{
				for(Cell c : firstRow)
				{
					XSSFCell cell = (XSSFCell)c;
					String cellContent = null;
					switch (cell.getCellTypeEnum())
					{
						case BOOLEAN:
							cellContent = String.format("b:%1$b", cell.getBooleanCellValue());
							break;
						case STRING:
							cellContent = String.format("s:%1$s", cell.getStringCellValue());
							break;
						case FORMULA:
							cellContent = String.format("f:%1$s", cell.getCellFormula());
							break;
						case ERROR:
							cellContent = String.format("e:%1$s", FormulaError.forInt(cell.getErrorCellValue()));
							break;
						case NUMERIC:
							cellContent = String.format("n:%1$f", cell.getNumericCellValue());
							break;
						default:
							break;
					}
					System.out.print(cellContent + "\t");
				}
				System.out.println();
			}
		}
	}


	public static void main(String[] args)
	{
		File excelFile = new File("src/main/resources/怪物属性表.xlsx");
		System.out.println(excelFile.getAbsolutePath());
		System.out.println(excelFile.exists());

		try
		{
			Workbook wb = WorkbookFactory.create(excelFile);
			System.out.println(wb);
			if(wb instanceof HSSFWorkbook)
				ProcessHSSFWorkbookContent((HSSFWorkbook)wb);
			else if(wb instanceof XSSFWorkbook)
				ProcessXSSFWorkbookContent((XSSFWorkbook)wb);
			wb.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InvalidFormatException e)
		{
			e.printStackTrace();
		}
	}
}

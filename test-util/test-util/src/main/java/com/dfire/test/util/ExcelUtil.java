package com.dfire.test.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil<T> {
	
	private static ExcelUtil<?> excelUtil;

	private ExcelUtil (){}  
	
	@SuppressWarnings("rawtypes")
	public static ExcelUtil getExcelUtil() {
		if (excelUtil == null) {
			excelUtil = new ExcelUtil();
		}
		return excelUtil;
	}
	

	/**
	 * 获取excel表所有sheet数据
	 * @param clz
	 * @param path
	 * @return
	 */
	@SuppressWarnings("hiding")
	public <T> List<T> readExcel(Class<T> clz, String path) {
		System.out.println(path);
		if (StringUtil.isEmpty(path)) {
			return null;
		}
		InputStream is;
		Workbook xssfWorkbook;
		try {
			is = new FileInputStream(path);
			if (path.endsWith(".xls")) {
				xssfWorkbook = new HSSFWorkbook(is);
			} else { //支持 office 2007 以上的版本
				xssfWorkbook = new XSSFWorkbook(is);
			}
			is.close();
			int sheetNumber = xssfWorkbook.getNumberOfSheets(); //获取sheet数量
			List<T> allData = new ArrayList<T>();
			for (int i = 0; i < sheetNumber; i++) {//循环读取sheet数据 至list
				allData.addAll(transToObject(clz, xssfWorkbook,xssfWorkbook.getSheetName(i)));
			}
			return allData;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("转换excel文件失败：" + e.getMessage());
		}
	}
	

	/**
	 * 
	 * @param clz model类型
	 * @param xssfWorkbook
	 * @param sheetName
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 */
	@SuppressWarnings("hiding")
	private <T> List<T> transToObject(Class<T> clz, Workbook xssfWorkbook, String sheetName){
		List<T> list = new ArrayList<T>();
		Sheet xssfSheet = xssfWorkbook.getSheet(sheetName); //获取sheet
		Row firstRow = xssfSheet.getRow(0); //第一行为表头
		if(null ==firstRow){
			return list;
		}
		List<Object> heads = getRowDataToList(firstRow);  //表头内容转为list
		for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {//从第二行开始遍历，第二行开始是用例内容
			try {
				Row row = xssfSheet.getRow(rowNum);
				if (row == null) {
					continue;
				}
				T obj = clz.newInstance();//实例
				List<Object> data = getRowDataToList(row);
				setValue(obj, data, heads); //将每一个单元格的内容set到obj中
				list.add(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 将一行内容转为list
	 * @param row
	 * @return
	 */
	private List<Object> getRowDataToList(Row row) {
		List<Object> cells = new ArrayList<Object>();
		if (row != null) {
			for (short cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
				Cell cell = row.getCell(cellNum);
				if (null!=cell&&StringUtil.isNotEmpty(cell.getStringCellValue())) {
					cells.add(cell.getStringCellValue());
				}else {
					cells.add("");
				}
				continue;
			}
		}
		return cells;
	}
	/**
	 * 
	 * @param obj
	 * @param data  需要转换的数据
	 * @param heads  表头
	 * @param methods  表头属性对应的set方法
	 */
	private void setValue(Object obj, List<Object> data, List<Object> heads){
		for (int i = 0; i < heads.size(); i++) {
			if (i<data.size()) {
				invokeSet(obj, String.valueOf(heads.get(i)), data.get(i)) ;
			}
		}
	}

	/**
	 * 调用set方法
	 * @param o
	 * @param fieldName
	 * @param value
	 */
	public void invokeSet(Object o, String fieldName, Object value) {
	   Method method = getSetMethod(o.getClass(), fieldName);
	   try {
	       method.invoke(o, new Object[]{value});
	   } catch (Exception e) {
		   System.out.println("调用set方法失败！");
	   }
	}

	
	/**
	 * 获取set方法
	 * @param objcls
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Method getSetMethod(Class objcls, String fieldName) {
		try {
			Class[] paramTypes = new Class[1];
			Field field = objcls.getDeclaredField(fieldName);
			paramTypes[0] = field.getType();
			// 拼写field的set方法
			StringBuffer sb = new StringBuffer();
			sb.append("set");
			sb.append(fieldName.substring(0, 1).toUpperCase());
			sb.append(fieldName.substring(1));
			Method method = objcls.getMethod(sb.toString(), paramTypes);
			return method;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}




	
	public static void main(String arg[]) {
		
	}
}

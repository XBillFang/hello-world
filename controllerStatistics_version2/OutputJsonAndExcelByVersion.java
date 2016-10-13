package com.controller.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * create time: 2016年8月23日 上午10:40:39
 *
 * author: FangDongsheng
 *
 * description:统计controller接口
 *
 * version:1.0
 */
@SuppressWarnings("deprecation")
public class OutputJsonAndExcelByVersion
{
 
	public static void main(String[] args)
	{
		// git版本取得
		String gitRoot = LoadProperties("target.gitroot.path");
		String revision = getHistoryInfo(gitRoot);
		
		outputConsoleInfoToFile(revision);
		String pathName = LoadProperties("target.path.name");
		ArrayList<String> fileNameList = new ArrayList<String>();

		String packageName = LoadProperties("target.package.name");
		loop(pathName, packageName, fileNameList);
		
		OutputJsonAndExcelByVersion gcnInstant = new OutputJsonAndExcelByVersion();
		Map<String, List<Map<String, List<ControllerInfo>>>> controllerInfoMap = gcnInstant.getControllerList(fileNameList);
		//gcnInstant.outputJsonFile(controllerInfoMap);
		gcnInstant.outputTxtFile(controllerInfoMap);
		gcnInstant.outputExcelFile(revision, controllerInfoMap);
	}
	
	public void outputTxtFile(Map<String, List<Map<String, List<ControllerInfo>>>> controllerInfoMap)
	{
		for (String key : controllerInfoMap.keySet())
		{
			List<Map<String, List<ControllerInfo>>> controllerInfoList = controllerInfoMap.get(key);
			
			System.out.println("#################################################################");
			System.out.println("SHEETNAME = " + key);
			for (Map<String, List<ControllerInfo>> controllerInfoSubMap : controllerInfoList)
			{
				for (String subKey : controllerInfoSubMap.keySet())
				{
					List<ControllerInfo> innerList = controllerInfoSubMap.get(subKey);
					Collections.sort(innerList, (p1, p2) -> p1.getMethodName().compareTo(p2.getMethodName()));
					for (ControllerInfo info : innerList)
					{						
						System.out.println("CONTROLLERNAME = " + info.getControllerName());
						System.out.println("DESCRIPTION = " + info.getDescription());
						System.out.println("REQUESTMAPPINGOFCLASS = " + info.getRequestMappingOfClass());
						System.out.println("METHODNAME = " + info.getMethodName());
						System.out.println("REQUESTMAPPINGOFMETHOD = " + info.getRequestMappingOfMethod());
						System.out.println("FULLNAME = " + info.getFullName());
						System.out.println("****************************************************");
					}
				}
			}
		}
	}
	
	public void outputJsonFile(Map<String, List<Map<String, List<ControllerInfo>>>> controllerInfoMap)
	{
		System.out.println("{");
		int index = 1;
		for (String key : controllerInfoMap.keySet())
		{
			List<Map<String, List<ControllerInfo>>> controllerInfoList = controllerInfoMap.get(key);
			StringBuilder strbdr = new StringBuilder();
			strbdr.append("\"" + key + "\":[");
			for (Map<String, List<ControllerInfo>> controllerInfoSubMap : controllerInfoList)
			{
				for (String subKey : controllerInfoSubMap.keySet())
				{
					List<ControllerInfo> innerList = controllerInfoSubMap.get(subKey);
					Collections.sort(innerList, (p1, p2) -> p1.getMethodName().compareTo(p2.getMethodName()));
					for (ControllerInfo info : innerList)
					{
						strbdr.append("{\"controllerName\" : \"" + info.getControllerName() + "\",");
						strbdr.append("\"description\" : \"" + info.getDescription() + "\",");
						strbdr.append("\"requestMappingOfClass\" : \"" + info.getRequestMappingOfClass() + "\",");
						strbdr.append("\"methodName\" : \"" + info.getMethodName() + "\",");
						strbdr.append("\"requestMappingOfMethod\" : \"" + info.getRequestMappingOfMethod() + "\",");
						strbdr.append("\"fullName\" : \"" + info.getFullName() + "\"},");
					}
				}
			}
			if(index == controllerInfoMap.keySet().size())
			{
				strbdr.append("]");
			} else {
				strbdr.append("],");
			}
			System.out.println(strbdr.toString().replace(",]", "]"));
			index += 1;
		}
		System.out.println("}");
	}
	
   public static String getHistoryInfo(String gitRoot) {
   	String revision = null;
   	Git git;
   	try
   	{
   		git = Git.open(new File(gitRoot));
   	   Iterable<RevCommit> gitlog= git.log().call();  
   	   revision = gitlog.iterator().next().getName();
   	}
   	catch (IOException e)
   	{
   		e.printStackTrace();
   	}
   	catch (NoHeadException e)
   	{
   		e.printStackTrace();
   	}
   	catch (GitAPIException e)
   	{
   		e.printStackTrace();
   	}
		return revision;
   }
   
	public static void outputConsoleInfoToFile(String revision)
	{
		// 把console里的内容输入到文件中
		String txtOutputPath = LoadProperties("result.output.path.name");
		//int revisionHashCode = getHistoryInfo(gitRoot).hashCode();
		//File outfile = new File(txtOutputPath, "ControllerStatistics_" + revision + ".json");
		File outfile = new File(txtOutputPath, "ControllerStatistics_" + revision + ".txt");
		try
		{
			outfile.createNewFile();
			FileOutputStream fos = new FileOutputStream(outfile);
			PrintStream printStream = new PrintStream(fos);
			System.setOut(printStream);
			//fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String LoadProperties(String keyName)
	{
		String value = "";
		try
		{
			Properties prop =  new  Properties();
			FileInputStream fis = new FileInputStream("statisticsTargetInfo.properties");
			prop.load(fis);
			value = prop.getProperty(keyName);
			fis.close();
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return value;
	}

	public static void loop(String pathName, String packageName, ArrayList<String> fileNameList)
	{
		String fullName;
		File file = new File(pathName);
		File[] files = file.listFiles();
		String[] names = file.list();

		for (String name : names)
		{
			if (name.indexOf(".") != -1)
			{
				fullName = packageName + "." + name;
				fileNameList.add(fullName);
			}
		}

		for (File a : files)
		{
			String newPackage = null;
			if (a.isDirectory())
			{
				newPackage = a.getAbsolutePath().substring(a.getAbsolutePath().lastIndexOf("\\")).replace("\\", ".");
				loop(a.getAbsolutePath(), packageName + newPackage, fileNameList);
			}
		}
	}

	private static void getMethodName(String classFullName, List<Map<String, List<ControllerInfo>>> ControllerInfoList)
	{

		try
		{
			Class<?> clazz = Class.forName(classFullName);
			Method[] methods = clazz.getDeclaredMethods();

			String controllerName = classFullName.substring(classFullName.lastIndexOf(".") + 1);
			if("HeaderNavigationRightComponentNewController".equals(controllerName)) {
				System.out.println();
			}
			String classAnnotationValue = parseClassAnnotation(clazz);
			List<ControllerInfo> innerList = new ArrayList<ControllerInfo>();

			String methodName = null;
			
			// 满足多态性
			String oldMethodName = methods[0].getName() + parseMethodAnnotation(methods[0]);
			int index = 0;
			//此循环来完成List<ControllerInfo>
			for (Method method : methods)
			{
				ControllerInfo controllerInfo = new ControllerInfo();
				String methodAnnotationValue = parseMethodAnnotation(method);
				methodName = method.getName();
				String methodInfo = methodName + methodAnnotationValue;
				
				if (index !=0 && oldMethodName.equals(methodInfo))
				{
					continue;
				}
				
				if (classAnnotationValue == null)
				{
					classAnnotationValue = "";
				}
				if (methodAnnotationValue == null)
				{
					methodAnnotationValue = "";
				}
				
				controllerInfo.setControllerName(controllerName);
				controllerInfo.setDescription("");
				controllerInfo.setRequestMappingOfClass(classAnnotationValue);
				controllerInfo.setMethodName(methodName);
				controllerInfo.setRequestMappingOfMethod(methodAnnotationValue);
				controllerInfo.setFullName(classFullName);
				innerList.add(controllerInfo);
				oldMethodName = methodName + methodAnnotationValue;
				index += 1;
			}
			
			//此treemap用于行controllerName排序
			Map<String, List<ControllerInfo>> controllerInfoMap = new TreeMap<String, List<ControllerInfo>>();
			//此处来完成Map<String, List<ControllerInfo>>
			controllerInfoMap.put(controllerName, innerList);
			ControllerInfoList.add(controllerInfoMap);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static <T> String parseClassAnnotation(Class<T> clazz)
	{
		String classAnnotationValue = null;
		if (clazz.isAnnotationPresent(RequestMapping.class))
		{
			RequestMapping rm = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
			String[] values = rm.value();
			for (String string : values)
			{
				if (!string.isEmpty() && string != null)
				{
					classAnnotationValue = string;
				}
			}
		}
		return classAnnotationValue;
	}

	public static <T> String parseMethodAnnotation(Method method) throws Exception
	{
		String methodAnnotationValue = "";
		if (method.isAnnotationPresent(RequestMapping.class))
		{
			RequestMapping rm = method.getAnnotation(RequestMapping.class);
			String[] values = rm.value();
			for (String string : values)
			{
				if (!string.isEmpty() && string != null)
				{
					methodAnnotationValue = string;
				}
			}
		}
		return methodAnnotationValue;
	}




	public Map<String, List<Map<String, List<ControllerInfo>>>> getControllerList(ArrayList<String> fileNameList)
	{
		//此treemap用于sheet名排序
		Map<String, List<Map<String, List<ControllerInfo>>>> controllerInfoMap = new TreeMap<String, List<Map<String, List<ControllerInfo>>>>();
		List<Map<String, List<ControllerInfo>>> controllerInfoList = null;

		//此循环来完成List<Map<String, List<ControllerInfo>>>
		for (String name : fileNameList)
		{
			if (name.indexOf("Controller.java") != -1)
			{
				String fullName = name.substring(0, name.lastIndexOf("."));
				String packageName = fullName.substring(0, fullName.lastIndexOf("."));

				if (!controllerInfoMap.containsKey(packageName))
				{
					controllerInfoList = new ArrayList<Map<String, List<ControllerInfo>>>();
					getMethodName(fullName, controllerInfoList);
					controllerInfoMap.put(packageName, controllerInfoList);
				}
				else
				{
					getMethodName(fullName, controllerInfoList);
				}
			}
		}
		return controllerInfoMap;
	}

	public void outputExcelFile(String revision, Map<String, List<Map<String, List<ControllerInfo>>>> controllerInfoMap)
	{
		//String workbookExportPath = "D:\\exportExcel";
		String workbookExportPath = LoadProperties("result.output.path.name");
		String workbookName = "ControllerStatistics";

		HSSFWorkbook wb = new HSSFWorkbook();
		//Date current = new Date();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		//String fileName = workbookExportPath + "\\" + workbookName + "_" + sdf.format(current) + ".xls";
		String fileName = workbookExportPath + "\\" + workbookName + "_" + revision + ".xls";
		File file = new File(fileName);
		FileOutputStream fileOut = null;
		try
		{
			fileOut = new FileOutputStream(file);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}

		makeControllerInfoSheet(wb, controllerInfoMap);



		try
		{
			wb.write(fileOut);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fileOut != null)
			{
				try
				{
					fileOut.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void makeControllerInfoSheet(HSSFWorkbook wb, Map<String, List<Map<String, List<ControllerInfo>>>> controllerInfoMap)
	{
		String[] headerNames =
		{ "序号", "Controller名称", "Controller描述", "类 @RequestMapping", "方法名", "方法 @RequestMapping", "类全路径" };


		for (String key : controllerInfoMap.keySet())
		{
			List<Map<String, List<ControllerInfo>>> controllerInfoList = new ArrayList<Map<String, List<ControllerInfo>>>();
			int rowIndex = 0;
			int colIndex = 0;
			controllerInfoList = controllerInfoMap.get(key);
			
			// 创建sheet sheet名为各controller所在的文件夹名
			HSSFSheet sheet = wb.createSheet(key.substring(key.lastIndexOf(".") + 1));

			// 创建header行
			HSSFRow headerRow = sheet.createRow(rowIndex++);

			// header部分 内容和样式设定
			for (String headerName : headerNames)
			{
				HSSFCell headerCell = headerRow.createCell(colIndex++);

				// 内容设定
				headerCell.setCellValue(headerName);
				HSSFCellStyle cellStyle = wb.createCellStyle();

				//对齐方式设定
				cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 设置单元格水平方向对其方式
				cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 设置单元格垂直方向对其方式

				//前景色设定
				cellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
				cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

				//字体设定
				HSSFFont font = wb.createFont();
				font.setFontName("微软雅黑");
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				cellStyle.setFont(font);

				//边框设定
				cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
				cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
				cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
				cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

				headerCell.setCellStyle(cellStyle);
			}

			sheet.createFreezePane(0, 1, 0, 1);
			
			// controller部分 内容和样式设定
			if (controllerInfoList != null && controllerInfoList.size() > 0)
			{
				// 序号
				Integer itemNum = 1;

				// 合并单元个开始值
				int mergeCellNumStart = 1;

				// 合并单元个结束值
				int mergeCellNumEnd = 1;
				String controllerName = "";
				for (Map<String, List<ControllerInfo>> infoMap : controllerInfoList)
				{
					for (String infoKey : infoMap.keySet())
					{
						List<ControllerInfo> infoList = infoMap.get(infoKey);
						for (ControllerInfo info : infoList)
						{
							if (controllerName.equals(info.getControllerName()))
							{
								++mergeCellNumEnd;
							}
							else
							{
								if (mergeCellNumEnd != 1)
								{
									// controller合并单元格
									sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 0, 0));
									sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 1, 1));
									sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 2, 2));
									sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 3, 3));
									sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 6, 6));
									mergeCellNumStart = mergeCellNumEnd + 1;
									++mergeCellNumEnd;
									++itemNum;
								}
							}
							
							HSSFRow dataRow = sheet.createRow(rowIndex++);
							
							// 每个单元格设定值和样式
							createCell(wb, sheet, dataRow, 0, HSSFCellStyle.ALIGN_GENERAL, HSSFCellStyle.VERTICAL_CENTER, itemNum.toString());
							createCell(wb, sheet, dataRow, 1, HSSFCellStyle.ALIGN_GENERAL, HSSFCellStyle.VERTICAL_CENTER,
									info.getControllerName());
							createCell(wb, sheet, dataRow, 2, HSSFCellStyle.ALIGN_GENERAL, HSSFCellStyle.VERTICAL_CENTER,
									info.getDescription());
							createCell(wb, sheet, dataRow, 3, HSSFCellStyle.ALIGN_GENERAL, HSSFCellStyle.VERTICAL_CENTER,
									info.getRequestMappingOfClass());
							createCell(wb, sheet, dataRow, 4, HSSFCellStyle.ALIGN_GENERAL, HSSFCellStyle.VERTICAL_CENTER,
									info.getMethodName());
							createCell(wb, sheet, dataRow, 5, HSSFCellStyle.ALIGN_GENERAL, HSSFCellStyle.VERTICAL_CENTER,
									info.getRequestMappingOfMethod());
							createCell(wb, sheet, dataRow, 6, HSSFCellStyle.ALIGN_GENERAL, HSSFCellStyle.VERTICAL_CENTER, info.getFullName());
							
							controllerName = info.getControllerName();
						}
					}
				}

				// 最后一个controller合并单元格
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 0, 0));
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 1, 1));
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 2, 2));
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 3, 3));
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 6, 6));
			}
		}

		// 自动适应列宽度
		for (int i = 0; i < wb.getNumberOfSheets(); i++)
		{
			for (int j = 0; j < headerNames.length; j++)
			{
				wb.getSheetAt(i).autoSizeColumn(j, true);
			}
		}
	}

	/**
	 * 创建一个单元格并为其设定内容和样式
	 * 
	 * @param wb
	 *           工作簿
	 * @param row
	 *           行
	 * @param column
	 *           列
	 * @param halign
	 *           水平方向对其方式
	 * @param valign
	 *           垂直方向对其方式
	 */
	private static void createCell(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row, int column, short halign, short valign,
			String value)
	{

		HSSFCell contentCell = row.createCell(column);

		contentCell.setCellValue(value);
		HSSFCellStyle cellStyle = wb.createCellStyle(); // 创建单元格样式
		cellStyle.setAlignment(halign); // 设置单元格水平方向对其方式
		cellStyle.setVerticalAlignment(valign); // 设置单元格垂直方向对其方式
		HSSFFont font = wb.createFont();
		font.setFontName("微软雅黑");
		cellStyle.setFont(font);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		contentCell.setCellStyle(cellStyle); // 设置单元格样式
	}
}

package com.tasly.anguo.storefront.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * create time: 2016��8��23�� ����10:40:39
 *
 * author: FangDongsheng
 *
 * description:ͳ��controller�ӿ�
 *
 * version:1.0
 */
@SuppressWarnings("deprecation")
public class GetClassName
{
 
	public static void main(String[] args)
	{
		String pathName = "D:\\anguo\\ext\\anguo\\anguostorefront\\web\\src\\com\\tasly\\anguo\\storefront\\controllers";

		// ��console����������뵽�ļ���
		File outfile = new File(pathName, "StatisticsControllerInterface.txt");
		try
		{
			outfile.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(outfile);
			PrintStream printStream = new PrintStream(fileOutputStream);
			System.setOut(printStream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		ArrayList<String> fileNameList = new ArrayList<String>();

		String packageName = "com.tasly.anguo.storefront.controllers";
		loop(pathName, packageName, fileNameList);

		GetClassName gcnInstant = new GetClassName();
		Map<String, List<ControllerInfo>> controllerInfoMap = gcnInstant.getControllerList(fileNameList);
		gcnInstant.getWorkBookExport(controllerInfoMap);
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

	private static void getMethodName(String classFullName, List<ControllerInfo> ControllerInfoList)
	{

		try
		{
			Class<?> clazz = Class.forName(classFullName);
			Method[] methods = clazz.getDeclaredMethods();

			String controllerName = classFullName.substring(classFullName.lastIndexOf(".") + 1);
			String classAnnotationValue = parseClassAnnotation(clazz);

			String methodName = null;
			for (Method method : methods)
			{
				ControllerInfo controllerInfo = new ControllerInfo();
				String methodAnnotationValue = parseMethodAnnotation(method);
				methodName = method.getName();

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

				ControllerInfoList.add(controllerInfo);
			}
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
		String methodAnnotationValue = null;
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




	public Map<String, List<ControllerInfo>> getControllerList(ArrayList<String> fileNameList)
	{
		Map<String, List<ControllerInfo>> controllerInfoMap = new HashMap<String, List<ControllerInfo>>();
		List<ControllerInfo> controllerInfoList = null;

		for (String name : fileNameList)
		{
			if (name.indexOf("Controller.java") != -1)
			{
				String fullName = name.substring(0, name.lastIndexOf("."));
				String packageName = fullName.substring(0, fullName.lastIndexOf("."));

				if (!controllerInfoMap.containsKey(packageName))
				{
					controllerInfoList = new ArrayList<ControllerInfo>();
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

	public void getWorkBookExport(Map<String, List<ControllerInfo>> controllerInfoMap)
	{
		String workbookExportPath = "D:\\exportExcel";
		String workbookName = "ControllerStatistics";

		HSSFWorkbook wb = new HSSFWorkbook();
		Date current = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		String fileName = workbookExportPath + "\\" + workbookName + "_" + sdf.format(current) + ".xls";
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

	public void makeControllerInfoSheet(HSSFWorkbook wb, Map<String, List<ControllerInfo>> controllerInfoMap)
	{
		String[] headerNames =
		{ "���", "Controller����", "Controller����", "�� @RequestMapping", "������", "���� @RequestMapping", "��ȫ·��" };

		List<ControllerInfo> controllerInfoList = new ArrayList<ControllerInfo>();

		for (String key : controllerInfoMap.keySet())
		{
			int rowIndex = 0;
			int colIndex = 0;
			controllerInfoList = controllerInfoMap.get(key);

			// ����sheet sheet��Ϊ��controller���ڵ��ļ�����
			HSSFSheet sheet = wb.createSheet(key.substring(key.lastIndexOf(".") + 1));

			// ����header��
			HSSFRow headerRow = sheet.createRow(rowIndex++);

			// header���� ���ݺ���ʽ�趨
			for (String headerName : headerNames)
			{
				HSSFCell headerCell = headerRow.createCell(colIndex++);

				// �����趨
				headerCell.setCellValue(headerName);
				HSSFCellStyle cellStyle = wb.createCellStyle();

				//���뷽ʽ�趨
				cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // ���õ�Ԫ��ˮƽ������䷽ʽ
				cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // ���õ�Ԫ��ֱ������䷽ʽ

				//ǰ��ɫ�趨
				cellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
				cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

				//�����趨
				HSSFFont font = wb.createFont();
				font.setFontName("΢���ź�");
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				cellStyle.setFont(font);

				//�߿��趨
				cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//�ϱ߿�
				cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //�±߿�
				cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//��߿�
				cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//�ұ߿�

				headerCell.setCellStyle(cellStyle);
			}

			// controller���� ���ݺ���ʽ�趨
			if (controllerInfoList != null && controllerInfoList.size() > 0)
			{
				// ���
				Integer itemNum = 1;

				// �ϲ���Ԫ����ʼֵ
				int mergeCellNumStart = 1;

				// �ϲ���Ԫ������ֵ
				int mergeCellNumEnd = 1;
				String controllerName = "";
				for (ControllerInfo info : controllerInfoList)
				{
					if (controllerName.equals(info.getControllerName()))
					{
						++mergeCellNumEnd;
					}
					else
					{
						if (mergeCellNumEnd != 1)
						{
							// controller�ϲ���Ԫ��
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

					// ÿ����Ԫ���趨ֵ����ʽ
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

				// ���һ��controller�ϲ���Ԫ��
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 0, 0));
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 1, 1));
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 2, 2));
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 3, 3));
				sheet.addMergedRegion(new CellRangeAddress(mergeCellNumStart, mergeCellNumEnd, 6, 6));
			}
		}

		// �Զ���Ӧ�п��
		for (int i = 0; i < wb.getNumberOfSheets(); i++)
		{
			for (int j = 0; j < headerNames.length; j++)
			{
				wb.getSheetAt(i).autoSizeColumn(j, true);
			}
		}
	}

	/**
	 * ����һ����Ԫ��Ϊ���趨���ݺ���ʽ
	 * 
	 * @param wb
	 *           ������
	 * @param row
	 *           ��
	 * @param column
	 *           ��
	 * @param halign
	 *           ˮƽ������䷽ʽ
	 * @param valign
	 *           ��ֱ������䷽ʽ
	 */
	private static void createCell(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row, int column, short halign, short valign,
			String value)
	{

		HSSFCell contentCell = row.createCell(column);

		contentCell.setCellValue(value);
		HSSFCellStyle cellStyle = wb.createCellStyle(); // ������Ԫ����ʽ
		cellStyle.setAlignment(halign); // ���õ�Ԫ��ˮƽ������䷽ʽ
		cellStyle.setVerticalAlignment(valign); // ���õ�Ԫ��ֱ������䷽ʽ
		HSSFFont font = wb.createFont();
		font.setFontName("΢���ź�");
		cellStyle.setFont(font);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//�ϱ߿�
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //�±߿�
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//��߿�
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//�ұ߿�
		contentCell.setCellStyle(cellStyle); // ���õ�Ԫ����ʽ
	}
}

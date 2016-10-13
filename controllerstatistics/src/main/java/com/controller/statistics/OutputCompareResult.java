package com.controller.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import org.json.JSONObject;

public class OutputCompareResult
{

	public static void main(String[] args)
	{
		outputConsoleInfoToFile();
		
		String txtOutputPath = LoadProperties("result.output.path.name");
		StringBuilder strbdr = new StringBuilder();
		String strLine = "";
		try
		{
			File file = new File(txtOutputPath + "/ControllerStatistics_3adc04c.json");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((strLine = reader.readLine()) != null) 
			{
		      //System.out.println(strLine);
		      strbdr.append(strLine);
		   }
			System.out.println(strbdr.toString());
		   reader.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject obj = new JSONObject(strbdr.toString());
		System.out.println(obj);
		//System.out.println(obj.get("controllerName"));
	}
	
	public static void outputConsoleInfoToFile()
	{
		// 把console里的内容输入到文件中
		String txtOutputPath = LoadProperties("result.output.path.name");
		File outfile = new File(txtOutputPath, "ControllerStatistics_test.json");
		try
		{
			outfile.createNewFile();
			FileOutputStream fos = new FileOutputStream(outfile);
			PrintStream printStream = new PrintStream(fos);
			System.setOut(printStream);
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

}

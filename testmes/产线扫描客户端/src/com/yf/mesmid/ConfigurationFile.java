package com.yf.mesmid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationFile {

	public static String getProfileString(
		String file, 
		String section,
		String variable, 
		String defaultValue) throws IOException {
		String strLine , value = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		boolean isInSecion = false;
		try{ 
			while((strLine = bufferedReader.readLine()) != null) {
				strLine = strLine.trim();
				strLine = strLine.split("[;]")[0];
				Pattern p;
				Matcher m;
				p = Pattern.compile("\\[\\s*.*\\s*\\]");
				m = p.matcher(strLine);
				if(m.matches()) {
					p = Pattern.compile("\\[\\s*" + section + "\\s*\\]");
					m = p.matcher(strLine);
					if(m.matches()) {
						isInSecion = true;
					}else{
						isInSecion = false;
					}
				}
				if(isInSecion == true) {
					strLine = strLine.trim();
					String[] strArray = strLine.split("=");
					if(strArray.length == 1) {
						value = strArray[0].trim();
						if(value.equalsIgnoreCase(variable)) {
							value = "";
							return value;
						}
					}else if(strArray.length == 2) {
						value = strArray[0].trim();
						if(value.equalsIgnoreCase(variable)) {
							value = strArray[1].trim();
							return value;
						}
					}else if(strArray.length > 2) {
						value = strArray[0].trim();
						if(value.equalsIgnoreCase(variable)) {
							value = strLine.substring(strLine.indexOf("=") + 1).trim();
							return value;
					}
				}
			}
		}
	}finally {
		bufferedReader.close();
	}
	return defaultValue;
	}
	
	public static void SqlLog(String strPath, String str)
	{
		try {
			FileOutputStream Output = new FileOutputStream(strPath);
			Output.write(str.getBytes());
			Output.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

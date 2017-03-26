package com.nathan.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import jxl.common.Logger;

public class PropertiesProcesser {
	
	private static Logger logger = Logger.getLogger(PropertiesProcesser.class);
	
	private Properties prop;
	
	public String getProperty(String key) {
		return prop.getProperty(key);
	}

	public void setProperty(String key, String value) {
		prop.put(key, value);
	}
	
	public void loadProperties(String filePath) {
		prop = new Properties();		
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			prop.load(in);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Enumeration<?> en = prop.propertyNames(); // 得到配置文件的名字

		while (en.hasMoreElements()) {
			String strKey = (String) en.nextElement();
			String strValue = prop.getProperty(strKey);
			logger.debug(strKey + "=" + strValue);
		}
	}

	public void saveProperties(String filePath) {
		try {
//			InputStream in = new FileInputStream(filePath);
//
//			prop.load(in);
			backupProperties(filePath);
			OutputStream out = new FileOutputStream(filePath);
//			prop.setProperty("phone", "10086");
			prop.store(out, "The system properties file");
			out.close();
			
			logger.debug("saveProperties: " + filePath);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void backupProperties(String filePath) {	
		int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(filePath);  
            out = new FileOutputStream(filePath+".backup");  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
        } catch (FileNotFoundException e) {  
        	e.printStackTrace();
        } catch (IOException e) {  
        	e.printStackTrace();
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        } 
	}

	public static void main(String[] args) {
		PropertiesProcesser processer = new PropertiesProcesser();
		String filePath = "properties\\setting.properties";
		processer.loadProperties(filePath);
		processer.setProperty("user.highTemperatureAllowance", String.valueOf(90));
		processer.setProperty("user.制表人", "何娜");
		processer.saveProperties(filePath);

	}

}

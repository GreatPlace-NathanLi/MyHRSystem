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

import org.apache.log4j.Logger;

public class PropertiesProcesser {
	
	private static Logger logger = Logger.getLogger(PropertiesProcesser.class);
//	user.highTemperatureAllowance=90
//			user.highTemperatureAllowanceMonths=6,7,8,9,10
//			user.individualIncomeTaxThreshold=3500
//			user.billingWay=A
//			user.\u5236\u8868\u4EBA=\u4F55\u5A1C
//			user.socialSecurityAmount=300
	
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
			logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
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
        	logger.error(e.getMessage(), e);
        } catch (IOException e) {  
        	logger.error(e.getMessage(), e);
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
            	logger.error(e.getMessage(), e);
            }  
        } 
	}
	
//	private void nioTransferCopy(File source, File target) {
//		FileChannel in = null;
//		FileChannel out = null;
//		FileInputStream inStream = null;
//		FileOutputStream outStream = null;
//		try {
//			inStream = new FileInputStream(source);
//			outStream = new FileOutputStream(target);
//			in = inStream.getChannel();
//			out = outStream.getChannel();
//			in.transferTo(0, in.size(), out);
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//		} finally {
//			try {
//				inStream.close();
//				in.close();
//				outStream.close();
//				out.close();
//			} catch (IOException e) {
//				logger.error(e.getMessage(), e);
//			}
//
//		}
//	}

	public static void main(String[] args) {
		PropertiesProcesser processer = new PropertiesProcesser();
		String filePath = "properties\\setting.properties";
		processer.loadProperties(filePath);
		processer.setProperty("user.highTemperatureAllowance", String.valueOf(90));
		processer.setProperty("user.制表人", "何娜");
		processer.saveProperties(filePath);

	}

}

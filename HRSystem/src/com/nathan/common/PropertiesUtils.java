package com.nathan.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 从properties配置中取值并打印日志，注意:读取完毕要手动调用close方法.
 * 
 */

public class PropertiesUtils {
	/**
	 * 日志对象.
	 */
	private static final Logger logger = Logger.getLogger(PropertiesUtils.class);

	/**
	 * 文件名称
	 */
	private String _confName;

	private InputStream _inputStream;
	private Properties _properties;

	public PropertiesUtils(String confpath) {
		this._confName = confpath;
	}

	public void init() throws Exception {
		_inputStream = new FileInputStream(_confName);
		_properties = new Properties();
		_properties.load(new InputStreamReader(_inputStream, "UTF-8"));
		close();
		Enumeration<?> en = _properties.propertyNames(); // 得到配置文件的名字

		while (en.hasMoreElements()) {
			String strKey = (String) en.nextElement();
			String strValue = _properties.getProperty(strKey);
			logger.debug(strKey + "=" + strValue);
		}
	}

	/**
	 * 从配置中取值.
	 * 
	 * @param key
	 *            取值的key
	 * @return
	 */
	private String getTrimValue(String key) {
		String tmp = _properties.getProperty(key);
		if (null == tmp) {
			return Constant.EMPTY_STRING;
		} else {
			return tmp.trim();
		}
	}

	/**
	 * 取可为空的字符串.
	 * 
	 * @param key
	 *            取值key
	 * @return ""/trim后的结果
	 */
	public String getStringEnEmpty(String key) {
		String tmp = getTrimValue(key);
		logger.info("Read [" + key + "] ==> [" + tmp + "]");
		return tmp;
	}

	/**
	 * 取不可为空字符串.
	 * 
	 * @param key
	 *            取值key
	 * @return ""/trim后的结果
	 * @throws Exception
	 *             取得结果为空.
	 */
	public String getStringDisEmpty(String key) throws Exception {
		String tmp = getTrimValue(key);
		logger.info("Read [" + key + "] ==> [" + tmp + "]");
		if (tmp.isEmpty()) {
			throw new Exception("[" + key + "] value is Empty!");
		}
		return tmp;
	}

	/**
	 * 取字符串格式的默认值.
	 * 
	 * @param key
	 *            取值key,不为null即可
	 * @param defaultvalue
	 *            默认值,可为null
	 */
	public String getStringValue(String key, String defaultvalue) {
		String tmp = getTrimValue(key);
		if (tmp == null || Constant.EMPTY_STRING.equals(tmp)) {
			logger.info("Read [" + key + "] ==> [" + tmp + "] isEmpty,use defalut value:[" + defaultvalue + "]");
			tmp = defaultvalue;
		} else {
			logger.info("Read [" + key + "] ==> [" + tmp + "]");
		}
		return tmp;
	}

	/**
	 * 取整形，为空或者不合法会抛出异常.
	 * 
	 * @param key
	 * @param defalutvalue
	 * @return
	 */
	public int getIntValue(String key) {
		String tmpvalue = getTrimValue(key);
		int num = 0;
		num = Integer.parseInt(tmpvalue);
		logger.info("Read [" + key + "] ==> [" + num + "]");
		return num;
	}

	/**
	 * 取整形，带默认值.取不到或者不合法则取默认值
	 * 
	 * @param key
	 * @param defalutvalue
	 * @return
	 */
	public int getIntValue(String key, int defalutvalue) {
		String tmpvalue = getTrimValue(key);
		int num = 0;
		try {
			num = Integer.parseInt(tmpvalue);
			logger.info("Read [" + key + "] ==> [" + num + "]");
		} catch (Exception e) {
			num = defalutvalue;
			logger.warn("Read [" + key + "] ==> [" + tmpvalue + "] ,use default value:[" + num + "]");
		}
		return num;
	}

	/**
	 * 取范围内的值.
	 * 
	 * @param key
	 *            取值的key.
	 * @param minnum
	 *            允许的最小值.
	 * @param maxnum
	 *            允许的最大值.
	 * @return
	 * @throws Exception
	 *             不是数字/范围不合法.
	 */
	public int getIntValue(String key, int minnum, int maxnum) throws Exception {
		String tmpvalue = getTrimValue(key);
		int num = 0;
		try {
			num = Integer.parseInt(tmpvalue);
			if (num >= minnum && num <= maxnum) {
				// 合法则到此结束.
				logger.info("Read [" + key + "] ==> [" + num + "]");
				return num;
			}
		} catch (Exception e) {
		}
		throw new Exception("Read [" + key + "] ==> [" + tmpvalue + "] not in [" + minnum + "," + maxnum + "]");
	}

	/**
	 * 取长整形，带默认值.
	 * 
	 * @param key
	 * @param defalutvalue
	 * @return
	 */
	public long getLongValue(String key, long defalutvalue) {
		String tmpvalue = getTrimValue(key);
		long num = 0;
		try {
			num = Long.parseLong(tmpvalue);
			logger.info("Read [" + key + "] ==> [" + num + "]");
		} catch (Exception e) {
			num = defalutvalue;
			logger.warn("Read [" + key + "] ==> [" + tmpvalue + "] ,use default value:[" + num + "]");
		}
		return num;
	}

	/**
	 * 取范围内的值.
	 * 
	 * @param key
	 *            取值的key.
	 * @param minnum
	 *            允许的最小值.
	 * @param maxnum
	 *            允许的最大值.
	 * @return
	 * @throws Exception
	 *             不是数字/范围不合法.
	 */
	public long getLongValue(String key, long minnum, long maxnum) throws Exception {
		String tmpvalue = getTrimValue(key);
		long num = 0;
		try {
			num = Long.parseLong(tmpvalue);
			if (num >= minnum && num <= maxnum) {
				// 合法则到此结束.
				logger.info("Read [" + key + "] ==> [" + num + "]");
				return num;
			}
		} catch (Exception e) {
		}
		throw new Exception("Read [" + key + "] ==> [" + tmpvalue + "] not in [" + minnum + "," + maxnum + "]");
	}

	/**
	 * 关闭文件流,需要手动关闭!
	 */
	public void close() {
		if (null != _inputStream) {
			try {
				_inputStream.close();
			} catch (IOException e) {
			}
		}
	}

	public static void main(String[] args) {
		PropertiesUtils proputil = new PropertiesUtils("F:/work/project/conf/setting.properties");
		try {
			proputil.init();
			
			// 取key为"key1"的值，取不到则默认为"all"
			proputil.getStringValue("user.个税起征点", "all");
			// 取key为"name"的值，该值不能为空
			proputil.getStringDisEmpty("user.高温补贴月份");

			proputil.getIntValue("user.高温补贴金额");

			proputil.getStringEnEmpty("user.制表人");
		} catch (Exception e) {
			e.printStackTrace();
			// 配置文件读取异常，程序退出
			return;
		} finally {
			proputil.close();
		}
	}

}

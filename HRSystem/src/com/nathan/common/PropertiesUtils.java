package com.nathan.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * ��properties������ȡֵ����ӡ��־��ע��:��ȡ���Ҫ�ֶ�����close����.
 * 
 */

public class PropertiesUtils {
	/**
	 * ��־����.
	 */
	private static final Logger logger = Logger.getLogger(PropertiesUtils.class);

	/**
	 * �ļ�����
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
		Enumeration<?> en = _properties.propertyNames(); // �õ������ļ�������

		while (en.hasMoreElements()) {
			String strKey = (String) en.nextElement();
			String strValue = _properties.getProperty(strKey);
			logger.debug(strKey + "=" + strValue);
		}
	}

	/**
	 * ��������ȡֵ.
	 * 
	 * @param key
	 *            ȡֵ��key
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
	 * ȡ��Ϊ�յ��ַ���.
	 * 
	 * @param key
	 *            ȡֵkey
	 * @return ""/trim��Ľ��
	 */
	public String getStringEnEmpty(String key) {
		String tmp = getTrimValue(key);
		logger.info("Read [" + key + "] ==> [" + tmp + "]");
		return tmp;
	}

	/**
	 * ȡ����Ϊ���ַ���.
	 * 
	 * @param key
	 *            ȡֵkey
	 * @return ""/trim��Ľ��
	 * @throws Exception
	 *             ȡ�ý��Ϊ��.
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
	 * ȡ�ַ�����ʽ��Ĭ��ֵ.
	 * 
	 * @param key
	 *            ȡֵkey,��Ϊnull����
	 * @param defaultvalue
	 *            Ĭ��ֵ,��Ϊnull
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
	 * ȡ���Σ�Ϊ�ջ��߲��Ϸ����׳��쳣.
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
	 * ȡ���Σ���Ĭ��ֵ.ȡ�������߲��Ϸ���ȡĬ��ֵ
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
	 * ȡ��Χ�ڵ�ֵ.
	 * 
	 * @param key
	 *            ȡֵ��key.
	 * @param minnum
	 *            �������Сֵ.
	 * @param maxnum
	 *            ��������ֵ.
	 * @return
	 * @throws Exception
	 *             ��������/��Χ���Ϸ�.
	 */
	public int getIntValue(String key, int minnum, int maxnum) throws Exception {
		String tmpvalue = getTrimValue(key);
		int num = 0;
		try {
			num = Integer.parseInt(tmpvalue);
			if (num >= minnum && num <= maxnum) {
				// �Ϸ��򵽴˽���.
				logger.info("Read [" + key + "] ==> [" + num + "]");
				return num;
			}
		} catch (Exception e) {
		}
		throw new Exception("Read [" + key + "] ==> [" + tmpvalue + "] not in [" + minnum + "," + maxnum + "]");
	}

	/**
	 * ȡ�����Σ���Ĭ��ֵ.
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
	 * ȡ��Χ�ڵ�ֵ.
	 * 
	 * @param key
	 *            ȡֵ��key.
	 * @param minnum
	 *            �������Сֵ.
	 * @param maxnum
	 *            ��������ֵ.
	 * @return
	 * @throws Exception
	 *             ��������/��Χ���Ϸ�.
	 */
	public long getLongValue(String key, long minnum, long maxnum) throws Exception {
		String tmpvalue = getTrimValue(key);
		long num = 0;
		try {
			num = Long.parseLong(tmpvalue);
			if (num >= minnum && num <= maxnum) {
				// �Ϸ��򵽴˽���.
				logger.info("Read [" + key + "] ==> [" + num + "]");
				return num;
			}
		} catch (Exception e) {
		}
		throw new Exception("Read [" + key + "] ==> [" + tmpvalue + "] not in [" + minnum + "," + maxnum + "]");
	}

	/**
	 * �ر��ļ���,��Ҫ�ֶ��ر�!
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
			
			// ȡkeyΪ"key1"��ֵ��ȡ������Ĭ��Ϊ"all"
			proputil.getStringValue("user.��˰������", "all");
			// ȡkeyΪ"name"��ֵ����ֵ����Ϊ��
			proputil.getStringDisEmpty("user.���²����·�");

			proputil.getIntValue("user.���²������");

			proputil.getStringEnEmpty("user.�Ʊ���");
		} catch (Exception e) {
			e.printStackTrace();
			// �����ļ���ȡ�쳣�������˳�
			return;
		} finally {
			proputil.close();
		}
	}

}

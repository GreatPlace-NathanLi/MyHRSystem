package com.nathan.service;

import org.apache.log4j.Logger;

import com.jacob.com.Variant;
import com.nathan.common.Util;

import jp.ne.so_net.ga2.no_ji.jcom.ReleaseManager;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelApplication;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorkbook;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorkbooks;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorksheet;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorksheets;

public class JcomPrinter implements ExcelPrinter {

	private static Logger logger = Logger.getLogger(JcomPrinter.class);

	/*
	 * Excel ��ӡ���� 
	 * ����1-��ֵ����ʼҳ�ţ�ʡ����Ĭ��Ϊ��ʼλ�� 
	 * ����2-��ֵ����ֹҳ�ţ�ʡ����Ĭ��Ϊ���һҳ
	 * ����3-��ֵ����ӡ������ʡ����Ĭ��Ϊ1�� 
	 * ����4-�߼�ֵ���Ƿ�Ԥ����ʡ����Ĭ��Ϊֱ�Ӵ�ӡ(.F.)
	 * ����5-�ַ�ֵ�����û��ӡ�����ƣ�ʡ����ΪĬ�ϴ�ӡ��
	 * ����6-�߼�ֵ���Ƿ�������ļ���ʡ����Ĭ��Ϊ��(.F.)����ѡ.T.�Ҳ���8Ϊ�գ���Excel��ʾ����Ҫ������ļ���
	 * ����7-�߼�ֵ��������ͣ�ʡ����Ĭ��Ϊ(.T.)��ݴ�ӡ��������ҳ��ӡ 
	 * ����8-�ַ�ֵ��������6Ϊ.T.ʱ������Ҫ��ӡ�����ļ���
	 * 
	 */
	public boolean printExcel(String path, int fromSheetIndex, int toSheetIndex, int copies) {
		boolean returnFlag = false;
		ReleaseManager rm = new ReleaseManager();
		try {
			if (Util.isFileExists(path)) {
				logger.info("��ʼ��ӡ: " + path);
				ExcelApplication excel = new ExcelApplication(rm);
				ExcelWorkbooks xlBooks = excel.Workbooks();
				ExcelWorkbook xlBook = xlBooks.Open(path);
				ExcelWorksheet xlSheet = excel.ActiveSheet();
				ExcelWorksheets xlSheets = xlBook.Worksheets();

				Object[] argsList = new Object[8];
				argsList[0] = Variant.VT_MISSING;
				argsList[1] = Variant.VT_MISSING;
				argsList[2] = new Integer(copies);
				argsList[3] = new Boolean(false);
				argsList[4] = Variant.VT_MISSING;
				argsList[5] = new Boolean(false);
				argsList[6] = Variant.VT_MISSING;
				argsList[7] = Variant.VT_MISSING;

				for (int i = fromSheetIndex; i <= toSheetIndex; i++) {
					xlSheet = xlSheets.Item(i);
					logger.info("���ڴ�ӡ: " + xlSheet.get("name"));
					xlSheet.method("PrintOut", argsList);
				}
				xlBook.Close(false, null, false);
				excel.Quit();
			}
			returnFlag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rm.release();
			rm = null;
		}
		logger.info("��ӡ���!");
		return returnFlag;
	}


	public static void main(String[] args) throws Exception {
		logger.info("��ʼ��ӡ...");
		String path = "F:/work/project/��ʢ������Ŀ����ϵͳ/out/�⴨����ξ��ռ��16-0289������������.xls";
		JcomPrinter printer = new JcomPrinter();
		printer.printExcel(path, 1, 1, 1);		
		logger.info("��ӡ���!");
	}


	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
}

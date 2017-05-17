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
	 * Excel 打印参数 
	 * 参数1-数值：起始页号，省略则默认为开始位置 
	 * 参数2-数值：终止页号，省略则默认为最后一页
	 * 参数3-数值：打印份数，省略则默认为1份 
	 * 参数4-逻辑值：是否预览，省略则默认为直接打印(.F.)
	 * 参数5-字符值：设置活动打印机名称，省略则为默认打印机
	 * 参数6-逻辑值：是否输出到文件，省略则默认为否(.F.)，若选.T.且参数8为空，则Excel提示输入要输出的文件名
	 * 参数7-逻辑值：输出类型，省略则默认为(.T.)逐份打印，否则逐页打印 
	 * 参数8-字符值：当参数6为.T.时，设置要打印到的文件名
	 * 
	 */
	public boolean printExcel(String path, int fromSheetIndex, int toSheetIndex, int copies) {
		boolean returnFlag = false;
		ReleaseManager rm = new ReleaseManager();
		try {
			if (Util.isFileExists(path)) {
				logger.info("开始打印: " + path);
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
					logger.info("正在打印: " + xlSheet.get("name"));
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
		logger.info("打印完成!");
		return returnFlag;
	}


	public static void main(String[] args) throws Exception {
		logger.info("开始打印...");
		String path = "F:/work/project/德盛人力项目管理系统/out/吴川电力尉迟占军16-0289付款手续单据.xls";
		JcomPrinter printer = new JcomPrinter();
		printer.printExcel(path, 1, 1, 1);		
		logger.info("打印完成!");
	}


	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
}

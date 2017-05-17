package com.nathan.service;

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.nathan.common.Util;

public class JacobPrinter implements ExcelPrinter {

	private static Logger logger = Logger.getLogger(JacobPrinter.class);
	
	private ActiveXComponent xl = null;

	public static void main(String[] args) throws Exception {
		String path = "F:/work/project/德盛人力项目管理系统/out/吴川电力尉迟占军16-0289考勤表.xls";
		JacobPrinter printer = new JacobPrinter();
		printer.printExcel(path, 2, 2, 1);
	}

	public JacobPrinter() {
		 init();
	}
	
	public void init() {
		logger.info("初始化打印机通道");
		ComThread.InitSTA();
		xl = new ActiveXComponent("Excel.Application");
	}
	
	public void close() {
		if (xl != null) {
			ComThread.Release();
			logger.info("释放打印机资源");
			xl = null;
		}	
	}
	
	public boolean printExcel(String filePath, int fromSheetIndex, int toSheetIndex, int copies) {
		boolean returnFlg = false;
		try {
			if (Util.isFileExists(filePath)) {
				logger.info("开始打印: " + filePath);
				if (xl == null) {
					init();
				}

				// 不打开文档
				Dispatch.put(xl, "Visible", new Variant(false));
				Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();

				Object[] argsList = new Object[8];
				argsList[0] = Variant.VT_MISSING;
				argsList[1] = Variant.VT_MISSING;
				argsList[2] = new Integer(copies);
				argsList[3] = new Boolean(false);
				argsList[4] = Variant.VT_MISSING;
				argsList[5] = new Boolean(false);
				argsList[6] = Variant.VT_MISSING;
				argsList[7] = Variant.VT_MISSING;

				Dispatch excel = Dispatch.call(workbooks, "Open", filePath).toDispatch();
				Dispatch sheets = Dispatch.get((Dispatch) excel, "Sheets").toDispatch();
				int count = Dispatch.get(sheets, "Count").getInt();
				logger.debug("sheet count:" + count);

				for (int i = fromSheetIndex; i <= toSheetIndex; i++) {
					Dispatch sheet = Dispatch
							.invoke(sheets, "Item", Dispatch.Get, new Object[] { new Integer(i) }, new int[1])
							.toDispatch();
					Variant sheetName = Dispatch.get(sheet, "name");
					logger.info("正在打印: " + sheetName);
					Dispatch.call(sheet, "PrintOut", argsList);
					Dispatch.call(excel, "save");  
				    Dispatch.call(excel, "Close" ,  new  Variant(true));  
				    excel=null;  
				}
				
				xl.invoke("Quit", new Variant[] {});  
				returnFlg = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		logger.info("打印完成!");
		return returnFlg;
	}

}

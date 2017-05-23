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
	
	private Dispatch workbooks = null;  //工作簿对象  
    private Dispatch workbook = null; //具体工作簿  
    private Dispatch sheets = null;// 获得sheets集合对象  
    private Dispatch sheet = null;// 当前sheet  

	public static void main(String[] args) throws Exception {
		String path = "F:/work/project/德盛人力项目管理系统/out/吴川电力曾明17-020付款手续单据.xls";
		JacobPrinter printer = new JacobPrinter();
		printer.printExcel(path, 1, 3, 1);
	}

	public JacobPrinter() {
		 init();
	}
	
	public void init() {
		logger.info("初始化打印机通道");
		ComThread.Release();
		ComThread.InitSTA();
		workbooks = null;  //工作簿对象  
	    workbook = null; //具体工作簿  
	    sheets = null;// 获得sheets集合对象  
	    sheet = null;// 当前sheet  
		xl = new ActiveXComponent("Excel.Application");
	}
	
	public void close() {
		releaseSource();
		logger.info("释放打印机资源");
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
				workbooks = xl.getProperty("Workbooks").toDispatch();

				Object[] argsList = new Object[8];
				argsList[0] = Variant.VT_MISSING;
				argsList[1] = Variant.VT_MISSING;
				argsList[2] = new Integer(copies);
				argsList[3] = new Boolean(false);
				argsList[4] = Variant.VT_MISSING;
				argsList[5] = new Boolean(false);
				argsList[6] = Variant.VT_MISSING;
				argsList[7] = Variant.VT_MISSING;

				workbook = Dispatch.call(workbooks, "Open", filePath).toDispatch();
				sheets = Dispatch.get((Dispatch) workbook, "Sheets").toDispatch();
				int count = Dispatch.get(sheets, "Count").getInt();
				logger.debug("sheet count:" + count);

				for (int i = fromSheetIndex; i <= toSheetIndex; i++) {
					sheet = Dispatch
							.invoke(sheets, "Item", Dispatch.Get, new Object[] { new Integer(i) }, new int[1])
							.toDispatch();
					Variant sheetName = Dispatch.get(sheet, "name");
					logger.info("正在打印: " + sheetName);
					Dispatch.call(sheet, "PrintOut", argsList);
					Dispatch.call(workbook, "save");  
				    Dispatch.call(workbook, "Close" ,  new  Variant(false));  
				}
				
				returnFlg = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
//			close();
		}
		logger.info("打印完成!");
		return returnFlg;
	}
	
    /** 
     * 释放资源 
     */  
    private void releaseSource(){  
        if(xl!=null){  
        	logger.debug("ActiveXComponent Quit");
            xl.invoke("Quit", new Variant[] {});  
            xl = null;  
        }  
        workbooks = null;  //工作簿对象  
	    workbook = null; //具体工作簿  
	    sheets = null;// 获得sheets集合对象  
	    sheet = null;// 当前sheet  
        ComThread.Release();  
        System.gc();  
    }  
    
}

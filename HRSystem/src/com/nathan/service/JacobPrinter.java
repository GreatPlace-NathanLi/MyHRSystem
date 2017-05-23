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
	
	private Dispatch workbooks = null;  //����������  
    private Dispatch workbook = null; //���幤����  
    private Dispatch sheets = null;// ���sheets���϶���  
    private Dispatch sheet = null;// ��ǰsheet  

	public static void main(String[] args) throws Exception {
		String path = "F:/work/project/��ʢ������Ŀ����ϵͳ/out/�⴨��������17-020������������.xls";
		JacobPrinter printer = new JacobPrinter();
		printer.printExcel(path, 1, 3, 1);
	}

	public JacobPrinter() {
		 init();
	}
	
	public void init() {
		logger.info("��ʼ����ӡ��ͨ��");
		ComThread.Release();
		ComThread.InitSTA();
		workbooks = null;  //����������  
	    workbook = null; //���幤����  
	    sheets = null;// ���sheets���϶���  
	    sheet = null;// ��ǰsheet  
		xl = new ActiveXComponent("Excel.Application");
	}
	
	public void close() {
		releaseSource();
		logger.info("�ͷŴ�ӡ����Դ");
	}
	
	public boolean printExcel(String filePath, int fromSheetIndex, int toSheetIndex, int copies) {
		boolean returnFlg = false;
		try {
			if (Util.isFileExists(filePath)) {
				logger.info("��ʼ��ӡ: " + filePath);
				if (xl == null) {
					init();
				}

				// �����ĵ�
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
					logger.info("���ڴ�ӡ: " + sheetName);
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
		logger.info("��ӡ���!");
		return returnFlg;
	}
	
    /** 
     * �ͷ���Դ 
     */  
    private void releaseSource(){  
        if(xl!=null){  
        	logger.debug("ActiveXComponent Quit");
            xl.invoke("Quit", new Variant[] {});  
            xl = null;  
        }  
        workbooks = null;  //����������  
	    workbook = null; //���幤����  
	    sheets = null;// ���sheets���϶���  
	    sheet = null;// ��ǰsheet  
        ComThread.Release();  
        System.gc();  
    }  
    
}

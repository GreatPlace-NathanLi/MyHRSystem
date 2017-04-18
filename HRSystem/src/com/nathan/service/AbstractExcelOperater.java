package com.nathan.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;
import jxl.write.WritableWorkbook;

public abstract class AbstractExcelOperater implements ExcelOperater {

	private static Logger logger = Logger.getLogger(AbstractExcelOperater.class);

	@Override
	public void read(String filePath) {

		Workbook readwb = null;

		try {
			// ֱ�Ӵӱ����ļ�����Workbook
			InputStream instream = new FileInputStream(filePath);

			readwb = Workbook.getWorkbook(instream);

			readContent(readwb);

			instream.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readwb.close();
		}

	}

	protected void readContent(Workbook readwb) throws Exception {
		Sheet readsheet = readwb.getSheet(0);

		int rsColumns = readsheet.getColumns();
		int rsRows = readsheet.getRows();

		logger.debug("��������" + rsColumns + ", ��������" + rsRows);

		// �������е�Ԫ��

		for (int i = 0; i < rsRows; i++) {

			for (int j = 0; j < rsColumns; j++) {

				Cell cell = readsheet.getCell(j, i);

				logger.debug(cell.getContents() + " ");

				logger.debug(cell.getType());
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.nathan.service.ExcelOperater#write(java.lang.String)
	 */
	@Override
	public void write(String outFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String inFile, String outFile) throws Exception {
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			File inputFile = new File(inFile);
			rwb = Workbook.getWorkbook(inputFile);
			File outputFille = new File(outFile);
			wwb = Workbook.createWorkbook(outputFille, rwb);// copy
			
			writeContent(wwb);
	
			wwb.write();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwb.close();
			wwb.close();
		}	
	}
	
	protected void writeContent(WritableWorkbook wwb) throws Exception {
		
	}

	@Override
	public void modify(String destFile) throws Exception {	
        Workbook rwb = null;
		WritableWorkbook wwb = null;
		boolean needToDelete = false;
		try {
			rwb = Workbook.getWorkbook(new File(destFile));
			wwb = Workbook.createWorkbook(new File(destFile), rwb);
			
			modifyContent(wwb);
			
			if(wwb.getNumberOfSheets() == 0) {
				logger.info("������Ϊ�㣬ɾ���ļ���" + destFile);
				needToDelete = true;
				return;
			}
	
			wwb.write();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwb.close();
			wwb.close();
			if (needToDelete) {
				delete(destFile);
			}
		}	
	}
	
	protected void modifyContent(WritableWorkbook wwb) throws Exception {
		
	}

	@Override
	public boolean delete(String fillPath) {
		boolean flag = false;
//		boolean isFilePathValid = fillPath.matches(Constant.MATCHES);
//		logger.debug("�Ƿ�Ϊ��ȷ��·����:" + isFilePathValid);
//		if (!isFilePathValid) {
//			fillPath = fillPath.replaceAll("/", "\\\\");
//		}
		File file = new File(fillPath);
		if (file.isFile() && file.exists()) {
			flag = file.delete();
//			logger.debug(flag + "ɾ���ļ���" + fillPath);
		}
		return flag;
	}
	
}

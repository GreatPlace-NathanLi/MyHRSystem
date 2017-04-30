package com.nathan.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public abstract class AbstractExcelOperater implements ExcelOperater {

	private static Logger logger = Logger.getLogger(AbstractExcelOperater.class);

	private boolean backupFlag = false;

	protected void setBackupFlag(boolean flag) {
		this.backupFlag = flag;
	}

	@Override
	public void read(String filePath) throws Exception {

		Workbook readwb = null;

		try {
			// ֱ�Ӵӱ����ļ�����Workbook
			InputStream instream = new FileInputStream(filePath);

			readwb = Workbook.getWorkbook(instream);

			readContent(readwb);

			instream.close();

		} finally {
			close(readwb, null);
		}

	}

	protected void readContent(Workbook readwb) throws Exception {
		for (Sheet readsheet : readwb.getSheets()) {

			int rsColumns = readsheet.getColumns();
			int rsRows = readsheet.getRows();
			logger.debug("��ȡ���" + readsheet.getName());
			logger.debug("��������" + rsColumns + ", ��������" + rsRows);

			// �������е�Ԫ��

			for (int i = 0; i < rsRows; i++) {

				for (int j = 0; j < rsColumns; j++) {

					Cell cell = readsheet.getCell(j, i);

					logger.debug(cell.getContents() + " c:" + j + " r:" + i);

					logger.debug(cell.getType());
				}
			}
		}
	}

	protected void logInfo(WritableSheet sheet) {
		int rsColumns = sheet.getColumns();
		int rsRows = sheet.getRows();
		logger.debug("�������" + sheet.getName());
		logger.debug("��������" + rsColumns + ", ��������" + rsRows);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nathan.service.ExcelOperater#write(java.lang.String)
	 */
	@Override
	public void write(String outFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String inFile, String outFile) throws Exception {
		if (backupFlag) {
			backup(inFile);
		}
		preWrite(outFile);
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			try {
				File inputFile = new File(outFile);
				rwb = Workbook.getWorkbook(inputFile);
			} catch (FileNotFoundException e) {
				rwb = Workbook.getWorkbook(new File(inFile));
			}

			File outputFille = new File(outFile);
			wwb = Workbook.createWorkbook(outputFille, rwb);// copy

			writeContent(wwb);

			wwb.write();

		} finally {
			close(rwb, wwb);
		}
	}
	
	private void close(Workbook rwb, WritableWorkbook wwb) throws Exception {
		if (rwb != null) {
			rwb.close();
		}
		if (wwb != null) {
			wwb.close();
		}	
	}
	
	protected void preWrite(String outFile) {
		
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {

	}

	@Override
	public void modify(String destFile) throws Exception {
		if (backupFlag) {
			backup(destFile);
		}
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		boolean needToDelete = false;
		try {
			rwb = Workbook.getWorkbook(new File(destFile));
			wwb = Workbook.createWorkbook(new File(destFile), rwb);

			modifyContent(wwb);

			if (isNeededToDeleteFile(wwb)) {
				logger.info("������Ϊ�㣬ɾ���ļ���" + destFile);
				needToDelete = true;
				return;
			}

			wwb.write();

		} finally {
			close(rwb, wwb);
			if (needToDelete) {
				delete(destFile);
			}
		}
	}

	protected void modifyContent(WritableWorkbook wwb) throws Exception {

	}
	
	protected boolean isNeededToDeleteFile(WritableWorkbook wwb) {
		return false;
	}

	@Override
	public boolean delete(String fillPath) {
		boolean flag = false;
		// boolean isFilePathValid = fillPath.matches(Constant.MATCHES);
		// logger.debug("�Ƿ�Ϊ��ȷ��·����:" + isFilePathValid);
		// if (!isFilePathValid) {
		// fillPath = fillPath.replaceAll("/", "\\\\");
		// }
		File file = new File(fillPath);
		if (file.isFile() && file.exists()) {
			flag = file.delete();
			// logger.debug(flag + "ɾ���ļ���" + fillPath);
		}
		return flag;
	}

	public void backup(String filePath) throws Exception {

		String outFile = getBackupFilePath(filePath);
		logger.info("�����ļ���" + outFile);

		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			File inputFile = new File(filePath);
			rwb = Workbook.getWorkbook(inputFile);

			File outputFille = new File(outFile);
			wwb = Workbook.createWorkbook(outputFille, rwb);// copy

			wwb.write();

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} finally {
			close(rwb, wwb);
		}
	}

	private String getBackupFilePath(String filePath) {
		String backupPath = Constant.propUtil.getStringEnEmpty("user.�ļ�����·��");
		return backupPath + Util.getFileNameFromPath(filePath) + ".backup." + System.currentTimeMillis();
	}

}

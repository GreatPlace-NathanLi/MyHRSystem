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
	public void read(String filePath) {

		Workbook readwb = null;

		try {
			// 直接从本地文件创建Workbook
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
		for (Sheet readsheet : readwb.getSheets()) {

			int rsColumns = readsheet.getColumns();
			int rsRows = readsheet.getRows();
			logger.debug("读取表格：" + readsheet.getName());
			logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

			// 遍历所有单元格

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
		logger.debug("创建表格：" + sheet.getName());
		logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);
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

			if (wwb.getNumberOfSheets() == 0) {
				logger.info("表数量为零，删除文件：" + destFile);
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
		// boolean isFilePathValid = fillPath.matches(Constant.MATCHES);
		// logger.debug("是否为正确的路径名:" + isFilePathValid);
		// if (!isFilePathValid) {
		// fillPath = fillPath.replaceAll("/", "\\\\");
		// }
		File file = new File(fillPath);
		if (file.isFile() && file.exists()) {
			flag = file.delete();
			// logger.debug(flag + "删除文件：" + fillPath);
		}
		return flag;
	}

	public void backup(String filePath) throws Exception {

		String outFile = getBackupFilePath(filePath);
		logger.info("备份文件：" + outFile);

		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			File inputFile = new File(filePath);
			rwb = Workbook.getWorkbook(inputFile);

			File outputFille = new File(outFile);
			wwb = Workbook.createWorkbook(outputFille, rwb);// copy

			wwb.write();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwb.close();
			wwb.close();
		}
	}

	private String getBackupFilePath(String filePath) {
		String backupPath = Constant.propUtil.getStringEnEmpty("user.文件备份路径");
		return backupPath + Util.getFileNameFromPath(filePath) + ".backup." + System.currentTimeMillis();
	}

}

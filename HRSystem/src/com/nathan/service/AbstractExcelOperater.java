package com.nathan.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;
import com.nathan.view.InteractionHandler;

import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public abstract class AbstractExcelOperater implements ExcelOperater {

	private static Logger logger = Logger.getLogger(AbstractExcelOperater.class);

	private boolean backupFlag = false;
	
	private boolean writeRetryFlag = false;

	protected void setBackupFlag(boolean flag) {
		this.backupFlag = flag;
	}

	public void setWriteRetryFlag(boolean writeRetryFlag) {
		this.writeRetryFlag = writeRetryFlag;
	}
	
	public boolean isWriteRetry() {
		return this.writeRetryFlag;
	}

	@Override
	public void read(String filePath) throws Exception {

		Workbook readwb = null;

		try {
			// 直接从本地文件创建Workbook
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
			logger.debug("读取表格：" + readsheet.getName());
			logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

			// 遍历所有单元格

			for (int i = 0; i < rsRows; i++) {

				for (int j = 0; j < rsColumns; j++) {

					Cell cell = readsheet.getCell(j, i);

					if (cell.getType() != CellType.EMPTY) {
						logger.debug(cell.getContents() + " c:" + j + " r:" + i);
						logger.debug(cell.getType());
					}
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
			backup(outFile);
		}
		preWrite(outFile);
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			try {
				//如果输出文件已存在，以已有文件为模板
				File inputFile = new File(outFile);
				rwb = Workbook.getWorkbook(inputFile);
			} catch (FileNotFoundException e) {
				rwb = Workbook.getWorkbook(new File(inFile));
			}

			File outputFille = new File(outFile);
			wwb = Workbook.createWorkbook(outputFille, rwb);// copy

			writeContent(wwb);

			wwb.write();

		} catch (FileNotFoundException e) {
			writeRetry(e, inFile, outFile);
		} finally {
			close(rwb, wwb);
		}
	}
	
	private void writeRetry(Exception e, String inFile, String outFile) throws Exception {
		if (!e.getMessage().contains("另一个程序正在使用此文件，进程无法访问")) {
			throw e;
		}
		logger.error(e.getMessage(), e);
		if (InteractionHandler.handleWriteRetry(e.getMessage())) {	
			write(inFile, outFile);
			logger.info("文件已被释放，重新保存：" + outFile);
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
				logger.info("表数量为零，删除文件：" + destFile);
				needToDelete = true;
				return;
			}

			wwb.write();

		} catch (FileNotFoundException e) {
			modifyRetry(e, destFile);
		} finally {
			close(rwb, wwb);
			if (needToDelete) {
				delete(destFile);
			}
		}
	}
	
	private void modifyRetry(Exception e, String destFile) throws Exception {
		if (!e.getMessage().contains("另一个程序正在使用此文件，进程无法访问")) {
			throw e;
		}
		logger.error(e.getMessage(), e);
		if (InteractionHandler.handleWriteRetry(e.getMessage())) {	
			modify(destFile);
			logger.info("文件已被释放，重新保存：" + destFile);
		}
	}

	protected void modifyContent(WritableWorkbook wwb) throws Exception {

	}

	protected boolean isNeededToDeleteFile(WritableWorkbook wwb) {
		return false;
	}

	@Override
	public void copy(String fromFile, String toFile) throws Exception {
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			File from = new File(fromFile);
			rwb = Workbook.getWorkbook(from);

			File to = new File(toFile);
			wwb = Workbook.createWorkbook(to, rwb);

			copyContent(wwb);

			wwb.write();

		} finally {
			close(rwb, wwb);
		}
	}
	
	protected void copyContent(WritableWorkbook wwb) throws Exception {

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

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} finally {
			backupFlag = false;
			close(rwb, wwb);
		}
	}

	private String getBackupFilePath(String filePath) {
		String backupPath = Constant.propUtil.getStringEnEmpty("user.文件备份路径");
		return backupPath + Util.getFileNameFromPath(filePath) + ".backup." + System.currentTimeMillis();
	}

	/**
	 * 
	 * @param sheet
	 *            操作对象
	 * @param mergedCell
	 *            所有合并单元格范围
	 * 
	 *            考虑到该方法可能被循环调用多次，而且是复制同一个表格到不同地方，因此本参数记录的原始
	 * 
	 *            被拷贝表格中的单元格，避免在多次循环后，本参数数据量不断增加，导致遍历时间太长。即本参数值需在调用
	 * 
	 *            本方法的循环外就已经获得。
	 *
	 * 
	 * @param from1Cols
	 *            被复制表格开始列
	 * @param from1Row
	 *            被复制表格开始行
	 * @param to1Col
	 *            被复制表格结束列
	 * @param to1Row
	 *            被复制表格结束行
	 * @param from2Col
	 *            复制到表格开始列
	 * @param from2Row
	 *            复制到表格开始行
	 * @return boolean 是否完整
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static boolean copyCells(WritableSheet sheet, Range[] mergedCell, int from1Col, int from1Row, int to1Col,
			int to1Row, int from2Col, int from2Row) throws IOException {

		try {
			// 制作表格，先合并单元格
			for (int i = 0; i < (to1Row - from1Row + 1); i++) {

				// 选中区域下一行
				sheet.insertRow(from2Row + i);
				sheet.setRowView(from2Row + i, sheet.getRowHeight(from1Row + i));
				// 对插入行的列进行处理，即单元格
				for (int j = 0; j < (to1Col - from1Col); j++) {

					CellFormat cf = sheet.getWritableCell(from1Col + j, from1Row + i).getCellFormat();

					String content = sheet.getCell(from1Col + j, from1Row + i).getContents();

					if (cf == null) {
						sheet.addCell(new Label(from1Col + j, from2Row + i, content));
					} else {
						sheet.addCell(new Label(from1Col + j, from2Row + i, content, cf));
					}
				}
			}

			// 合并单元格

			for (int i = 0; i < mergedCell.length; i++) {
				int fromRow = mergedCell[i].getTopLeft().getRow();
				int fromCol = mergedCell[i].getTopLeft().getColumn();

				int toRow = mergedCell[i].getBottomRight().getRow();
				int toCol = mergedCell[i].getBottomRight().getColumn();

				// 如果检测到的合并单元格，在复制表格内，则将对应粘贴表的单元格合并。列数=原列数+表高，列数=原列数
				if (fromRow >= from1Row && fromCol >= from1Col && toRow <= to1Row && toCol <= to1Col) {
					sheet.mergeCells(fromCol, fromRow + from2Row, toCol, toRow + from2Row);
				}
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void copyCell(WritableSheet sheet, int fromCol, int fromRow, int toCol, int toRow) throws Exception {
		CellFormat cf = sheet.getWritableCell(fromCol, fromRow).getCellFormat();
		WritableCellFormat wcf;
		if (cf != null) {
			wcf = new WritableCellFormat(cf);
		} else {
			wcf = new WritableCellFormat();
		}
		formatCell(wcf);
		String content = sheet.getCell(fromCol, fromRow).getContents();
		sheet.addCell(new Label(toCol, toRow, content, wcf));
	}
	
	protected void formatCell(WritableCellFormat wcf) throws Exception {
	}
}

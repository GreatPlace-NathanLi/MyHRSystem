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
			backup(outFile);
		}
		preWrite(outFile);
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			try {
				//�������ļ��Ѵ��ڣ��������ļ�Ϊģ��
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
		if (!e.getMessage().contains("��һ����������ʹ�ô��ļ��������޷�����")) {
			throw e;
		}
		logger.error(e.getMessage(), e);
		if (InteractionHandler.handleWriteRetry(e.getMessage())) {	
			write(inFile, outFile);
			logger.info("�ļ��ѱ��ͷţ����±��棺" + outFile);
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
		if (!e.getMessage().contains("��һ����������ʹ�ô��ļ��������޷�����")) {
			throw e;
		}
		logger.error(e.getMessage(), e);
		if (InteractionHandler.handleWriteRetry(e.getMessage())) {	
			modify(destFile);
			logger.info("�ļ��ѱ��ͷţ����±��棺" + destFile);
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
			logger.error(e.getMessage());
		} finally {
			backupFlag = false;
			close(rwb, wwb);
		}
	}

	private String getBackupFilePath(String filePath) {
		String backupPath = Constant.propUtil.getStringEnEmpty("user.�ļ�����·��");
		return backupPath + Util.getFileNameFromPath(filePath) + ".backup." + System.currentTimeMillis();
	}

	/**
	 * 
	 * @param sheet
	 *            ��������
	 * @param mergedCell
	 *            ���кϲ���Ԫ��Χ
	 * 
	 *            ���ǵ��÷������ܱ�ѭ�����ö�Σ������Ǹ���ͬһ����񵽲�ͬ�ط�����˱�������¼��ԭʼ
	 * 
	 *            ����������еĵ�Ԫ�񣬱����ڶ��ѭ���󣬱������������������ӣ����±���ʱ��̫������������ֵ���ڵ���
	 * 
	 *            ��������ѭ������Ѿ���á�
	 *
	 * 
	 * @param from1Cols
	 *            �����Ʊ��ʼ��
	 * @param from1Row
	 *            �����Ʊ��ʼ��
	 * @param to1Col
	 *            �����Ʊ�������
	 * @param to1Row
	 *            �����Ʊ�������
	 * @param from2Col
	 *            ���Ƶ����ʼ��
	 * @param from2Row
	 *            ���Ƶ����ʼ��
	 * @return boolean �Ƿ�����
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static boolean copyCells(WritableSheet sheet, Range[] mergedCell, int from1Col, int from1Row, int to1Col,
			int to1Row, int from2Col, int from2Row) throws IOException {

		try {
			// ��������Ⱥϲ���Ԫ��
			for (int i = 0; i < (to1Row - from1Row + 1); i++) {

				// ѡ��������һ��
				sheet.insertRow(from2Row + i);
				sheet.setRowView(from2Row + i, sheet.getRowHeight(from1Row + i));
				// �Բ����е��н��д�������Ԫ��
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

			// �ϲ���Ԫ��

			for (int i = 0; i < mergedCell.length; i++) {
				int fromRow = mergedCell[i].getTopLeft().getRow();
				int fromCol = mergedCell[i].getTopLeft().getColumn();

				int toRow = mergedCell[i].getBottomRight().getRow();
				int toCol = mergedCell[i].getBottomRight().getColumn();

				// �����⵽�ĺϲ���Ԫ���ڸ��Ʊ���ڣ��򽫶�Ӧճ����ĵ�Ԫ��ϲ�������=ԭ����+��ߣ�����=ԭ����
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

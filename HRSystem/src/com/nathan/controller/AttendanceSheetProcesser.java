package com.nathan.controller;

import java.util.List;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.exception.AttendanceSheetProcessException;
import com.nathan.model.BillingPlan;
import com.nathan.model.Payroll;
import com.nathan.model.PayrollSheet;
import com.nathan.service.AbstractExcelOperater;

import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class AttendanceSheetProcesser extends AbstractExcelOperater {

	private static Logger logger = Logger.getLogger(AttendanceSheetProcesser.class);

	private List<PayrollSheet> payrollSheetList;

	public AttendanceSheetProcesser() {

	}

	public AttendanceSheetProcesser(BillingPlan billingPlan) {
		removeAttendanceSheetIfExists(buildAttendanceSheetFilePath(billingPlan.getProjectUnit(),
				billingPlan.getProjectLeader(), billingPlan.getContractID()));
	}

	public void processAttendanceSheet(BillingPlan billingPlan, List<PayrollSheet> payrollSheetList)
			throws AttendanceSheetProcessException {
		String attendanceSheetTemplatePath = buildAttendanceSheetTemplatePath();
		logger.info("��ȡ���ڱ�ģ�壺 " + attendanceSheetTemplatePath);
		String outputPath = buildAttendanceSheetFilePath(billingPlan.getProjectUnit(), billingPlan.getProjectLeader(),
				billingPlan.getContractID());
		writeAttendanceSheet(attendanceSheetTemplatePath, outputPath, payrollSheetList);
		logger.info(Constant.LINE1);
		logger.info("���濼�ڱ������ " + outputPath);
		logger.info(Constant.LINE1);
	}

	public void removeAttendanceSheetIfExists(String filePath) {
		if (delete(filePath)) {
			logger.debug("ɾ���ļ���" + filePath);
		}
	}

	public void readAttendanceSheetTemplate(String templatePath) throws AttendanceSheetProcessException {
		try {
			read(templatePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AttendanceSheetProcessException("��ȡ���ڱ�ģ�����" + e.getMessage());
		}
	}

	public void writeAttendanceSheet(String templatePath, String filePath, List<PayrollSheet> payrollSheetList)
			throws AttendanceSheetProcessException {
		this.payrollSheetList = payrollSheetList;
		try {
			write(templatePath, filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AttendanceSheetProcessException("���濼�ڱ����" + e.getMessage());
		}
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {
		if (payrollSheetList == null) {
			return;
		}
		int sheetNumber = wwb.getNumberOfSheets();
		for (int i = 0; i < payrollSheetList.size(); i++) {
			int sheetIndex = i + sheetNumber;
			PayrollSheet payrollSheet = payrollSheetList.get(i);
			wwb.copySheet(0,
					getAttendanceSheetName(payrollSheet.getPayMonth(), payrollSheet.getContractID(), sheetIndex),
					sheetIndex);
			WritableSheet newSheet = wwb.getSheet(sheetIndex);		
			updateTitleAndHeaders(payrollSheet, newSheet);
			fillNameList(payrollSheet, newSheet);
			fillPrintingSettings(newSheet);
		}
	}
	
	private void fillPrintingSettings(WritableSheet newSheet) {
		logger.debug("ֽ�Ŵ�С��" + newSheet.getSettings().getPaperSize());
		logger.debug("��ӡ������" + newSheet.getSettings().getCopies());
		logger.debug("��ӡ���ű�����" + newSheet.getSettings().getScaleFactor());
		newSheet.getSettings().setScaleFactor(100);
	}

	private void updateTitleAndHeaders(PayrollSheet payrollSheet, WritableSheet sheet) throws Exception {
		WritableCell cell = sheet.getWritableCell(0, 0);
		String title = ((Label) cell).getString();
		title = title.replace("YYYY", String.valueOf(payrollSheet.getPayYear()));
		title = title.replace("MM", String.valueOf(payrollSheet.getPayMonth()));
		((Label) cell).setString(title);
		
		for (int c = 0; c < sheet.getColumns(); c++) {				
			WritableCellFormat wcf = new WritableCellFormat();
			formatCell(wcf);

			sheet.addCell(new Label(c, 3, null, wcf));
		}
	}

	private void fillNameList(PayrollSheet payrollSheet, WritableSheet sheet) throws Exception {
		int size = payrollSheet.getPayrollList().size();
		int srcRowIndex = 4;
		int rsColumns = sheet.getColumns();

		for (int r = 0; r < size; r++) {
			int currentRowIndex = r + srcRowIndex + 1;
			Payroll payroll = payrollSheet.getPayrollList().get(r);
			sheet.insertRow(currentRowIndex);
			sheet.setRowView(currentRowIndex, sheet.getRowView(srcRowIndex));

			for (int c = 0; c < rsColumns; c++) {				
				copyCell(sheet, c, srcRowIndex, c, currentRowIndex);
			}
			
			WritableCell cell = sheet.getWritableCell(0, currentRowIndex);
			((Label) cell).setString(payroll.getName());
		}

		sheet.removeRow(srcRowIndex);
	}

	protected void formatCell(WritableCellFormat wcf) throws Exception {
		wcf.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK);
		wcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		wcf.setAlignment(jxl.format.Alignment.CENTRE);
	}
	
	private String getAttendanceSheetName(int month, String contractID, int index) {
		return "��" + month + Constant.DELIMITER1 + contractID + Constant.DELIMITER1 + index;
	}

	private String buildAttendanceSheetFilePath(String company, String projectLeader, String contractID) {
		String attendanceSheetFile = Constant.propUtil.getStringValue("user.���ڱ����·��", Constant.ATTENDANCE_SHEET_FILE);
		String filePath = attendanceSheetFile.replace("UUUU", company);
		filePath = filePath.replace("NNN", projectLeader);
		filePath = filePath.replace("CCCCC", contractID);
		return filePath;
	}

	private String buildAttendanceSheetTemplatePath() {
		String attendanceSheetTemplatePath = Constant.propUtil.getStringValue("user.���ڱ�ģ��·��",
				Constant.ATTENDANCE_SHEET_TEMPLATE_FILE);
		return attendanceSheetTemplatePath;
	}

	public static void main(String[] args) throws Exception {
		AttendanceSheetProcesser attendanceSheetProcesser = new AttendanceSheetProcesser();

		Constant.propUtil.init();
		long startTime = System.nanoTime();

		String attendanceSheetTemplatePath = attendanceSheetProcesser.buildAttendanceSheetTemplatePath();

		logger.info("��ȡ���ڱ�ģ�壺 " + attendanceSheetTemplatePath);
		attendanceSheetProcesser.readAttendanceSheetTemplate(attendanceSheetTemplatePath);

		String outputPath = attendanceSheetProcesser.buildAttendanceSheetFilePath("����", "����", "A101");
		attendanceSheetProcesser.removeAttendanceSheetIfExists(outputPath);
		attendanceSheetProcesser.writeAttendanceSheet(attendanceSheetTemplatePath, outputPath, null);
		logger.info("���濼�ڱ������ " + outputPath);

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("���ڱ��д������ ��ʱ��" + (endTime - startTime) / 1000000 + "����");
		logger.info(Constant.LINE0);
	}
}

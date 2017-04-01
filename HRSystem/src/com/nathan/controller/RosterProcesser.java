package com.nathan.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.nathan.common.Constant;
import com.nathan.exception.RosterProcessException;
import com.nathan.model.ProjectMember;
import com.nathan.model.ProjectMemberRoster;
import com.nathan.model.RosterMonthStatistics;
import com.nathan.model.RosterStatistics;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class RosterProcesser {

	private static Logger logger = Logger.getLogger(RosterProcesser.class);

	private ProjectMemberRoster roster;

	public void processRoster(String projectLeader, int year)
			throws RosterProcessException, WriteException, IOException {
		String inputPath = Constant.ROSTER_FILE.replace("NNN", projectLeader).replace("YYYY", String.valueOf(year));
		logger.info("步骤4.1 - 读取花名册： " + inputPath);
		roster = new ProjectMemberRoster();
		roster.setLocation(inputPath);
		roster.setCurrentPayYear(year);
		readProjectMemberRoster(inputPath);
		logger.info(Constant.LINE1);
		String outputPath = inputPath.replace("/in/", "/out/out");
		logger.info("步骤4.2 - 保存花名册： " + outputPath);
		writeProjectMemberRoster(inputPath, outputPath);
	}

	public void readProjectMemberRoster(String filePath) throws RosterProcessException {

		Workbook readwb = null;

		try {
			// 直接从本地文件创建Workbook
			InputStream instream = new FileInputStream(filePath);

			readwb = Workbook.getWorkbook(instream);

			Sheet readsheet = readwb.getSheet(0);

			int rsColumns = readsheet.getColumns();

			int rsRows = readsheet.getRows();

			logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

			int i = 0;
			if (isRosterWithStatistics(readsheet)) {
				i = 2;
			} else {
				i = 1;
			}
			for (; i < rsRows; i++) {
				ProjectMember member = new ProjectMember();

				member.setOrderNumber(Integer.valueOf(readsheet.getCell(0, i).getContents()));
				member.setName(readsheet.getCell(1, i).getContents());
				member.setBasePay(((NumberCell) readsheet.getCell(2, i)).getValue());
				member.setContractStartAndEndTime(readsheet.getCell(3, i).getContents());
				if (!Constant.EMPTY_STRING.equals(readsheet.getCell(4, i).getContents())) {
					member.setOnJobStartAndEndTime(readsheet.getCell(4, i).getContents());
				}

				logger.debug(member);
				roster.addMember(member);
			}

			if (!isRosterWithStatistics(readsheet)) {
				buildRosterStatistics(readsheet);
			}

			if (rsColumns <= 3) {
				roster.setAvailablePayCount(Integer.MAX_VALUE);
			}

			logger.debug("花名册人数： " + roster.getTotalMember());
			logger.debug("花名册： " + roster);
			instream.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RosterProcessException("读取花名册出错，" + e.getMessage());
		} finally {
			readwb.close();
		}
	}

	private void buildRosterStatistics(Sheet readsheet) {
		int rsColumns = readsheet.getColumns();
		int rsRows = readsheet.getRows();
		Cell cell = null;
		int payYear = roster.getCurrentPayYear();
		RosterStatistics statistics = new RosterStatistics();
		for (int c = 5; c < rsColumns; c += 2) {
			int currentAvailableIndex = 2;
			int availableCount = 0;
			int payMonth = Integer.valueOf(readsheet.getCell(c + 1, 0).getContents().split("月")[0]);
			for (int r = 1; r < rsRows; r++) {
				cell = readsheet.getCell(c, r);
				if (Constant.EMPTY_STRING.equals(cell.getContents())) {
					if (!isContractExpiredOrOffJob(readsheet, r, payYear, payMonth)) {
						availableCount++;
						if (currentAvailableIndex == -1) {
							currentAvailableIndex = r + 1;
						}
					}
				} else {
					currentAvailableIndex = -1;
					availableCount = 0;
				}
			}
			if (currentAvailableIndex > rsRows) {
				currentAvailableIndex = -1;
			}

			statistics.putMonthStatistics(payMonth, new RosterMonthStatistics(currentAvailableIndex, availableCount));
		}
		logger.debug("roster statistics:" + statistics);
		roster.setStatistics(statistics);
	}

	private boolean isContractExpiredOrOffJob(Sheet readsheet, int rowIndex, int year, int month) {
		int processingTime = year * 10000 + month * 100 + 1;
		ProjectMember member = roster.getMember(rowIndex - 1);
		int contractStartTime = member.getContractStartTimeInInteger();
		int contractEndTime = member.getContractEndTimeInInteger();
		boolean isContractExpired = processingTime < contractStartTime || processingTime > contractEndTime;
		int onJobStartTime = member.getOnJobStartTimeInInteger();
		int onJobEndTime = member.getOnJobStartTimeInInteger();
		boolean isOffJob = false;
		if (onJobEndTime > 0) {
			isOffJob = processingTime < onJobStartTime || processingTime >= onJobEndTime;
		}

		return isContractExpired || isOffJob;
	}

	private boolean isRosterWithStatistics(Sheet readsheet) {
		if ("序号".equals(readsheet.getCell(0, 0).getContents())) {
			logger.debug("Roster Without Statistics");
			return false;
		}
		return true;
	}

	public void writeProjectMemberRoster(String inputFilePath, String outputFilePath)
			throws RosterProcessException, WriteException, IOException {
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			File inputFile = new File(inputFilePath);
			File outputFille = new File(outputFilePath);
			rwb = Workbook.getWorkbook(inputFile);

			wwb = Workbook.createWorkbook(outputFille, rwb);// copy
			WritableSheet sheet = wwb.getSheet(0);

			writeRosterStatistics(sheet);
			writeCursor(sheet);

			wwb.write();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RosterProcessException("保存花名册出错，" + e.getMessage());
		} finally {
			rwb.close();
			wwb.close();
		}
	}

	private void writeRosterStatistics(WritableSheet sheet) throws RowsExceededException, WriteException {
		sheet.insertRow(0);
		for (int m = 1; m <= 12; m++) {
			RosterStatistics statistics = roster.getStatistics();
			Number availableIndex = new Number(2 * m + 3, 0,
					statistics.getMonthStatistics(m).getCurrentAvailableIndex());
			sheet.addCell(availableIndex);
			Number availableCount = new Number(2 * m + 4, 0, statistics.getMonthStatistics(m).getAvailableCount());
			sheet.addCell(availableCount);
		}
	}	

	private void writeCursor(WritableSheet sheet) {
		// TODO:
	}

	/**
	 * @return the roster
	 */
	public ProjectMemberRoster getRoster() {
		return roster;
	}

	/**
	 * @param roster
	 *            the roster to set
	 */
	public void setRoster(ProjectMemberRoster roster) {
		this.roster = roster;
	}

}

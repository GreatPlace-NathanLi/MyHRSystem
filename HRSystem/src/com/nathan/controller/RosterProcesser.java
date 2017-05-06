package com.nathan.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.exception.RosterProcessException;
import com.nathan.model.ProjectMember;
import com.nathan.model.ProjectMemberRoster;
import com.nathan.model.RosterCursor;
import com.nathan.model.RosterMonthStatistics;
import com.nathan.model.RosterStatistics;
import com.nathan.service.AbstractExcelOperater;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class RosterProcesser extends AbstractExcelOperater {

	private static Logger logger = Logger.getLogger(RosterProcesser.class);

	private ProjectMemberRoster roster;
	
	private Map<String, ProjectMemberRoster> rosterCache;

	private boolean isReconstruction;

	public RosterProcesser() {
		rosterCache = new ConcurrentHashMap<String, ProjectMemberRoster>();
	}

	public ProjectMemberRoster getRosterFormCache(String projectLeaderAndYear) {
		return rosterCache.get(projectLeaderAndYear);
	}

	public void putRosterToCache(String projectLeaderAndYear, ProjectMemberRoster roster) {
		rosterCache.put(projectLeaderAndYear, roster);
	}

	public void processRoster(String company, String projectLeader, int year, boolean isReconstruction) throws RosterProcessException {
		String key = company + projectLeader + year;
		roster = getRosterFormCache(key);
		if (roster == null || isReconstruction) {
			String inputPath = getRosterFilePath(company, projectLeader, year);
			logger.info("从本地读取花名册： " + inputPath);
			roster = new ProjectMemberRoster();
			roster.setLocation(inputPath);
			roster.setCurrentPayYear(year);
			readProjectMemberRoster(inputPath, isReconstruction);

			putRosterToCache(key, roster);

		} else {
			logger.info("从缓存中读取花名册：" + roster.getFileName());
		}
//		logger.debug("花名册统计数据:" + roster.getStatistics());
	}
	
	private String getRosterFilePath(String company, String projectLeader, int year) {
		String rosterFile = Constant.propUtil.getStringValue("user.花名册路径", Constant.ROSTER_FILE);
		String filePath = rosterFile.replaceAll("NNN", projectLeader).replaceAll("YYYY", String.valueOf(year)).replace("UUUU", company);
		return filePath;
	}

	public void updateProjectMemberRoster() throws RosterProcessException {
		String inputPath = roster.getLocation();
		logger.info("保存花名册： " + inputPath);
		writeProjectMemberRoster(inputPath, inputPath);
	}

	public void readProjectMemberRoster(String filePath, boolean isReconstruction) throws RosterProcessException {

		this.isReconstruction = isReconstruction;

		try {
			read(filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RosterProcessException("读取花名册出错，" + e.getMessage());
		}
	}

	protected void readContent(Workbook readwb) {
		Sheet cashSheet = readwb.getSheet(Constant.ROSTER_CASH);
		if (cashSheet != null) {
			readSheet(cashSheet, roster);
		}
		Sheet bankSheet = readwb.getSheet(Constant.ROSTER_BANK);
		if (bankSheet != null) {
			ProjectMemberRoster bankRoster = new ProjectMemberRoster();
			bankRoster.setCurrentPayYear(roster.getCurrentPayYear());
			bankRoster.setLocation(roster.getLocation());
			readSheet(bankSheet, bankRoster);
			roster.setBankRoster(bankRoster);
		}
	}
	
	private void readSheet(Sheet readsheet, ProjectMemberRoster roster) {
		roster.setName(readsheet.getName());
		
		int rsColumns = readsheet.getColumns();

		int rsRows = readsheet.getRows();

		logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

		boolean isRosterWithStatistics = isRosterWithStatistics(readsheet);
		int i = 0;
		if (isRosterWithStatistics) {
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

			// logger.debug(member);
			roster.addMember(member);
		}

		if (!isRosterWithStatistics) {
			logger.debug("创建花名册统计数据");
			buildRosterStatistics(readsheet, roster);
		} else {
			logger.debug("读取花名册统计数据");
			parseRosterStatistics(readsheet, roster);
		}

		if (isReconstruction) {
			parseCursors(readsheet, isRosterWithStatistics, roster);
		}

		logger.debug(roster);
	}

	private void parseCursors(Sheet readsheet, boolean isRosterWithStatistics, ProjectMemberRoster roster) {
		int rsColumns = readsheet.getColumns();
		int rsRows = readsheet.getRows();
		int payYear = roster.getCurrentPayYear();
		Cell cell = null;
		int r = 0;
		for (int c = 5; c < rsColumns; c += 2) {
			if (isRosterWithStatistics) {
				r = 2;
			} else {
				r = 1;
			}
			int payMonth = Integer.valueOf(readsheet.getCell(c + 1, r - 1).getContents().split("月")[0]);
			int payCount = 1;
			for (; r < rsRows; r++) {
				cell = readsheet.getCell(c, r);
				if (!Constant.EMPTY_STRING.equals(cell.getContents())) {
					RosterCursor cursor = new RosterCursor(c, isRosterWithStatistics ? r : r + 1);
					cursor.setIdentifier(cell.getContents());
					cell = readsheet.getCell(c + 1, r);
					if (CellType.NUMBER.equals(cell.getType())) {
						cursor.setAmount(((NumberCell) cell).getValue());
					}
					cursor.setMonth(payMonth);
					cursor.setPayCount(payCount);
					roster.addExistingCursor(cursor);
					payCount = 1;
				} else {
					if (isAvailable(r - 1, payYear, payMonth, isRosterWithStatistics, roster)) {
						payCount++;
					}
				}
			}
		}
		logger.debug("parseCursors(): " + roster.getExistingCursorList());
	}

	private void buildRosterStatistics(Sheet readsheet, ProjectMemberRoster roster) {
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
					if (isAvailable(r, payYear, payMonth, true, roster)) {
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
		roster.setStatistics(statistics);
	}

	public void parseRosterStatistics(Sheet readsheet, ProjectMemberRoster roster) {
		int rsColumns = readsheet.getColumns();
		Cell cell = null;
		RosterStatistics statistics = new RosterStatistics();
		for (int c = 5; c < rsColumns; c += 2) {
			cell = readsheet.getCell(c, 0);
//			int currentAvailableIndex = Integer.valueOf(cell.getContents());
			int currentAvailableIndex = (int) ((NumberCell)cell).getValue();
			cell = readsheet.getCell(c + 1, 0);
//			int availableCount = Integer.valueOf(cell.getContents());
			int availableCount = (int) ((NumberCell)cell).getValue();
			int payMonth = Integer.valueOf(readsheet.getCell(c + 1, 1).getContents().split("月")[0]);

			statistics.putMonthStatistics(payMonth, new RosterMonthStatistics(currentAvailableIndex, availableCount));
		}
		roster.setStatistics(statistics);
	}

	private boolean isAvailable(int rowIndex, int year, int month, boolean isRosterWithStatistics, ProjectMemberRoster roster) {
		int memberIndex = isRosterWithStatistics ? rowIndex - 1 : rowIndex;
		ProjectMember member = roster.getMember(memberIndex);
		return member.isAvailable(year, month);
	}

	private boolean isRosterWithStatistics(Sheet readsheet) {
		if ("序号".equals(readsheet.getCell(0, 0).getContents())) {
			logger.debug("Roster Without Statistics");
			return false;
		}
		return true;
	}

	public void writeProjectMemberRoster(String inputFilePath, String outputFilePath) throws RosterProcessException {
		try {
			setBackupFlag(true);
			write(inputFilePath, outputFilePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RosterProcessException("保存花名册出错，" + e.getMessage());
		}
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {
		WritableSheet bankSheet = wwb.getSheet(Constant.ROSTER_BANK);
		if (bankSheet != null) {
			writeRosterStatistics(bankSheet, roster.getBankRoster());
			writeCursor(bankSheet, roster.getBankRoster());
		}

		WritableSheet cashSheet = wwb.getSheet(Constant.ROSTER_CASH);
		if (cashSheet != null) {
			writeRosterStatistics(cashSheet, roster);
			writeCursor(cashSheet, roster);
		}
	}

	private void writeRosterStatistics(WritableSheet sheet, ProjectMemberRoster roster) throws RowsExceededException, WriteException {
		if (!isRosterWithStatistics(sheet)) {
			sheet.insertRow(0);
		}
		int rsColumns = sheet.getColumns();
		RosterStatistics statistics = roster.getStatistics();
		for (int c = 5; c < rsColumns; c += 2) {
			int payMonth = Integer.valueOf(sheet.getCell(c + 1, 1).getContents().split("月")[0]);

			Number availableIndex = new Number(c, 0,
					statistics.getMonthStatistics(payMonth).getCurrentAvailableIndex());
			sheet.addCell(availableIndex);
			Number availableCount = new Number(c + 1, 0, statistics.getMonthStatistics(payMonth).getAvailableCount());
			sheet.addCell(availableCount);
		}
	}

	private void writeCursor(WritableSheet sheet, ProjectMemberRoster roster) throws RowsExceededException, WriteException {
		for (RosterCursor cursor : roster.getToAddCursorList()) {
			Label identifier = new Label(cursor.getColumnIndex(), cursor.getRowIndex(), cursor.getIdentifier());
			sheet.addCell(identifier);
			Number amount = new Number(cursor.getColumnIndex() + 1, cursor.getRowIndex(), cursor.getAmount());
			sheet.addCell(amount);
		}
		for (RosterCursor cursor : roster.getToDeleteCursorList()) {
			Label identifier = new Label(cursor.getColumnIndex(), cursor.getRowIndex(), Constant.EMPTY_STRING);
			sheet.addCell(identifier);
			Label amount = new Label(cursor.getColumnIndex() + 1, cursor.getRowIndex(), Constant.EMPTY_STRING);
			sheet.addCell(amount);
		}
	}
	
	public int deleteRosterCursorsByContractID(String contractID) throws RosterProcessException {
		int releasedPayCount = 0;
		releasedPayCount = deleteRosterCursorsByContractID(contractID, roster);
		releasedPayCount += deleteRosterCursorsByContractID(contractID, roster.getBankRoster());	
		return releasedPayCount;
	}

	public int deleteRosterCursorsByContractID(String contractID, ProjectMemberRoster roster) throws RosterProcessException {
		logger.info("删除花名册游标编号为： " + contractID);
		int releasedPayCount = 0;
		if (roster == null) {
			return releasedPayCount;
		}
		for (RosterCursor cursor : roster.getExistingCursorList()) {
			if (cursor.getIdentifier().equals(contractID)) {
				logger.info("删除游标：" + cursor);
				roster.addToDeleteCursor(cursor);
				updateRosterStatisticsOnceDeleteCursor(cursor, roster);
				releasedPayCount += cursor.getPayCount();
			}
		}

		if (roster.getToDeleteCursorList().size() > 0) {
			logger.debug(roster.getToDeleteCursorList());
			String inputPath = roster.getLocation();
			logger.info("删除花名册游标： " + inputPath);
			writeProjectMemberRoster(inputPath, inputPath);
			roster.resetToDeleteCursorList();
		}
		
		return releasedPayCount;
	}

	private void updateRosterStatisticsOnceDeleteCursor(RosterCursor cursor, ProjectMemberRoster roster) {
		int month = cursor.getMonth();
		int payCount = cursor.getPayCount();
		int monthAvailableIndex = 2;
		RosterCursor preCursor = getLastCursor(cursor, roster);
		logger.debug("updateRosterStatisticsOnceDeleteCursor()－preCursor:" + preCursor);
		if (preCursor != null && preCursor.getMonth() == month) {
			int preCursorIndex = preCursor.getRowIndex();
			for (int i = 1; i <= payCount; i++) {
				if (isAvailable(preCursorIndex + i - 1, roster.getCurrentPayYear(), month, true, roster)) {
					monthAvailableIndex = preCursorIndex + i;
					break;
				}
			}
		}
		int monthAvailableCount = roster.getStatistics().getMonthAvailableCount(month);
		roster.getStatistics().setMonthAvailableCount(month, monthAvailableCount + payCount);
		roster.getStatistics().setMonthAvaiableIndex(month, monthAvailableIndex);
	}

	private RosterCursor getLastCursor(RosterCursor currentCursor, ProjectMemberRoster roster) {
		RosterCursor cursor = null;
		for (int i = 1; i < roster.getExistingCursorList().size(); i++) {
			cursor = roster.getExistingCursorList().get(i);
			if (cursor != null && cursor.getColumnIndex() == currentCursor.getColumnIndex()
					&& cursor.getRowIndex() == currentCursor.getRowIndex()) {
				return roster.getExistingCursorList().get(i - 1);
			}
		}
		return null;
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

	public static void main(String[] args) throws Exception {
		RosterProcesser rosterProcesser = new RosterProcesser();

		long startTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("读写花名册开始...");
		logger.info(Constant.LINE0);

		Constant.propUtil.init();
		String company = "雷能电力";
		String projectLeader = "陈志强";
		int year = 2016;
		rosterProcesser.processRoster(company, projectLeader, year, false);

		logger.info(Constant.LINE1);
		rosterProcesser.updateProjectMemberRoster();
		logger.info(Constant.LINE1);

		rosterProcesser.processRoster(company, projectLeader, year, true);
		rosterProcesser.deleteRosterCursorsByContractID("14-019补", rosterProcesser.getRoster());

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("读写花名册结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
		logger.info(Constant.LINE0);
	}
}

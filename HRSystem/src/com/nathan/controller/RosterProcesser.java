package com.nathan.controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;
import com.nathan.exception.RosterProcessException;
import com.nathan.model.ProjectMember;
import com.nathan.model.ProjectMemberRoster;
import com.nathan.model.RosterCursor;
import com.nathan.model.RosterMonthStatistics;
import com.nathan.model.RosterStatistics;
import com.nathan.service.AbstractExcelOperater;
import com.nathan.view.InteractionHandler;

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
	
	private boolean isNeededToRemoveRosterStatistics = false;

	private boolean isReconstruction;

	public RosterProcesser() {
		rosterCache = new ConcurrentHashMap<String, ProjectMemberRoster>();
		isNeededToRemoveRosterStatistics = Constant.YES.equals(Constant.propUtil.getStringEnEmpty(Constant.CONFIG_SYSTEM_isNeededToRemoveRosterStatisticsRow));
	}

	public ProjectMemberRoster getRosterFormCache(String projectLeaderAndYear) {
		return rosterCache.get(projectLeaderAndYear);
	}

	public void putRosterToCache(String projectLeaderAndYear, ProjectMemberRoster roster) {
		rosterCache.put(projectLeaderAndYear, roster);
	}

	public ProjectMemberRoster processRoster(String company, String projectLeader, int year, boolean isReconstruction) throws RosterProcessException {
		String key = company + projectLeader + year;
		roster = getRosterFormCache(key);
		if (roster == null || isReconstruction || isNeededToRemoveRosterStatistics) {
			String rosterPath = getRosterFilePath(company, projectLeader, year);
			if (!isRosterExists(company, projectLeader, year)) {
				logger.info(projectLeader + "没有" + year + "年度花名册，尝试寻找公用花名册");
				rosterPath = getRosterFilePath(company, company, year);
				if (!Util.isFileExists(rosterPath)) {
					logger.info(company + "没有" + year + "年度公用花名册，尝试复制上年度花名册");
					rosterPath = tryToCloneRoster(company, projectLeader, year);
				}
			}
			
			logger.info("从本地读取花名册： " + rosterPath);
			roster = new ProjectMemberRoster();
			roster.setLocation(rosterPath);
			roster.setCurrentPayYear(year);
			readProjectMemberRoster(rosterPath, isReconstruction);

			putRosterToCache(key, roster);

		} else {
			logger.info("从缓存中读取花名册：" + roster.getFileName());
		}
		return roster;
	}
	
	public boolean isRosterExists(String company, String projectLeader, int year) {
		String rosterPath = getRosterFilePath(company, projectLeader, year);
		return Util.isFileExists(rosterPath);
	}
	
	public String tryToCloneRoster(String company, String projectLeader, int year) throws RosterProcessException {
		String oldProjectLeaderRosterPath = getRosterFilePath(company, projectLeader, year - 1);
		if (Util.isFileExists(oldProjectLeaderRosterPath)) {
			logger.info("复制领队花名册：" + year);
			String newProjectLeaderRosterPath = getRosterFilePath(company, projectLeader, year);
			cloneRoster(oldProjectLeaderRosterPath, newProjectLeaderRosterPath);
			return newProjectLeaderRosterPath;
		} 
		String oldCommonRosterPath = getRosterFilePath(company, company, year - 1);
		if (Util.isFileExists(oldCommonRosterPath)) {
			logger.info("复制单位公用花名册：" + year);
			String newCommonRosterPath = getRosterFilePath(company, company, year);
			cloneRoster(oldCommonRosterPath, newCommonRosterPath);
			return newCommonRosterPath;
		}
		throw new RosterProcessException(year + "年度找不到可开票的花名册");
	}
	
	public void cloneRoster(String oldRosterPath, String newRosterPath) throws RosterProcessException {		
		try {
			copy(oldRosterPath, newRosterPath);
		} catch (Exception e) {
			String message = "复制花名册出错，" + e.getMessage();
			logger.error(message, e);
			throw new RosterProcessException(message);
		}
		logger.info("复制花名册");
		logger.info("从 " + oldRosterPath);
		logger.info("到 " + newRosterPath);	
	}
	
	protected void copyContent(WritableWorkbook wwb) throws Exception {

		copySheetByRosterType(wwb, Constant.ROSTER_BANK);

		copySheetByRosterType(wwb, Constant.ROSTER_CASH);
		
	}
	
	private void copySheetByRosterType(WritableWorkbook wwb, String rosterType) throws Exception {
		WritableSheet sheet = wwb.getSheet(rosterType);
		if (sheet != null) {
			removeRosterStatistics(sheet);
			clearCursors(sheet);
		}
	}
	
	private void clearCursors(WritableSheet sheet) throws Exception {
		logger.debug("清除花名册中所有游标");
		int rsColumns = sheet.getColumns();
		int rsRows = sheet.getRows();

		for (int r = 1; r < rsRows; r++) {
			for (int c = 5; c < rsColumns; c++) {
				Label emptyCell = new Label(c, r, Constant.EMPTY_STRING);
				sheet.addCell(emptyCell);
			}
		}
	}
	
	public String getRosterFilePath(String company, String projectLeader, int year) {
		String filePath = Constant.propUtil.getStringValue("user.花名册路径", Constant.ROSTER_FILE);
		filePath = filePath.replaceAll("NNN", projectLeader);
		filePath = filePath.replaceAll("YYYY", String.valueOf(year));
		filePath = filePath.replace("UUUU", company);
		return filePath;
	}

	public void updateProjectMemberRoster(boolean backupFlag) throws RosterProcessException {
		String inputPath = roster.getLocation();
		logger.info("保存花名册： " + inputPath);
		setBackupFlag(backupFlag);
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

	protected void readContent(Workbook readwb) throws Exception {
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
		if (cashSheet == null && bankSheet == null) {
			throw new RosterProcessException("现金和网银花名册都不存在！");
		}
	}
	
	private void readSheet(Sheet readsheet, ProjectMemberRoster roster) {
		roster.setName(readsheet.getName());
		
		int rsColumns = readsheet.getColumns();

		int rsRows = readsheet.getRows();

		logger.debug(readsheet.getName() + " - 总列数：" + rsColumns + ", 总行数：" + rsRows);

		boolean isRosterWithStatistics = isRosterWithStatistics(readsheet);
		int i = 0;
		if (isRosterWithStatistics) {
			i = 2;
		} else {
			i = 1;
		}
		for (; i < rsRows; i++) {
			ProjectMember member = new ProjectMember();

			member.setOrderNumber(Integer.valueOf(readsheet.getCell(0, i).getContents().trim()));
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

//		logger.debug(roster);
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
	
	public void clearRosterStatistics(String filePath, boolean backupFlag) throws RosterProcessException {
		logger.info("清除花名册统计信息： " + filePath);
		try {
			setBackupFlag(backupFlag);
			modify(filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RosterProcessException("清除花名册统计信息出错，" + e.getMessage());
		}
	}
	
	protected void modifyContent(WritableWorkbook wwb) throws Exception {

		modifySheetByRosterType(wwb, Constant.ROSTER_BANK);

		modifySheetByRosterType(wwb, Constant.ROSTER_CASH);
	}
	
	private void modifySheetByRosterType(WritableWorkbook wwb, String rosterType) {
		WritableSheet sheet = wwb.getSheet(rosterType);
		if (sheet != null) {
			removeRosterStatistics(sheet);
		}
	}
	
	private void removeRosterStatistics(WritableSheet sheet) {
		if (isRosterWithStatistics(sheet)) {
			logger.debug("Remove RosterStatistics");
			sheet.removeRow(0);
		}
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
		logger.debug("Roster With Statistics");
		return true;
	}

	public void writeProjectMemberRoster(String inputFilePath, String outputFilePath) throws RosterProcessException {
		try {
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
			removeRosterStatisticsIfNeeded(bankSheet);
		}

		WritableSheet cashSheet = wwb.getSheet(Constant.ROSTER_CASH);
		if (cashSheet != null) {
			writeRosterStatistics(cashSheet, roster);
			writeCursor(cashSheet, roster);
			removeRosterStatisticsIfNeeded(cashSheet);
		}
	}
	
	private void removeRosterStatisticsIfNeeded(WritableSheet sheet) {
		if (isNeededToRemoveRosterStatistics) {
			removeRosterStatistics(sheet);
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

	public void validateRosters() throws Exception {
		String rostersRootPath = Constant.propUtil.getStringValue("user.花名册根目录", Constant.ROSTER_ROOT_PATH);
		rostersRootPath = InteractionHandler.handleRostersPathInput(rostersRootPath);
		if (rostersRootPath == null) {
			return;
		}
		long startTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("校验花名册开始...");
		logger.info(Constant.LINE0);

		ArrayList<String> filesList = new ArrayList<String>();
		
		Util.listAllFileUnderPath(rostersRootPath, filesList, null);
		
		String result = null;
		if (InteractionHandler.handleIsGoOn("总共有" + filesList.size() + "份花名册需要校验")) {
			ArrayList<String> failedFilesList = new ArrayList<String>();
			ArrayList<String> failedReasonList = new ArrayList<String>();
			for (String file : filesList) {
				logger.info("校验：" + file);			
				try {
					roster = new ProjectMemberRoster();
					roster.setLocation(file);
					roster.setCurrentPayYear(Util.getYearFromFilePath(file));
					clearRosterStatistics(file, true);
					readProjectMemberRoster(file, true);
					updateProjectMemberRoster(false);
				} catch(Exception e) {
					logger.info("校验不通过，原因：" + e.getMessage());
					failedFilesList.add(file);
					failedReasonList.add(e.getMessage());
				}		
				logger.info(Constant.LINE1);
			}		
			result = buildValidationResult(rostersRootPath, failedFilesList, failedReasonList);
		} else {
			result = "校验花名册中止！";
		}	
		
		logger.info(result);
		
		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("校验花名册结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
		logger.info(Constant.LINE0);
		
		InteractionHandler.handleProgressCompleted(result);
	}
	
	private String buildValidationResult(String rostersRootPath, ArrayList<String> failedFilesList, ArrayList<String> failedReasonList) {
		StringBuilder sb = new StringBuilder();
		sb.append(rostersRootPath);
		sb.append(" 花名册校验完成，");
		if (failedFilesList.size()>0) {
			sb.append(failedFilesList.size()).append("份花名册没有通过校验：\r\n");
			for (int i = 0; i < failedFilesList.size(); i++) {
				sb.append("\r\n").append(failedFilesList.get(i));
				sb.append(" ( ").append(failedReasonList.get(i)).append(")");
			}
			sb.append("\r\n\r\n").append("可在log文件中查找具体原因。");
		} else {
			sb.append("全部花名册通过！");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		Constant.propUtil.init();
		RosterProcesser rosterProcesser = new RosterProcesser();
		
		String oldRosterPath = "F:/work/project/德盛人力项目管理系统/in/花名册/雷能电力/2016/陈志强2016年花名册.xls";
		String newRosterPath = "F:/work/project/德盛人力项目管理系统/in/花名册/吴川电力/2017/陈志强2017年花名册.xls";
		rosterProcesser.cloneRoster(oldRosterPath, newRosterPath);
//		InteractionHandler.setActionCallback(new BillingCallback());
//		rosterProcesser.validateRosters();
//		
//		long startTime = System.nanoTime();
//		logger.info(Constant.LINE0);
//		logger.info("读写花名册开始...");
//		logger.info(Constant.LINE0);;
//
//		String company = "雷能电力";
//		String projectLeader = "陈志强";
//		int year = 2016;
//		rosterProcesser.processRoster(company, projectLeader, year, false);
//
//		logger.info(Constant.LINE1);
//		rosterProcesser.updateProjectMemberRoster();
//		logger.info(Constant.LINE1);
//
//		rosterProcesser.processRoster(company, projectLeader, year, true);
//		rosterProcesser.deleteRosterCursorsByContractID("14-019补", rosterProcesser.getRoster());
//
//		long endTime = System.nanoTime();
//		logger.info(Constant.LINE0);
//		logger.info("读写花名册结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
//		logger.info(Constant.LINE0);
	}
}

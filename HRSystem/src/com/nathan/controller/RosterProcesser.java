package com.nathan.controller;

import java.io.FileInputStream;
import java.io.InputStream;

import com.nathan.common.Constant;
import com.nathan.model.ProjectMember;
import com.nathan.model.ProjectMemberRoster;

import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

public class RosterProcesser {
	
	private static Logger logger = Logger.getLogger(RosterProcesser.class);

	private ProjectMemberRoster roster;
	
	public void processRoster(String projectLeader) {
		String rosterPath = Constant.ROSTER_FILE.replace("NNN", projectLeader);
		logger.info("����4 - ��ȡ�����᣺ " + rosterPath);
		readProjectMemberRoster(rosterPath);
		logger.info(Constant.LINE1);
	}

	public void readProjectMemberRoster(String filePath) {
		
		Workbook readwb = null;

		try {
			// ֱ�Ӵӱ����ļ�����Workbook
			InputStream instream = new FileInputStream(filePath);

			readwb = Workbook.getWorkbook(instream);

			// Sheet���±��Ǵ�0��ʼ
			// ��ȡ��һ��Sheet��
			Sheet readsheet = readwb.getSheet(0);

			// ��ȡSheet������������������

			int rsColumns = readsheet.getColumns();

			// ��ȡSheet������������������

			int rsRows = readsheet.getRows();

			logger.debug("��������" + rsColumns + ", ��������" + rsRows);

			// ��ȡָ����Ԫ��Ķ�������

			roster = new ProjectMemberRoster();
			int i = 0;
			if (isRosterWithStatistics(readsheet)) {
				i = 2;
			} else {
				i = 1;
				buildRosterStatistics(readsheet);
			}
			for (; i < rsRows; i++) {
				ProjectMember member = new ProjectMember();

				member.setOrderNumber(Integer.valueOf(readsheet.getCell(0, i).getContents()));
				member.setName(readsheet.getCell(1, i).getContents());
				member.setBasePay(((NumberCell) readsheet.getCell(2, i)).getValue());
				member.setContractStartAndEndTime(readsheet.getCell(3, i).getContents());

				logger.debug(member);
				roster.addMember(member);
			}
			if (rsColumns<=3) {
				roster.setAvailablePayCount(Integer.MAX_VALUE);
			}

			logger.debug("������������ " + roster.getTotalMember());
			logger.debug("�����᣺ " + roster);
			instream.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readwb.close();
		}
	}
	
	private void buildRosterStatistics(Sheet readsheet) {
		
	}
	
	private boolean isRosterWithStatistics(Sheet readsheet) {
		if ("���".equals(readsheet.getCell(0, 0).getContents())) {
			logger.debug("Roster Without Statistics");
			return false;
		}
		return true;
	}

	/**
	 * @return the roster
	 */
	public ProjectMemberRoster getRoster() {
		return roster;
	}

	/**
	 * @param roster the roster to set
	 */
	public void setRoster(ProjectMemberRoster roster) {
		this.roster = roster;
	}

}

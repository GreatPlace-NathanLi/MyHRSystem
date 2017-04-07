package com.nathan.model;

import java.util.ArrayList;
import java.util.Calendar;

import com.nathan.common.Util;
import com.sun.media.jfxmedia.logging.Logger;

public class ProjectMemberRoster extends AbstractBook {
	
	private ArrayList<ProjectMember> projectMemberList;
	
	private ArrayList<RosterCursor> cursorList;
	
	private RosterStatistics statistics;
	
	private int currentPayYear;
	
	private int currentPayMonth;
	
	private int lastPayIndex = -1;
	
	private int currentPayIndex = -1;
	
	private int availablePayCount = 0;
	
	public ProjectMemberRoster() {
		this.projectMemberList = new ArrayList<ProjectMember>();
		this.cursorList = new ArrayList<RosterCursor>();
	}

	public void calcAvailablePayCount() {
		Calendar now = Calendar.getInstance();  
        System.out.println("��: " + now.get(Calendar.YEAR));  
        System.out.println("��: " + (now.get(Calendar.MONTH) + 1) + ""); 
        //int year = now.get(Calendar.YEAR);
        if (availablePayCount == Integer.MAX_VALUE) {
        	currentPayIndex = -1;
        }
        int month = (now.get(Calendar.MONTH) + 1);
        if (currentPayMonth < month - 1) {
        	int totalMember = projectMemberList.size();
        	availablePayCount = (month - 1 - currentPayMonth) * totalMember + (totalMember - currentPayIndex - 1);
        }
	}
	
	/**
	 * @return the projectMemberList
	 */
	public ArrayList<ProjectMember> getProjectMemberList() {
		return projectMemberList;
	}

	/**
	 * @param projectMemberList the projectMemberList to set
	 */
	public void setProjectMemberList(ArrayList<ProjectMember> projectMemberList) {
		this.projectMemberList = projectMemberList;
	} 
	
	/**
	 * @return the cursorList
	 */
	public ArrayList<RosterCursor> getCursorList() {
		return cursorList;
	}

	/**
	 * @param cursorList the cursorList to set
	 */
	public void setCursorList(ArrayList<RosterCursor> cursorList) {
		this.cursorList = cursorList;
	}
	
	public void setCursor(int month, String identifier, double amount) {
		RosterCursor cursor = new RosterCursor();
		cursor.setMonth(month);
		cursor.setIdentifier(identifier);
		cursor.setAmount(amount);
		cursor.setColumnIndex(Util.getMonthIdentifierColumnIndexInRoster(month));
		cursor.setRowIndex(lastPayIndex);
		System.out.println(cursor);
		
		cursorList.add(cursor);
	}

	public void addMember(ProjectMember member) {
		this.projectMemberList.add(member.getOrderNumber()-1, member);
	}
	
	public ProjectMember getMember(int index) {
		return this.projectMemberList.get(index);
	}
	
	public ProjectMember getMember() {
		ProjectMember member = getMember(currentPayIndex + 1);
		currentPayIndex++;
		if(currentPayIndex>=getTotalMember()-1) {
			currentPayIndex = -1;
		}
		return member;
	}
	
	public ProjectMember getMemberByMonth(int month) {
		
		availablePayCount = statistics.getMonthAvailableCount(month);
		if(availablePayCount == 0) {
			return null;
		}
		currentPayMonth = month;
		currentPayIndex = statistics.getMonthAvaiableIndex(month);
		ProjectMember member = getMember(currentPayIndex - 2);
		
		updateMonthStatistics();
		
		return member;
	}

	private void updateMonthStatistics() {
		availablePayCount--;
		lastPayIndex = currentPayIndex;
		if (availablePayCount == 0) {
			currentPayIndex = -1;
		} else {
			do {
				currentPayIndex++;
			} while(!getMember(currentPayIndex - 2).isAvailable(currentPayYear, currentPayMonth));	
		}
		
		statistics.setMonthAvailableCount(currentPayMonth, availablePayCount);
		statistics.setMonthAvaiableIndex(currentPayMonth, currentPayIndex);
	}
	
	public int getTotalMember() {
		return this.projectMemberList.size();
	}

	/**
	 * @return the currentPayYear
	 */
	public int getCurrentPayYear() {
		return currentPayYear;
	}

	/**
	 * @param currentPayYear the currentPayYear to set
	 */
	public void setCurrentPayYear(int currentPayYear) {
		this.currentPayYear = currentPayYear;
	}

	/**
	 * @return the currentPayMonth
	 */
	public int getCurrentPayMonth() {
		return currentPayMonth;
	}

	/**
	 * @param currentPayMonth the currentPayMonth to set
	 */
	public void setCurrentPayMonth(int currentPayMonth) {
		this.currentPayMonth = currentPayMonth;
	}

	/**
	 * @return the currentPayIndex
	 */
	public int getCurrentPayIndex() {
		return currentPayIndex;
	}

	/**
	 * @param currentPayIndex the currentPayIndex to set
	 */
	public void setCurrentPayIndex(int currentPayIndex) {
		this.currentPayIndex = currentPayIndex;
	}

	/**
	 * @return the availablePayCount
	 */
	public int getAvailablePayCount() {
		calcAvailablePayCount();
		return availablePayCount;
	}

	/**
	 * @param availablePayCount the availablePayCount to set
	 */
	public void setAvailablePayCount(int availablePayCount) {
		this.availablePayCount = availablePayCount;
	}

	/**
	 * @return the statistics
	 */
	public RosterStatistics getStatistics() {
		return statistics;
	}

	/**
	 * @param statistics the statistics to set
	 */
	public void setStatistics(RosterStatistics statistics) {
		this.statistics = statistics;
	}
	
	public String getName() {
		String[] s = this.getLocation().split("/");
		return s[s.length-1];
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProjectMemberRoster [totalMember=" + projectMemberList.size() + ", currentPayYear=" + currentPayYear
				+ ", currentPayMonth=" + currentPayMonth + ", currentPayIndex=" + currentPayIndex
				+ ", availablePayCount=" + availablePayCount + "]";
	}
	
	
}

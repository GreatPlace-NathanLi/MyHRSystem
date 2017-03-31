package com.nathan.model;

import java.util.ArrayList;
import java.util.Calendar;

public class ProjectMemberRoster {
	
	private ArrayList<ProjectMember> projectMemberList;
	
	private RosterStatistics statistics;
	
	private int currentPayYear;
	
	private int currentPayMonth;
	
	private int currentPayIndex = -1;
	
	private int availablePayCount = 0;

	public void calcAvailablePayCount() {
		Calendar now = Calendar.getInstance();  
        System.out.println("Äê: " + now.get(Calendar.YEAR));  
        System.out.println("ÔÂ: " + (now.get(Calendar.MONTH) + 1) + ""); 
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
	
	public void buildStatistics() {
		
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
	
	public ProjectMemberRoster() {
		this.projectMemberList = new ArrayList<ProjectMember>();
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

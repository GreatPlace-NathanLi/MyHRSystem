package com.nathan.model;

public class AggregatingCriteria {

	private String company;
	
	private String projectLeader;
	
	private int startYearMonthInt;
	
	private int endYearMonthInt;

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the projectLeader
	 */
	public String getProjectLeader() {
		return projectLeader;
	}

	/**
	 * @param projectLeader the projectLeader to set
	 */
	public void setProjectLeader(String projectLeader) {
		this.projectLeader = projectLeader;
	}

	/**
	 * @return the startYearMonthInt
	 */
	public int getStartYearMonthInt() {
		return startYearMonthInt;
	}

	/**
	 * @param startYearMonthInt the startYearMonthInt to set
	 */
	public void setStartYearMonthInt(int startYearMonthInt) {
		this.startYearMonthInt = startYearMonthInt;
	}

	/**
	 * @return the endYearMonthInt
	 */
	public int getEndYearMonthInt() {
		return endYearMonthInt;
	}

	/**
	 * @param endYearMonthInt the endYearMonthInt to set
	 */
	public void setEndYearMonthInt(int endYearMonthInt) {
		this.endYearMonthInt = endYearMonthInt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AggregatingCriteria [company=" + company + ", projectLeader=" + projectLeader + ", startYearMonthInt="
				+ startYearMonthInt + ", endYearMonthInt=" + endYearMonthInt + "]";
	}
	
}

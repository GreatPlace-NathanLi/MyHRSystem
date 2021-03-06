package com.nathan.view;

public class InteractionInput {

	private String company;
	
	private String projectLeader;
	
	private int startPayYear;
	
	private int startPayMonth;
	
	private int startYearMonth;
	
	private int endPayYear;
	
	private int endPayMonth;
	
	private int endYearMonth;

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
	 * @return the startPayYear
	 */
	public int getStartPayYear() {
		return startPayYear;
	}

	/**
	 * @param startPayYear the startPayYear to set
	 */
	public void setStartPayYear(int startPayYear) {
		this.startPayYear = startPayYear;
	}

	/**
	 * @return the startPayMonth
	 */
	public int getStartPayMonth() {
		return startPayMonth;
	}

	/**
	 * @param startPayMonth the startPayMonth to set
	 */
	public void setStartPayMonth(int startPayMonth) {
		this.startPayMonth = startPayMonth;
	}

	/**
	 * @return the startYearMonth
	 */
	public int getStartYearMonth() {
		return startYearMonth;
	}

	/**
	 * @param startYearMonth the startYearMonth to set
	 */
	public void setStartYearMonth(int startYearMonth) {
		this.startYearMonth = startYearMonth;
	}

	/**
	 * @return the endPayYear
	 */
	public int getEndPayYear() {
		return endPayYear;
	}

	/**
	 * @param endPayYear the endPayYear to set
	 */
	public void setEndPayYear(int endPayYear) {
		this.endPayYear = endPayYear;
	}

	/**
	 * @return the endPayMonth
	 */
	public int getEndPayMonth() {
		return endPayMonth;
	}

	/**
	 * @param endPayMonth the endPayMonth to set
	 */
	public void setEndPayMonth(int endPayMonth) {
		this.endPayMonth = endPayMonth;
	}

	/**
	 * @return the endYearMonth
	 */
	public int getEndYearMonth() {
		return endYearMonth;
	}

	/**
	 * @param endYearMonth the endYearMonth to set
	 */
	public void setEndYearMonth(int endYearMonth) {
		this.endYearMonth = endYearMonth;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InteractionInput [company=" + company + ", projectLeader=" + projectLeader + ", startYearMonth="
				+ startYearMonth + ", endYearMonth=" + endYearMonth + ", startPayYear=" + startPayYear
				+ ", startPayMonth=" + startPayMonth + ", endPayYear=" + endPayYear + ", endPayMonth=" + endPayMonth
				+ "]";
	}

}

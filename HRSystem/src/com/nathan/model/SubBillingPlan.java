package com.nathan.model;

public class SubBillingPlan extends BillingPlan {
	
	private String subPlanProjectUnit;
	
	private String subPlanProjectLeader;
	
	private int subPlanPayYear;
	
	private int subPlanStartMonth;
	
	private int subPlanEndMonth;
	
	private int subPlanPayCount;
	
	private String subPlanRosterType;

	/**
	 * @return the subPlanProjectUnit
	 */
	public String getSubPlanProjectUnit() {
		return subPlanProjectUnit;
	}

	/**
	 * @param subPlanProjectUnit the subPlanProjectUnit to set
	 */
	public void setSubPlanProjectUnit(String subPlanProjectUnit) {
		this.subPlanProjectUnit = subPlanProjectUnit;
	}

	/**
	 * @return the subPlanProjectLeader
	 */
	public String getSubPlanProjectLeader() {
		return subPlanProjectLeader;
	}

	/**
	 * @param subPlanProjectLeader the subPlanProjectLeader to set
	 */
	public void setSubPlanProjectLeader(String subPlanProjectLeader) {
		this.subPlanProjectLeader = subPlanProjectLeader;
	}

	/**
	 * @return the subPlanPayYear
	 */
	public int getSubPlanPayYear() {
		return subPlanPayYear;
	}

	/**
	 * @param subPlanPayYear the subPlanPayYear to set
	 */
	public void setSubPlanPayYear(int subPlanPayYear) {
		this.subPlanPayYear = subPlanPayYear;
	}

	/**
	 * @return the subPlanStartMonth
	 */
	public int getSubPlanStartMonth() {
		return subPlanStartMonth;
	}

	/**
	 * @param subPlanStartMonth the subPlanStartMonth to set
	 */
	public void setSubPlanStartMonth(int subPlanStartMonth) {
		this.subPlanStartMonth = subPlanStartMonth;
	}

	/**
	 * @return the subPlanEndMonth
	 */
	public int getSubPlanEndMonth() {
		return subPlanEndMonth;
	}

	/**
	 * @param subPlanEndMonth the subPlanEndMonth to set
	 */
	public void setSubPlanEndMonth(int subPlanEndMonth) {
		this.subPlanEndMonth = subPlanEndMonth;
	}

	/**
	 * @return the subPlanPayCount
	 */
	public int getSubPlanPayCount() {
		return subPlanPayCount;
	}

	/**
	 * @param subPlanPayCount the subPlanPayCount to set
	 */
	public void setSubPlanPayCount(int subPlanPayCount) {
		this.subPlanPayCount = subPlanPayCount;
	}

	/**
	 * @return the subPlanRosterType
	 */
	public String getSubPlanRosterType() {
		return subPlanRosterType;
	}

	/**
	 * @param subPlanRosterType the subPlanRosterType to set
	 */
	public void setSubPlanRosterType(String subPlanRosterType) {
		this.subPlanRosterType = subPlanRosterType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubBillingPlan [subPlanProjectUnit=" + subPlanProjectUnit + ", subPlanProjectLeader="
				+ subPlanProjectLeader + ", subPlanPayYear=" + subPlanPayYear + ", subPlanStartMonth="
				+ subPlanStartMonth + ", subPlanEndMonth=" + subPlanEndMonth + ", subPlanPayCount=" + subPlanPayCount
				+ "]";
	}
	
}

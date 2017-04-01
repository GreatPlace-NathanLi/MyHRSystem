package com.nathan.model;

public class RosterMonthStatistics {

	private int currentAvailableIndex;
	
	private int availableCount;
	
	public RosterMonthStatistics(int currentAvailableIndex, int availableCount) {
		this.currentAvailableIndex = currentAvailableIndex;
		this.availableCount = availableCount;
	}

	/**
	 * @return the currentAvailableIndex
	 */
	public int getCurrentAvailableIndex() {
		return currentAvailableIndex;
	}

	/**
	 * @param currentAvailableIndex
	 *            the currentAvailableIndex to set
	 */
	public void setCurrentAvailableIndex(int currentAvailableIndex) {
		this.currentAvailableIndex = currentAvailableIndex;
	}

	/**
	 * @return the availableCount
	 */
	public int getAvailableCount() {
		return availableCount;
	}

	/**
	 * @param availableCount
	 *            the availableCount to set
	 */
	public void setAvailableCount(int availableCount) {
		this.availableCount = availableCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RosterMonthStatistics [currentAvailableIndex=" + currentAvailableIndex + ", availableCount="
				+ availableCount + "]\n";
	}

}

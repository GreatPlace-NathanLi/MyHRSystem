package com.nathan.model;

import java.util.Map;

public class RosterStatistics {
	
	private int totalAvailableCount;
	
	private int currentAvailableMonth;
	
	private Map<Integer, RosterMonthStatistics> monthsStatisticsMap;

	/**
	 * @return the totalAvailableCount
	 */
	public int getTotalAvailableCount() {
		return totalAvailableCount;
	}

	/**
	 * @param totalAvailableCount the totalAvailableCount to set
	 */
	public void setTotalAvailableCount(int totalAvailableCount) {
		this.totalAvailableCount = totalAvailableCount;
	}

	/**
	 * @return the currentAvailableMonth
	 */
	public int getCurrentAvailableMonth() {
		return currentAvailableMonth;
	}

	/**
	 * @param currentAvailableMonth the currentAvailableMonth to set
	 */
	public void setCurrentAvailableMonth(int currentAvailableMonth) {
		this.currentAvailableMonth = currentAvailableMonth;
	}

	/**
	 * @return the monthsStatisticsMap
	 */
	public Map<Integer, RosterMonthStatistics> getMonthsStatisticsMap() {
		return monthsStatisticsMap;
	}

	/**
	 * @param monthsStatisticsMap the monthsStatisticsMap to set
	 */
	public void setMonthsStatisticsMap(Map<Integer, RosterMonthStatistics> monthsStatisticsMap) {
		this.monthsStatisticsMap = monthsStatisticsMap;
	}
	
	
}

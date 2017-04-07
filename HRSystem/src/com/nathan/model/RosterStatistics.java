package com.nathan.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RosterStatistics {
	
	private int totalAvailableCount;
	
	private int currentAvailableMonth;
	
	private Map<Integer, RosterMonthStatistics> monthsStatisticsMap;
	
	public RosterStatistics() {
		monthsStatisticsMap = new ConcurrentHashMap<Integer, RosterMonthStatistics>();
	}

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
	
	public void putMonthStatistics(int month, RosterMonthStatistics monthsStatistics) {
		this.monthsStatisticsMap.put(month, monthsStatistics);
	}
	
	public RosterMonthStatistics getMonthStatistics(int month) {
		return this.monthsStatisticsMap.get(month);
	}

	public int getMonthAvailableCount(int month) {
		return this.monthsStatisticsMap.get(month).getAvailableCount();
	}
	
	public void setMonthAvailableCount(int month, int availableCount) {
		this.monthsStatisticsMap.get(month).setAvailableCount(availableCount);
	}
	
	public int getMonthAvaiableIndex(int month) {
		return this.monthsStatisticsMap.get(month).getCurrentAvailableIndex();
	}
	
	public void setMonthAvaiableIndex(int month, int currentAvailableIndex) {
		this.monthsStatisticsMap.get(month).setCurrentAvailableIndex(currentAvailableIndex);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RosterStatistics [totalAvailableCount=" + totalAvailableCount + ", currentAvailableMonth="
				+ currentAvailableMonth + ", monthsStatisticsMap=" + monthsStatisticsMap + "]";
	}
	
	
}

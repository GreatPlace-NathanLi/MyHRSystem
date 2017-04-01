package com.nathan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BillingPlanBook extends AbstractBook {
	
	private List<BillingPlan> billingPlanList;
	private Map<String, Integer> projectLeaderPayrollCountMap;
	private Map<String, List<BillingPlan>> projectLeaderBillingPlanMap;
	
	public BillingPlanBook() {
		this.billingPlanList = new ArrayList<BillingPlan>();
		this.projectLeaderPayrollCountMap = new ConcurrentHashMap<String, Integer>();
		this.projectLeaderBillingPlanMap = new ConcurrentHashMap<String, List<BillingPlan>>();
	}

	public void addBillingPlan(BillingPlan billingPlan) {
		this.billingPlanList.add(billingPlan.getOrderNumber()-1, billingPlan);
	}
	
	public int getTotalBillingPlanSize() {
		return this.billingPlanList.size();
	}
	
	public void addPayrollCountByProjectLeader(String projectLeader, Integer payrollCount) {
		if(projectLeaderPayrollCountMap.keySet().contains(projectLeader)) {
			Integer pc = projectLeaderPayrollCountMap.get(projectLeader);
			pc += payrollCount;
			projectLeaderPayrollCountMap.put(projectLeader, pc);
		} else {
			projectLeaderPayrollCountMap.put(projectLeader, payrollCount);
		}
	}
	
	public int getPayrollCountByProjectLeader(String projectLeader) {
		if(projectLeaderPayrollCountMap.keySet().contains(projectLeader)) {
			return projectLeaderPayrollCountMap.get(projectLeader);
		} else {
			return 0;
		}
	}
	
	public void addBillingPlanByProjectLeader(String projectLeader, BillingPlan billingPlan) {
		if(projectLeaderBillingPlanMap.keySet().contains(projectLeader)) {
			List<BillingPlan> billingPlanList = projectLeaderBillingPlanMap.get(projectLeader);
			billingPlanList.add(billingPlan);
		} else {
			List<BillingPlan> billingPlanList = new ArrayList<BillingPlan>();
			billingPlanList.add(billingPlan);
			projectLeaderBillingPlanMap.put(projectLeader, billingPlanList);
		}
	}
	
	public List<BillingPlan> getBillingByProjectLeader(String projectLeader) {
		if(projectLeaderBillingPlanMap.keySet().contains(projectLeader)) {
			return projectLeaderBillingPlanMap.get(projectLeader);
		} else {
			return null;
		}
	}
	
	/**
	 * @return the billingPlanList
	 */
	public List<BillingPlan> getBillingPlanList() {
		return billingPlanList;
	}

	/**
	 * @param billingPlanList the billingPlanList to set
	 */
	public void setBillingList(List<BillingPlan> billingPlanList) {		
		this.billingPlanList = billingPlanList;
		buildProjectLeaderBillingPlanMap();
		buildProjectLeaderPayrollCountMap();
	}
	
	public void buildProjectLeaderPayrollCountMap() {
		for(BillingPlan billingPlan : billingPlanList) {
			addPayrollCountByProjectLeader(billingPlan.getProjectLeader(), billingPlan.getPayCount());
		}
	}
	
	public void buildProjectLeaderBillingPlanMap() {
		for(BillingPlan billingPlan : billingPlanList) {
			addBillingPlanByProjectLeader(billingPlan.getProjectLeader(),billingPlan);
		}
	}

	/**
	 * @return the projectLeaderPayrollCountMap
	 */
	public Map<String, Integer> getProjectLeaderPayrollCountMap() {
		return projectLeaderPayrollCountMap;
	}

	/**
	 * @param projectLeaderPayrollCountMap the projectLeaderPayrollCountMap to set
	 */
	public void setProjectLeaderPayrollCountMap(Map<String, Integer> projectLeaderPayrollCountMap) {
		this.projectLeaderPayrollCountMap = projectLeaderPayrollCountMap;
	}

	/**
	 * @return the projectLeaderBillingPlanMap
	 */
	public Map<String, List<BillingPlan>> getProjectLeaderBillingPlanMap() {
		return projectLeaderBillingPlanMap;
	}

	/**
	 * @param projectLeaderBillingPlanMap the projectLeaderBillingPlanMap to set
	 */
	public void setProjectLeaderBillingPlanMap(Map<String, List<BillingPlan>> projectLeaderBillingPlanMap) {
		this.projectLeaderBillingPlanMap = projectLeaderBillingPlanMap;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BillingPlanBook [billingPlanList=" + billingPlanList + ", projectLeaderPayrollCountMap="
				+ projectLeaderPayrollCountMap + ", projectLeaderBillingPlanMap=" + projectLeaderBillingPlanMap + "]";
	}

}

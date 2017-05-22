package com.nathan.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.PropertiesUtils;
import com.nathan.model.BillingPlan;
import com.nathan.model.ServiceFeeSummarySheet;
import com.nathan.view.ActionType;
import com.nathan.view.AggregatingResultGUI;
import com.nathan.view.BillingSystemCallback;
import com.nathan.view.InteractionHandler;

public class AggregatingOperater {
	
	private static Logger logger = Logger.getLogger(AggregatingOperater.class);
	
	protected AggregatingInputProcesser aggregatingInputProcesser;
	
//	private AggregatingResultProcesser aggregatingResultProcesser;
	
	private List<BillingPlan> billingPlanList;
	
//	private Map<String, List<BillingPlan>> companyYearMonthBillingPlanMap;
	
	public void startAggregating(String company, String projectLeader, int startYearMonth, int endYearMonth) throws Exception {
		long startTime = System.nanoTime();
		if (aggregatingInputProcesser == null) {
			aggregatingInputProcesser = new AggregatingInputProcesser();
		}

		if (billingPlanList == null) {
			String billingFile = getAggregatingInputFilePath();
			logger.info("从本地读取开票计划输入： " + billingFile);
			aggregatingInputProcesser.readBillingInput(billingFile);
			billingPlanList = aggregatingInputProcesser.getBillingPlanBook().getBillingPlanList();
		} else {
			logger.info("从缓存读取开票计划输入");
		}
			
		logger.info("size: " + billingPlanList.size());
		List<BillingPlan> resultList = new ArrayList<BillingPlan>();
		for (BillingPlan billingPlan : billingPlanList) {
			filter(resultList, billingPlan, company, projectLeader, startYearMonth, endYearMonth);
		}
		
		logger.info("总共有" + resultList.size() + "条开票计划被汇总");
		if (resultList.size() > 0) {
			ServiceFeeSummarySheetProcesser serviceFeeSummarySheetProcesser = new ServiceFeeSummarySheetProcesser();
			ServiceFeeSummarySheet summarySheet = serviceFeeSummarySheetProcesser.processServiceFeeSummarySheet(resultList, company, startYearMonth, endYearMonth);
//			aggregatingResultProcesser = serviceFeeSummarySheetProcesser;
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					AggregatingResultGUI.createAndShowServiceFeeSummaryTableGUI(summarySheet, serviceFeeSummarySheetProcesser);
				}
			});
		} else {
			InteractionHandler.handleProgressCompleted("汇总结束！");
		}
		
		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("汇总结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
		logger.info(Constant.LINE0);
	}
	
	private void filter(List<BillingPlan> resultList, BillingPlan billingPlan, String company, String projectLeader, int startYearMonth, int endYearMonth) {
		if (company != null && !company.equals(billingPlan.getProjectUnit())) {
			return;
		}
		if (projectLeader != null && projectLeader.equals(billingPlan.getProjectLeader())) {
			return;
		}
		if (startYearMonth != 0 && startYearMonth > billingPlan.getBillingYearMonthInt()) {
			return;
		}
		if (endYearMonth != 0 && endYearMonth < billingPlan.getBillingYearMonthInt()) {
			return;
		}
		resultList.add(billingPlan);
	}

	private String getAggregatingInputFilePath() {
		return Constant.propUtil.getStringEnEmpty("user.汇总.输入路径");
	}
	
	public static void main(String[] args) throws Exception {
		try {
			String configFile = InteractionHandler.handleConfigPath();
			Constant.propUtil = new PropertiesUtils(configFile);
			Constant.propUtil.init();
			
			BillingSystemCallback callback = new BillingSystemCallback();
			InteractionHandler.setActionCallback(callback);
			
			callback.actionPerformed(ActionType.Aggregating);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			InteractionHandler.handleException(e.getMessage());
		}

	}
}

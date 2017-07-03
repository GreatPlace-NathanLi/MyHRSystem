package com.nathan.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.PropertiesUtils;
import com.nathan.model.AggregatingCriteria;
import com.nathan.model.AggregatingResultSheet;
import com.nathan.model.AggregatingType;
import com.nathan.model.BillingPlan;
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
	
	public void startAggregating(AggregatingType aggregatingType, AggregatingCriteria criteria) throws Exception {
		long startTime = System.nanoTime();
		if (aggregatingInputProcesser == null) {
			aggregatingInputProcesser = new AggregatingInputProcesser();
		}

		if (billingPlanList == null) {
			String billingFile = getAggregatingInputFilePath();
			logger.info("�ӱ��ض�ȡ��Ʊ�ƻ����룺 " + billingFile);
			aggregatingInputProcesser.readBillingInput(billingFile);
			billingPlanList = aggregatingInputProcesser.getBillingPlanBook().getBillingPlanList();
		} else {
			logger.info("�ӻ����ȡ��Ʊ�ƻ�����");
		}
			
		logger.info("size: " + billingPlanList.size());
		List<BillingPlan> resultList = new ArrayList<BillingPlan>();
		for (BillingPlan billingPlan : billingPlanList) {
			filter(resultList, billingPlan, criteria, aggregatingType);
		}
		
		logger.info("�ܹ���" + resultList.size() + "����Ʊ�ƻ�������");
		long endTime = System.nanoTime();
		
		if (resultList.size() > 0) {
			switch (aggregatingType) {
			case �����:
				startServiceFeeAggregating(resultList, criteria);
				break;
			case ������:
				startBorrowingAggregating(resultList, criteria);
				break;
			default:
				break;
			}		
		} else {
			InteractionHandler.handleProgressCompleted("���ܽ�����");
		}		
		
		logger.info(Constant.LINE0);
		logger.info("���ܽ����� ��ʱ��" + (endTime - startTime) / 1000000 + "����");
		logger.info(Constant.LINE0);
	}
	
	private void startServiceFeeAggregating(List<BillingPlan> resultList, AggregatingCriteria criteria) throws Exception {
		logger.info("��ʼ����ѻ���");
		ServiceFeeSummarySheetProcesser serviceFeeSummarySheetProcesser = new ServiceFeeSummarySheetProcesser();	
		startAggregating(serviceFeeSummarySheetProcesser, resultList, criteria);
	}
	
	private void startBorrowingAggregating(List<BillingPlan> resultList, AggregatingCriteria criteria) throws Exception {
		logger.info("��ʼ����������");
		BorrowingSummaryProcesser borrowingSummarySheetProcesser = new BorrowingSummaryProcesser();	
		startAggregating(borrowingSummarySheetProcesser, resultList, criteria);
	}
	
	private void startAggregating(AggregatingResultProcesser aggregatingResultProcesser, List<BillingPlan> resultList, AggregatingCriteria criteria) throws Exception {
		AggregatingResultSheet result = aggregatingResultProcesser.process(resultList, criteria);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				AggregatingResultGUI.createAndShowAggregatingResultGUI(result, aggregatingResultProcesser);
			}
		});
	}
	
	private void filter(List<BillingPlan> resultList, BillingPlan billingPlan, AggregatingCriteria criteria,
			AggregatingType aggregatingType) {
		// logger.debug(billingPlan);
		if (criteria.getCompany() != null && !criteria.getCompany().equals(billingPlan.getProjectUnit())
				&& !Constant.ALL_COMPANY.equals(criteria.getCompany())) {
			return;
		}

		if (criteria.getProjectLeader() != null
				&& !criteria.getProjectLeader().equals(billingPlan.getProjectLeader())) {
			return;
		}

		if (criteria.getStartYearMonthInt() != 0
				&& criteria.getStartYearMonthInt() > billingPlan.getBillingYearMonthInt()) {
			return;
		}

		if (criteria.getEndYearMonthInt() != 0
				&& criteria.getEndYearMonthInt() < billingPlan.getBillingYearMonthInt()) {
			return;
		}
		
		if (AggregatingType.������.equals(aggregatingType)) {
			if (billingPlan.getBollowingDateInt() == 0 && billingPlan.getRepaymentDateInt() == 0) {
				return;
			}
//			if (criteria.getStartYearMonthInt() != 0 && criteria.getStartYearMonthInt() > billingPlan.getBollowingDateInt()) {
//				return;
//			}
//			if (criteria.getEndYearMonthInt() != 0 && criteria.getEndYearMonthInt() < billingPlan.getBollowingDateInt()) {
//				return;
//			}
		}
		
//		logger.debug("Passed");
		resultList.add(billingPlan);
	}

	private String getAggregatingInputFilePath() {
		return Constant.propUtil.getStringEnEmpty(Constant.CONFIG_����_����·��);
	}
	
	public static void main(String[] args) throws Exception {
		try {
			String configFile = InteractionHandler.handleConfigPath();
			Constant.propUtil = new PropertiesUtils(configFile);
			Constant.propUtil.init();
			
			BillingSystemCallback callback = new BillingSystemCallback();
			InteractionHandler.setActionCallback(callback);
			
//			callback.actionPerformed(ActionType.ServiceFeeAggregating);
			callback.actionPerformed(ActionType.BorrowingAggregating);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			InteractionHandler.handleException(e.getMessage());
		}

	}
}

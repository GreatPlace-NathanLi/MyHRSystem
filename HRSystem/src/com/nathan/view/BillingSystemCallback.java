package com.nathan.view;

import org.apache.log4j.Logger;

import com.nathan.controller.AggregatingOperater;
import com.nathan.controller.BillingOperater;
import com.nathan.controller.RosterProcesser;
import com.nathan.controller.VirtualBillingOperater;
import com.nathan.model.AggregatingCriteria;
import com.nathan.model.AggregatingType;

public class BillingSystemCallback implements ActionCallback {

	private static Logger logger = Logger.getLogger(BillingSystemCallback.class);

	private BillingOperater operater = null;

	private AggregatingOperater aggregatingOperater = null;

	private VirtualBillingOperater virtualBillingOperater = null;

	@Override
	public void actionPerformed(ActionType actionType) throws Exception {
		logger.debug("actionPerformed: " + actionType);
		switch (actionType) {
		case Billing:
			operater = new BillingOperater();
			operater.startBilling();
			InteractionHandler.handleProgressCompleted("开票结束！");
			break;
		case VirtualBilling:
			virtualBillingOperater = new VirtualBillingOperater();
			virtualBillingOperater.startBilling();
			InteractionHandler.handleProgressCompleted("虚拟开票结束！");
			break;
		case ServiceFeeAggregating:
			InteractionInput input = InteractionHandler.handleAggregationInput(AggregatingType.劳务费);
			if (input == null) {
				return;
			}
			if (aggregatingOperater == null) {
				aggregatingOperater = new AggregatingOperater();
			}
			aggregatingOperater.startAggregating(AggregatingType.劳务费, getAggregatingCriteria(input));
			break;
		case BorrowingAggregating:
			InteractionInput input1 = InteractionHandler.handleAggregationInput(AggregatingType.借款情况);
			if (input1 == null) {
				return;
			}
			if (aggregatingOperater == null) {
				aggregatingOperater = new AggregatingOperater();
			}
			aggregatingOperater.startAggregating(AggregatingType.借款情况, getAggregatingCriteria(input1));
			break;
		case RosterValidation:
			RosterProcesser rosterProcesser = new RosterProcesser();
			rosterProcesser.validateRosters();
		default:
			break;
		}

	}
	
	private AggregatingCriteria getAggregatingCriteria(InteractionInput input) {
		AggregatingCriteria criteria = new AggregatingCriteria();
		criteria.setCompany(input.getCompany());
		criteria.setProjectLeader(input.getProjectLeader());
		criteria.setStartYearMonthInt(input.getStartYearMonth());
		criteria.setEndYearMonthInt(input.getEndYearMonth());
		logger.debug(criteria);
		return criteria;
	}

	@Override
	public void actionSuspend(ActionType actionType) throws Exception {
		logger.debug("actionSuspend: " + actionType);
		switch (actionType) {
		case Billing:
			if (operater != null) {
				operater.endBilling();
				operater = null;
			}
			break;
		case VirtualBilling:
			if (virtualBillingOperater != null) {
				virtualBillingOperater.endBilling();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void returnPerformed(ActionType actionType) throws Exception {
		logger.debug("returnPerformed: " + actionType);
		switch (actionType) {
		case Billing:
			if (operater != null) {
				operater.release();
				operater = null;
			}
			break;
		case VirtualBilling:
			if (virtualBillingOperater != null) {
				virtualBillingOperater.release();
				virtualBillingOperater.deleteFilesAfterPrinting();
				virtualBillingOperater.saveBillingPlan();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void exitPerformed(ActionType actionType) throws Exception {
		logger.debug("exitPerformed: " + actionType);
		if (actionType == null) {
			return;
		}
		switch (actionType) {
		case Any:
			if (operater != null) {
				operater.release();
				operater = null;
			}
			if (virtualBillingOperater != null) {
				virtualBillingOperater.release();
				virtualBillingOperater = null;
			}
			break;
		case Billing:
			if (operater != null) {
				operater.release();
				operater = null;
			}
			break;
		case VirtualBilling:
			if (virtualBillingOperater != null) {
				virtualBillingOperater.release();
				virtualBillingOperater.deleteFilesAfterPrinting();
				virtualBillingOperater.saveBillingPlan();
				virtualBillingOperater = null;
			}
			break;
		default:
			break;
		}
	}

}

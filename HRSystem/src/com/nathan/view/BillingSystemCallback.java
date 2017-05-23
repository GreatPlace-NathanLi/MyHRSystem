package com.nathan.view;

import org.apache.log4j.Logger;

import com.nathan.controller.AggregatingOperater;
import com.nathan.controller.BillingOperater;
import com.nathan.controller.RosterProcesser;
import com.nathan.controller.VirtualBillingOperater;

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
			InteractionHandler.handleProgressCompleted("开票完成！");
			break;
		case VirtualBilling:
			virtualBillingOperater = new VirtualBillingOperater();
			virtualBillingOperater.startBilling();
			InteractionHandler.handleProgressCompleted("虚拟开票完成！");
			break;
		case Aggregating:
			InteractionInput input = InteractionHandler.handleAggregationInput();
			if (input == null) {
//				InteractionHandler.handleProgressCompleted("汇总中止！");
				return;
			}
			if (aggregatingOperater == null) {
				aggregatingOperater = new AggregatingOperater();
			}
			aggregatingOperater.startAggregating(input.getCompany(), input.getProjectLeader(),
					input.getStartYearMonth(), input.getEndYearMonth());
//			InteractionHandler.handleProgressCompleted("汇总完成！");
			break;
		case RosterValidation:
			RosterProcesser rosterProcesser = new RosterProcesser();
			rosterProcesser.validateRosters();
//			InteractionHandler.handleProgressCompleted("花名册校验完成！");
		default:
			break;
		}

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
				virtualBillingOperater.deleteFilesAfterPrinting();
				virtualBillingOperater.saveBillingPlan();
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

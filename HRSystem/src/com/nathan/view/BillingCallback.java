package com.nathan.view;

import com.nathan.controller.BillingOperater;
import com.nathan.controller.RosterProcesser;
import com.nathan.controller.VirtualBillingOperater;

public class BillingCallback implements ActionCallback {
	
	private BillingOperater operater = null;
	
	private VirtualBillingOperater virtualBillingOperater = null;
	
	@Override
	public void actionPerformed(ActionType actionType) throws Exception {
		switch (actionType) {  
        case Billing:  
        	operater = new BillingOperater();
			operater.startBilling();
			InteractionHandler.handleProgressCompleted("开票完成！");  
        case VirtualBilling:
        	virtualBillingOperater = new VirtualBillingOperater();
        	virtualBillingOperater.startBilling();
        	InteractionHandler.handleProgressCompleted("虚拟开票完成！");  
        case RosterValidation:
        	RosterProcesser rosterProcesser = new RosterProcesser();
        	rosterProcesser.validateRosters();
        	InteractionHandler.handleProgressCompleted("花名册校验完成！");  
        default:  
            break;  
        }  
		
	}

	@Override
	public void actionSuspend(ActionType actionType) throws Exception {
		switch (actionType) {  
        case Billing:  
			if (operater != null) {
				operater.endBilling();
			}
        case VirtualBilling:
        	if (virtualBillingOperater != null) {
        		virtualBillingOperater.endBilling();
			}
        default:  
            break;  
        }  
		
	}

	@Override
	public void returnPerformed(ActionType actionType) throws Exception {
		switch (actionType) {  
        case Billing:  
        	if (operater != null) {
				operater.release();
			}  
        case VirtualBilling:
        	if (virtualBillingOperater != null) {
        		virtualBillingOperater.release();
        		virtualBillingOperater.deleteFilesAfterPrinting();
        		virtualBillingOperater.saveBillingPlan();
			}
        default:  
            break;  
        }  	
	}

	@Override
	public void exitPerformed(ActionType actionType) throws Exception {
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
        case Billing:  
        	if (operater != null) {
				operater.release();
				operater = null;
			}  
        case VirtualBilling:
        	if (virtualBillingOperater != null) {
        		virtualBillingOperater.release();
        		virtualBillingOperater.deleteFilesAfterPrinting();
        		virtualBillingOperater.saveBillingPlan();
        		virtualBillingOperater = null;
			}
        default:  
            break;  
        }  	
	}

}

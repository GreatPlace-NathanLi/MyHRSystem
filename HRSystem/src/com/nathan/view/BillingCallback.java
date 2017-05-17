package com.nathan.view;

import com.nathan.controller.BillingOperater;
import com.nathan.controller.RosterProcesser;

public class BillingCallback implements ActionCallback {
	
	private BillingOperater operater = null;
	
	@Override
	public void actionPerformed(ActionType actionType) throws Exception {
		switch (actionType) {  
        case Billing:  
        	operater = new BillingOperater();
			operater.startBilling();
			InteractionHandler.handleProgressCompleted("��Ʊ��ɣ�");  
        case VirtualBilling:
        	break;
        case RosterValidation:
        	RosterProcesser rosterProcesser = new RosterProcesser();
        	rosterProcesser.validateRosters();
        	InteractionHandler.handleProgressCompleted("������У����ɣ�");  
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
        	break;
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
        	break;
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
			}  
        case Billing:  
        	if (operater != null) {
				operater.release();
			}  
        case VirtualBilling:
        	break;
        default:  
            break;  
        }  	
	}

}

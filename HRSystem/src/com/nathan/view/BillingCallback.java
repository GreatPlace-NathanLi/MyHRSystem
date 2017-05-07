package com.nathan.view;

import com.nathan.controller.BillingOperater;

public class BillingCallback implements ActionCallback {
	
	private BillingOperater operater = null;
	
	@Override
	public void actionPerformed(ActionType actionType) throws Exception {
		switch (actionType) {  
        case Billing:  
        	operater = new BillingOperater();
			operater.startBilling();
			InteractionHandler.handleBillingCompleted("¿ªÆ±Íê³É£¡");  
        case VirtualBilling:
        	break;
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
        	break;  
        case VirtualBilling:
        	break;
        default:  
            break;  
        }  	
	}

	@Override
	public void exitPerformed(ActionType actionType) throws Exception {
		switch (actionType) {  
        case Billing:  
        	break;  
        case VirtualBilling:
        	break;
        default:  
            break;  
        }  	
	}

}

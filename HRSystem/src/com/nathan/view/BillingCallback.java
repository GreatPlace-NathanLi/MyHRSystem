package com.nathan.view;

import com.nathan.controller.BillingOperater;
import com.nathan.exception.PayrollSheetProcessException;

public class BillingCallback implements ActionCallback {

	@Override
	public void actionPerformed(ActionType actionType) throws Exception {
		switch (actionType) {  
        case Billing:  
        	BillingOperater operater = new BillingOperater();
			operater.startBilling();
			InteractionHandler.handleBillingCompleted("��Ʊ��ɣ�");  
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
			throw new PayrollSheetProcessException("��Ʊ����ֹ��");  
        case VirtualBilling:
        	break;
        default:  
            break;  
        }  
		
	}

}

package com.nathan.controller;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;

public class SingleBillingOperater {

	private static Logger logger = Logger.getLogger(SingleBillingOperater.class);

	private static int expireDate = 20170520;

	// public static PropertiesUtils propUtil = new
	// PropertiesUtils("F:/work/project/conf/setting.properties");

	public static void main(String[] args) throws Exception {
		try {
			if (Util.getCurrentDateInt() > expireDate) {
				logger.error("Exception: 本测试版本已经超过有效期，请使用最新版本。");
				JOptionPane.showMessageDialog(null, "本测试版本已经超过有效期，请使用最新版本。", "警告", JOptionPane.ERROR_MESSAGE);		
				System.exit(0);
			}
			Constant.propUtil.init();
			// PropertyConfigurator.configure(System.getProperty(Constant.propUtil.getStringEnEmpty("system.log4j.conf")));
			BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();
			RosterProcesser rosterProcesser = new RosterProcesser();
			PayrollSheetProcesser payrollSheetProcesser = new PayrollSheetProcesser();
			PaymentDocumentProcesser paymentDocumentProcesser = new PaymentDocumentProcesser();

			Object[] options = { "确认", "取消" };
			int a = JOptionPane.showOptionDialog(null, "一键开票", "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			logger.debug("JOptionPane " + a);
			if (a != 0) {
				logger.info("取消开票！");
				System.exit(0);
			}

			long startTime = System.nanoTime();
			logger.info(Constant.LINE0);
			logger.info("开票处理开始...");
			logger.info(Constant.LINE0);

			String billingFile = Constant.propUtil.getStringValue("user.开票计划路径", Constant.BILLING_INPUT_FILE);
			logger.info("步骤1 - 读取开票计划输入： " + billingFile);
			billingPlanProcesser.processBillingPlanInput(billingFile);
			logger.info(Constant.LINE1);

			logger.info("步骤2 - 开始制作工资表...");
			String payrollFile = Constant.propUtil.getStringValue("user.工资表输出路径", Constant.PAYROLL_FILE);
			String payrollTemplateFile = Constant.propUtil.getStringValue("user.工资表模板路径",
					Constant.PAYROLL_TEMPLATE_FILE);
			payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser,
					payrollFile, payrollTemplateFile);
			logger.info(Constant.LINE1);

			logger.info("步骤3 - 保存开票计划输出： " + billingFile);
			billingPlanProcesser.writeBillingOutput(billingFile);
			logger.info(Constant.LINE1);

			logger.info("步骤4 - 开始制作付款手续单据...");
			paymentDocumentProcesser.processPaymentDocument(billingPlanProcesser.getBillingPlanBook());

			long endTime = System.nanoTime();
			logger.info(Constant.LINE0);
			logger.info("开票结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
			logger.info(Constant.LINE0);

			JOptionPane.showMessageDialog(null, "开票完成！", "德盛人力项目管理", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "警告", JOptionPane.ERROR_MESSAGE);
		}

	}

}

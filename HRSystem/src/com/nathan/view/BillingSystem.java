package com.nathan.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.PropertiesUtils;

@SuppressWarnings("serial")
public class BillingSystem extends JFrame {
    
	private static Logger logger = Logger.getLogger(BillingSystem.class);
	
	private static int expireDate = 20170620;

//	private static final long serialVersionUID = 8944798469568082934L;
//
	JTextPane textPane = new JTextPane(); // 文本窗格，编辑窗口

    JLabel statusBar = new JLabel(); // 状态栏

    JFileChooser filechooser = new JFileChooser(); // 文件选择器

    public BillingSystem() { // 构造函数
        super("德盛人力项目管理"); // 调用父类构造函数

        Action[] actions = // Action数组,各种操作命令
                {
                    new MakePayrollAction(),
                    new SummarizeAction(),
                    new RosterValidationAction(),
                    new ExitAction() };

        Container container = this.getContentPane(); // 得到容器
        container.add(createJToolBar(actions), BorderLayout.NORTH); // 增加工具栏

        setSize(280, 68); // 设置窗口尺寸
        setVisible(true); // 设置窗口可视
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口时退出程序
//      setLocationRelativeTo(null);
        setLocation(this);
        setAlwaysOnTop(true);
                     
        InteractionHandler.setFrame(this);
        InteractionHandler.setActionCallback(new BillingSystemCallback());
    }

    private void setLocation(JFrame frame) {
    	 int windowWidth = frame.getWidth(); // 获得窗口宽
    	 Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
    	 Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
    	 int screenWidth = screenSize.width; // 获取屏幕的宽
    	 
    	 setLocation(screenWidth / 2 - windowWidth / 2, 0);
    }

    private JToolBar createJToolBar(Action[] actions) { // 创建工具条
        JToolBar toolBar = new JToolBar(); // 实例化工具条
        toolBar.setLayout(new FlowLayout());
        for (int i = 0; i < actions.length; i++) {
            JButton bt = new JButton(actions[i]); // 实例化新的按钮
            bt.setRequestFocusEnabled(false); // 设置不需要焦点
            toolBar.add(bt); // 增加按钮到工具栏
        }
        return toolBar; // 返回工具栏
    }

    class MakePayrollAction extends AbstractAction { // 新建文件命令
        public MakePayrollAction() {
            super("工资表制作");
        }

        public void actionPerformed(ActionEvent e) {
        	InteractionHandler.handleBilling();
        }
    }

    class QueryAction extends AbstractAction {
        public QueryAction() {
            super("查询");
        }

        public void actionPerformed(ActionEvent e) {
        	JOptionPane.showMessageDialog(BillingSystem.this, "查询功能正在完善中，请期待"); 
        }
    }
    
    class SystemSettingAction extends AbstractAction {
        public SystemSettingAction() {
            super("系统设置");
        }

        public void actionPerformed(ActionEvent e) {
        	JOptionPane.showMessageDialog(BillingSystem.this, "系统设置功能正在完善中，请期待"); 
        }
    }
    
    class DataSettingAction extends AbstractAction {
        public DataSettingAction() {
            super("数据设置");
        }

        public void actionPerformed(ActionEvent e) {
        	System.out.println("数据设置功能正在完善中，请期待");
        	JOptionPane.showMessageDialog(BillingSystem.this, "数据设置功能正在完善中，请期待"); 
        }
    }

    class SummarizeAction extends AbstractAction {
        public SummarizeAction() {
            super("汇总");
        }

        public void actionPerformed(ActionEvent e) {
        	InteractionHandler.handleAggregation();
        }       
    }
    
    class RosterValidationAction extends AbstractAction {
        public RosterValidationAction() {
            super("花名册校验");
        }

        public void actionPerformed(ActionEvent e) {
        	InteractionHandler.handleRosterValidation();
        }
    }

    class ExitAction extends AbstractAction { // 退出命令
        public ExitAction() {
            super("退出");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0); // 退出程序
        }
    }

    class AboutAction extends AbstractAction { // 关于选项命令
        public AboutAction() {
            super("关于");
        }

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(BillingSystem.this, "德盛人力项目管理系统"); // 显示软件信息
        }
    }

    public static void main(String[] args) {
        
        try {
			InteractionHandler.handleExpireChecking(expireDate);

			String configFile = InteractionHandler.handleConfigPath();
			Constant.propUtil = new PropertiesUtils(configFile);
			Constant.propUtil.init();

			new BillingSystem();

		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
			logger.info("退出系统！");
			System.exit(0);			
		}
    }
}

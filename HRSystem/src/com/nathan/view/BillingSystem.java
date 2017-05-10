package com.nathan.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.DefaultStyledDocument;

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
                    new QueryAction(),
                    new SummarizeAction(),
                    new SystemSettingAction(),
                    new DataSettingAction(),
                    new AboutAction(),
                    new ExitAction() };

        setJMenuBar(createJMenuBar(actions)); // 设置菜单栏
        Container container = this.getContentPane(); // 得到容器
        container.add(createJToolBar(actions), BorderLayout.NORTH); // 增加工具栏
        container.add(textPane, BorderLayout.CENTER); // 增加文本窗格
        container.add(statusBar, BorderLayout.SOUTH); // 增加状态栏

        setSize(500, 500); // 设置窗口尺寸
        setVisible(true); // 设置窗口可视
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口时退出程序
        
    }

    private JMenuBar createJMenuBar(Action[] actions) { // 创建菜单栏
        JMenuBar menubar = new JMenuBar(); // 实例化菜单栏
        JMenu menuFunction = new JMenu("功能"); // 实例化菜单
        JMenu menuSetting = new JMenu("设置");
        JMenu menuAbout = new JMenu("帮助");
        menuFunction.add(new JMenuItem(actions[0])); // 增加新菜单项
        menuFunction.add(new JMenuItem(actions[1]));
        menuFunction.add(new JMenuItem(actions[2]));
        menuSetting.add(new JMenuItem(actions[3]));
        menuSetting.add(new JMenuItem(actions[4]));
        menuAbout.add(new JMenuItem(actions[5]));
        menuFunction.add(new JMenuItem(actions[6]));
        menubar.add(menuFunction); // 增加菜单
        menubar.add(menuSetting);
        menubar.add(menuAbout);
        return menubar; // 返回菜单栏
    }

    private JToolBar createJToolBar(Action[] actions) { // 创建工具条
        JToolBar toolBar = new JToolBar(); // 实例化工具条
        for (int i = 0; i < actions.length; i++) {
            JButton bt = new JButton(actions[i]); // 实例化新的按钮
            bt.setRequestFocusEnabled(false); // 设置不需要焦点
            toolBar.add(bt); // 增加按钮到工具栏
        }
        return toolBar; // 返回工具栏
    }

    class MakePayrollAction extends AbstractAction { // 新建文件命令
        public MakePayrollAction() {
            super("制作工资表");
        }

        public void actionPerformed(ActionEvent e) {
            textPane.setDocument(new DefaultStyledDocument()); // 清空文档
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
        	System.out.println("汇总功能正在完善中，请期待");
        	JOptionPane.showMessageDialog(BillingSystem.this, "汇总功能正在完善中，请期待"); 
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
//        new BillingSystem();
        
        try {
			InteractionHandler.handleExpireChecking(expireDate);

			String configFile = InteractionHandler.handleConfigPath();
			Constant.propUtil = new PropertiesUtils(configFile);
			Constant.propUtil.init();

			InteractionHandler.setActionCallback(new BillingCallback());
			InteractionHandler.showMenu();

		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
			logger.info("退出系统！");
			System.exit(0);			
//			InteractionHandler.handleException(e.getMessage());
		}
    }
}

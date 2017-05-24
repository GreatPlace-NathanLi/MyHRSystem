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
	JTextPane textPane = new JTextPane(); // �ı����񣬱༭����

    JLabel statusBar = new JLabel(); // ״̬��

    JFileChooser filechooser = new JFileChooser(); // �ļ�ѡ����

    public BillingSystem() { // ���캯��
        super("��ʢ������Ŀ����"); // ���ø��๹�캯��

        Action[] actions = // Action����,���ֲ�������
                {
                    new MakePayrollAction(),
                    new SummarizeAction(),
                    new RosterValidationAction(),
                    new ExitAction() };

        Container container = this.getContentPane(); // �õ�����
        container.add(createJToolBar(actions), BorderLayout.NORTH); // ���ӹ�����

        setSize(280, 68); // ���ô��ڳߴ�
        setVisible(true); // ���ô��ڿ���
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // �رմ���ʱ�˳�����
//      setLocationRelativeTo(null);
        setLocation(this);
        setAlwaysOnTop(true);
                     
        InteractionHandler.setFrame(this);
        InteractionHandler.setActionCallback(new BillingSystemCallback());
    }

    private void setLocation(JFrame frame) {
    	 int windowWidth = frame.getWidth(); // ��ô��ڿ�
    	 Toolkit kit = Toolkit.getDefaultToolkit(); // ���幤�߰�
    	 Dimension screenSize = kit.getScreenSize(); // ��ȡ��Ļ�ĳߴ�
    	 int screenWidth = screenSize.width; // ��ȡ��Ļ�Ŀ�
    	 
    	 setLocation(screenWidth / 2 - windowWidth / 2, 0);
    }

    private JToolBar createJToolBar(Action[] actions) { // ����������
        JToolBar toolBar = new JToolBar(); // ʵ����������
        toolBar.setLayout(new FlowLayout());
        for (int i = 0; i < actions.length; i++) {
            JButton bt = new JButton(actions[i]); // ʵ�����µİ�ť
            bt.setRequestFocusEnabled(false); // ���ò���Ҫ����
            toolBar.add(bt); // ���Ӱ�ť��������
        }
        return toolBar; // ���ع�����
    }

    class MakePayrollAction extends AbstractAction { // �½��ļ�����
        public MakePayrollAction() {
            super("���ʱ�����");
        }

        public void actionPerformed(ActionEvent e) {
        	InteractionHandler.handleBilling();
        }
    }

    class QueryAction extends AbstractAction {
        public QueryAction() {
            super("��ѯ");
        }

        public void actionPerformed(ActionEvent e) {
        	JOptionPane.showMessageDialog(BillingSystem.this, "��ѯ�������������У����ڴ�"); 
        }
    }
    
    class SystemSettingAction extends AbstractAction {
        public SystemSettingAction() {
            super("ϵͳ����");
        }

        public void actionPerformed(ActionEvent e) {
        	JOptionPane.showMessageDialog(BillingSystem.this, "ϵͳ���ù������������У����ڴ�"); 
        }
    }
    
    class DataSettingAction extends AbstractAction {
        public DataSettingAction() {
            super("��������");
        }

        public void actionPerformed(ActionEvent e) {
        	System.out.println("�������ù������������У����ڴ�");
        	JOptionPane.showMessageDialog(BillingSystem.this, "�������ù������������У����ڴ�"); 
        }
    }

    class SummarizeAction extends AbstractAction {
        public SummarizeAction() {
            super("����");
        }

        public void actionPerformed(ActionEvent e) {
        	InteractionHandler.handleAggregation();
        }       
    }
    
    class RosterValidationAction extends AbstractAction {
        public RosterValidationAction() {
            super("������У��");
        }

        public void actionPerformed(ActionEvent e) {
        	InteractionHandler.handleRosterValidation();
        }
    }

    class ExitAction extends AbstractAction { // �˳�����
        public ExitAction() {
            super("�˳�");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0); // �˳�����
        }
    }

    class AboutAction extends AbstractAction { // ����ѡ������
        public AboutAction() {
            super("����");
        }

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(BillingSystem.this, "��ʢ������Ŀ����ϵͳ"); // ��ʾ�����Ϣ
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
			logger.info("�˳�ϵͳ��");
			System.exit(0);			
		}
    }
}

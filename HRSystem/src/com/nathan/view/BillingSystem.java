package com.nathan.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.*;

public class BillingSystem extends JFrame {
    
	private static final long serialVersionUID = 8944798469568082934L;

	JTextPane textPane = new JTextPane(); // �ı����񣬱༭����

    JLabel statusBar = new JLabel(); // ״̬��

    JFileChooser filechooser = new JFileChooser(); // �ļ�ѡ����

    public BillingSystem() { // ���캯��
        super("��ʢ������Ŀ����"); // ���ø��๹�캯��

        Action[] actions = // Action����,���ֲ�������
                {
                    new MakePayrollAction(),
                    new QueryAction(),
                    new SummarizeAction(),
                    new SystemSettingAction(),
                    new DataSettingAction(),
                    new AboutAction(),
                    new ExitAction() };

        setJMenuBar(createJMenuBar(actions)); // ���ò˵���
        Container container = this.getContentPane(); // �õ�����
        container.add(createJToolBar(actions), BorderLayout.NORTH); // ���ӹ�����
        container.add(textPane, BorderLayout.CENTER); // �����ı�����
        container.add(statusBar, BorderLayout.SOUTH); // ����״̬��

        setSize(500, 500); // ���ô��ڳߴ�
        setVisible(true); // ���ô��ڿ���
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // �رմ���ʱ�˳�����
        
    }

    private JMenuBar createJMenuBar(Action[] actions) { // �����˵���
        JMenuBar menubar = new JMenuBar(); // ʵ�����˵���
        JMenu menuFunction = new JMenu("����"); // ʵ�����˵�
        JMenu menuSetting = new JMenu("����");
        JMenu menuAbout = new JMenu("����");
        menuFunction.add(new JMenuItem(actions[0])); // �����²˵���
        menuFunction.add(new JMenuItem(actions[1]));
        menuFunction.add(new JMenuItem(actions[2]));
        menuSetting.add(new JMenuItem(actions[3]));
        menuSetting.add(new JMenuItem(actions[4]));
        menuAbout.add(new JMenuItem(actions[5]));
        menuFunction.add(new JMenuItem(actions[6]));
        menubar.add(menuFunction); // ���Ӳ˵�
        menubar.add(menuSetting);
        menubar.add(menuAbout);
        return menubar; // ���ز˵���
    }

    private JToolBar createJToolBar(Action[] actions) { // ����������
        JToolBar toolBar = new JToolBar(); // ʵ����������
        for (int i = 0; i < actions.length; i++) {
            JButton bt = new JButton(actions[i]); // ʵ�����µİ�ť
            bt.setRequestFocusEnabled(false); // ���ò���Ҫ����
            toolBar.add(bt); // ���Ӱ�ť��������
        }
        return toolBar; // ���ع�����
    }

    class MakePayrollAction extends AbstractAction { // �½��ļ�����
        public MakePayrollAction() {
            super("�������ʱ�");
        }

        public void actionPerformed(ActionEvent e) {
            textPane.setDocument(new DefaultStyledDocument()); // ����ĵ�
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
        	System.out.println("���ܹ������������У����ڴ�");
        	JOptionPane.showMessageDialog(BillingSystem.this, "���ܹ������������У����ڴ�"); 
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
        new BillingSystem();
    }
}

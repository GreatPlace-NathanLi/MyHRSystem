package com.nathan.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import com.nathan.controller.AggregatingResultProcesser;
import com.nathan.controller.ServiceFeeSummarySheetProcesser;
import com.nathan.model.ServiceFeeSummary;
import com.nathan.model.ServiceFeeSummarySheet;

@SuppressWarnings("serial")
public class AggregatingResultGUI extends JPanel {

	private static Logger logger = Logger.getLogger(AggregatingResultGUI.class);

	private JTable table;
//	
//	private AggregatingResultProcesser aggregatingResultProcesser;
//	
//	private Object aggregatingResult;

//	public AggregatingResultGUI() {
//
//	}

	public AggregatingResultGUI(Object aggregatingResult, AggregatingResultProcesser aggregatingResultProcesser) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
//		this.aggregatingResult = aggregatingResult;
//		this.aggregatingResultProcesser = aggregatingResultProcesser;

		if (aggregatingResult != null && aggregatingResult instanceof ServiceFeeSummarySheet) {
			ServiceFeeSummaryTableModel serviceFeeSummaryTable = new ServiceFeeSummaryTableModel();
			logger.info("create ServiceFeeSummaryTableModel");
			serviceFeeSummaryTable.setData((ServiceFeeSummarySheet) aggregatingResult);
			table = new JTable(serviceFeeSummaryTable);
		}

		// table.getTableHeader().setPreferredSize(new Dimension(0,25));
		table.setRowHeight(20);
		table.setPreferredScrollableViewportSize(new Dimension(800, 170));
		table.setFillsViewportHeight(true);

		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();// ��Ԫ����Ⱦ��
		tcr.setHorizontalAlignment(JLabel.CENTER);// ������ʾ
		table.setDefaultRenderer(Object.class, tcr);// ������Ⱦ��

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);

		Action[] actions = // Action����,���ֲ�������
			{ 	new SaveAction(aggregatingResult, aggregatingResultProcesser), 
				new PrintAction(aggregatingResult, aggregatingResultProcesser)};
		add(createJToolBar(actions), BorderLayout.NORTH);
	}

	class ServiceFeeSummaryTableModel extends AbstractTableModel {
		final DecimalFormat formatter = new DecimalFormat("###,##0.00");
		private String[] columnNames = { "��ǲ��λ", "�·�", "��Ʊ���", "����", "�����", "��ע" };
		// private Object[][] data = {
		// {"ʵҵ��", new Integer(201601), new Double(1716180.35), new
		// Double(1606780.35), new Double(109400.00), ""},
		// {"ʵҵ��", new Integer(201602), new Double(360000),new
		// Double(337400.00), new Double(22600.00), ""},
		// {"�ϼ�", null, new Double(470000),new Double(1937400.00), new
		// Double(122600.00), ""}
		// };
		private Object[][] data = null;

		public void setData(ServiceFeeSummarySheet serviceFeeSummarySheet) {
			List<ServiceFeeSummary> serviceFeeSummaryList = serviceFeeSummarySheet.getServiceFeeSummaryList();
			int size = serviceFeeSummaryList.size();
			data = new Object[size + 1][6];
			int row = 0;
			for (; row < size; row++) {
				data[row][0] = serviceFeeSummaryList.get(row).getCompany();
				data[row][1] = serviceFeeSummaryList.get(row).getYearMonthInt();
				data[row][2] = serviceFeeSummaryList.get(row).getInvoiceAmount();
				data[row][3] = serviceFeeSummaryList.get(row).getTotalPay();
				data[row][4] = serviceFeeSummaryList.get(row).getTotalAdministrationExpenses();
				data[row][5] = serviceFeeSummaryList.get(row).getRemark();
			}
			data[size][0] = "�ϼ�";
			data[size][2] = serviceFeeSummarySheet.getInvoiceAmountSum();
			data[size][3] = serviceFeeSummarySheet.getTotalPaySum();
			data[size][4] = serviceFeeSummarySheet.getTotalAdministrationExpensesSum();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}
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

	class SaveAction extends AbstractAction {
		private AggregatingResultProcesser aggregatingResultProcesser;
		private Object aggregatingResult;
		
		public SaveAction(Object aggregatingResult, AggregatingResultProcesser aggregatingResultProcesser) {
			super("����");
			this.aggregatingResult = aggregatingResult;
			this.aggregatingResultProcesser = aggregatingResultProcesser;
		}

		public void actionPerformed(ActionEvent e) {
			aggregatingResultProcesser.save(aggregatingResult);
		}
	}

	class PrintAction extends AbstractAction {
		private AggregatingResultProcesser aggregatingResultProcesser;
		private Object aggregatingResult;
		
		public PrintAction(Object aggregatingResult, AggregatingResultProcesser aggregatingResultProcesser) {
			super("��ӡ");
			this.aggregatingResult = aggregatingResult;
			this.aggregatingResultProcesser = aggregatingResultProcesser;
		}

		public void actionPerformed(ActionEvent e) {
			aggregatingResultProcesser.print(aggregatingResult);
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public static void createAndShowServiceFeeSummaryTableGUI(Object result, AggregatingResultProcesser aggregatingResultProcesser) {	
		// Create and set up the window.
		JFrame frame = new JFrame("��ǲ������ѻ��ܽ��");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create and set up the content pane.
		AggregatingResultGUI newContentPane = new AggregatingResultGUI(result, aggregatingResultProcesser);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);
		
		// Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		setLocation(frame);
		
	}
	
	private static void setLocation(JFrame frame) {
		int windowWidth = frame.getWidth(); // ��ô��ڿ�
		int windowHeight = frame.getHeight(); // ��ô��ڸ�
		Toolkit kit = Toolkit.getDefaultToolkit(); // ���幤�߰�
		Dimension screenSize = kit.getScreenSize(); // ��ȡ��Ļ�ĳߴ�
		int screenWidth = screenSize.width; // ��ȡ��Ļ�Ŀ�
		int screenHeight = screenSize.height; // ��ȡ��Ļ�ĸ�

		frame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 4 - windowHeight / 2);
	}
	
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowServiceFeeSummaryTableGUI(new ServiceFeeSummarySheet(), new ServiceFeeSummarySheetProcesser());
			}
		});

	}
}

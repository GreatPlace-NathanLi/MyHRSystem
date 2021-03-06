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
import com.nathan.model.AggregatingResultSheet;
import com.nathan.model.BorrowingSummary;
import com.nathan.model.BorrowingSummarySheet;
import com.nathan.model.ServiceFeeSummary;
import com.nathan.model.ServiceFeeSummarySheet;

@SuppressWarnings("serial")
public class AggregatingResultGUI extends JPanel {

	private static Logger logger = Logger.getLogger(AggregatingResultGUI.class);

	private JTable table;

//	public AggregatingResultGUI() {
//
//	}

	public AggregatingResultGUI(AggregatingResultSheet aggregatingResult, AggregatingResultProcesser aggregatingResultProcesser) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		if (aggregatingResult instanceof ServiceFeeSummarySheet) {
			ServiceFeeSummaryTableModel serviceFeeSummaryTable = new ServiceFeeSummaryTableModel();
			logger.info("create ServiceFeeSummaryTableModel");
			serviceFeeSummaryTable.setData(aggregatingResult);
			table = new JTable(serviceFeeSummaryTable);
		} else if (aggregatingResult instanceof BorrowingSummarySheet) {
			BorrowingSummaryTableModel borrowingSummaryTable = new BorrowingSummaryTableModel();
			borrowingSummaryTable.setData(aggregatingResult);
			logger.info("create BorrowingSummaryTableModel");
			table = new JTable(borrowingSummaryTable);
		}

		// table.getTableHeader().setPreferredSize(new Dimension(0,25));
		table.setRowHeight(20);
		table.setPreferredScrollableViewportSize(new Dimension(1000, 280));
		table.setFillsViewportHeight(true);

		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();// 单元格渲染器
		tcr.setHorizontalAlignment(JLabel.CENTER);// 居中显示
		table.setDefaultRenderer(Object.class, tcr);// 设置渲染器

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);

		Action[] actions = // Action数组,各种操作命令
			{ 	new SaveAction(aggregatingResult, aggregatingResultProcesser), 
				new PrintAction(aggregatingResult, aggregatingResultProcesser)};
		add(createJToolBar(actions), BorderLayout.NORTH);
	}

	class ServiceFeeSummaryTableModel extends AbstractTableModel {
//		final DecimalFormat formatter = new DecimalFormat("###,##0.00");
		protected final DecimalFormat decimalFormat = new DecimalFormat("###0.00");
		private String[] columnNames = { "派遣单位", "月份", "开票金额", "工资", "劳务费", "备注" };
		// private Object[][] data = {
		// {"实业总", new Integer(201601), new Double(1716180.35), new
		// Double(1606780.35), new Double(109400.00), ""},
		// {"实业总", new Integer(201602), new Double(360000),new
		// Double(337400.00), new Double(22600.00), ""},
		// {"合计", null, new Double(470000),new Double(1937400.00), new
		// Double(122600.00), ""}
		// };
		protected Object[][] data = null;
		
		public void setColumnNames(String[] columnNames) {
			this.columnNames = columnNames;
		}

		public void setData(AggregatingResultSheet aggregatingResult) {
			ServiceFeeSummarySheet serviceFeeSummarySheet = (ServiceFeeSummarySheet) aggregatingResult;
			List<ServiceFeeSummary> serviceFeeSummaryList = serviceFeeSummarySheet.getServiceFeeSummaryList();
			int size = serviceFeeSummaryList.size();
			data = new Object[size + 2][6];
			for (int row = 0; row < size; row++) {
				data[row][0] = serviceFeeSummaryList.get(row).getCompany();
				data[row][1] = serviceFeeSummaryList.get(row).getYearMonthInt();
				data[row][2] = decimalFormat.format(serviceFeeSummaryList.get(row).getInvoiceAmount());
				data[row][3] = decimalFormat.format(serviceFeeSummaryList.get(row).getTotalPay());
				data[row][4] = decimalFormat.format(serviceFeeSummaryList.get(row).getTotalAdministrationExpenses());
				data[row][5] = serviceFeeSummaryList.get(row).getRemark();
			}
			data[size + 1][0] = "合计";
			data[size + 1][2] = decimalFormat.format(serviceFeeSummarySheet.getInvoiceAmountSum());
			data[size + 1][3] = decimalFormat.format(serviceFeeSummarySheet.getTotalPaySum());
			data[size + 1][4] = decimalFormat.format(serviceFeeSummarySheet.getTotalAdministrationExpensesSum());
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
	
	class BorrowingSummaryTableModel extends ServiceFeeSummaryTableModel {
		
		private String[] columnNames = {"领队", "单位", "合同编号", "借款日期", "借款金额", "还款日期", "还款金额", "备注" };
		
		{
			super.columnNames = columnNames;
		}
		
		public void setData(AggregatingResultSheet aggregatingResult) {
			BorrowingSummarySheet borrowingSummarySheet = (BorrowingSummarySheet) aggregatingResult;
			List<BorrowingSummary> borrowingSummaryList = borrowingSummarySheet.getBorrowingSummaryList();
			int size = borrowingSummaryList.size();
			data = new Object[size + 2][8];	
			for (int row = 0; row < size; row++) {
				BorrowingSummary summary = borrowingSummaryList.get(row);
				data[row][0] = summary.getProjectLeader();
				data[row][1] = summary.getCompany();
				data[row][2] = summary.getContractID();
				data[row][3] = summary.getBorrowingDateInt();
				data[row][4] = decimalFormat.format(summary.getBorrowingAmount());
				data[row][5] = summary.getRepaymentDateInt();
				data[row][6] = decimalFormat.format(summary.getRepaymentAmount());
				data[row][7] = summary.getRemark();
			}
			data[size + 1][0] = "合计";
			data[size + 1][4] = decimalFormat.format(borrowingSummarySheet.getBorrowingAmountSum());
			data[size + 1][6] = decimalFormat.format(borrowingSummarySheet.getRepaymentAmountSum());
		}		
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

	class SaveAction extends AbstractAction {
		private AggregatingResultProcesser aggregatingResultProcesser;
		private AggregatingResultSheet aggregatingResult;
		
		public SaveAction(AggregatingResultSheet aggregatingResult, AggregatingResultProcesser aggregatingResultProcesser) {
			super("保存");
			this.aggregatingResult = aggregatingResult;
			this.aggregatingResultProcesser = aggregatingResultProcesser;
		}

		public void actionPerformed(ActionEvent e) {
			aggregatingResultProcesser.save(aggregatingResult);
		}
	}

	class PrintAction extends AbstractAction {
		private AggregatingResultProcesser aggregatingResultProcesser;
		private AggregatingResultSheet aggregatingResult;
		
		public PrintAction(AggregatingResultSheet aggregatingResult, AggregatingResultProcesser aggregatingResultProcesser) {
			super("打印");
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
	public static void createAndShowAggregatingResultGUI(AggregatingResultSheet aggregatingResult, AggregatingResultProcesser aggregatingResultProcesser) {	
		// Create and set up the window.
		JFrame frame = new JFrame();		
		if (aggregatingResult instanceof ServiceFeeSummarySheet) {
			frame = new JFrame("派遣队劳务费汇总结果");
		}
		if (aggregatingResult instanceof BorrowingSummarySheet) {
			frame = new JFrame("领队借款情况汇总结果");
		}
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create and set up the content pane.
		AggregatingResultGUI newContentPane = new AggregatingResultGUI(aggregatingResult, aggregatingResultProcesser);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);
		
		// Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		setLocation(frame);
		
	}
	
	private static void setLocation(JFrame frame) {
		int windowWidth = frame.getWidth(); // 获得窗口宽
		int windowHeight = frame.getHeight(); // 获得窗口高
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width; // 获取屏幕的宽
		int screenHeight = screenSize.height; // 获取屏幕的高

		frame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 3 - windowHeight / 2);
	}
	
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowAggregatingResultGUI(new ServiceFeeSummarySheet(), new ServiceFeeSummarySheetProcesser());
			}
		});

	}
}

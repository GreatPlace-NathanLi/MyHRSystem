package beans.excel;

import java.io.File;
import java.io.IOException;

import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.HeaderFooter;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Range;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.format.CellFormat;
import jxl.format.Font;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.read.biff.BiffException;
import jxl.write.Boolean;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @author allen.chen
 * 
 */
public class CopyXLS {
	private static Workbook readFile(File filename) {
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(filename);
			return book;
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return book;
	}

	private static WritableWorkbook readTargetFile(File filename) {
		WritableWorkbook book = null;
		try {
			if (!filename.exists()) {
				book = Workbook.createWorkbook(filename);
			} else {
				Workbook wb = Workbook.getWorkbook(filename);
				book = Workbook.createWorkbook(filename, wb);
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return book;
	}

	public static void copyExcel(File copyFile, File targetFile, String[] sheetname)
			throws IOException, WriteException {
		Workbook copybook = readFile(copyFile);
		if (copybook == null)
			return;
		WritableWorkbook targetbook = null;
		try {
			targetbook = readTargetFile(targetFile);
			if (targetbook == null)
				return;
			int nums = targetbook.getNumberOfSheets();
			int sheetlastindex = nums;
			for (int i = 0; i < sheetname.length; i++) {
				Sheet c_sheet = copybook.getSheet(sheetname[i]);
				if (c_sheet == null)
					continue;
				WritableSheet t_sheet = targetbook.createSheet(sheetname[i], sheetlastindex);
				// 复制sheetSetting
				copySheetSettingToSheet(t_sheet, c_sheet.getSettings());
				// 复制单元格
				copyCell(c_sheet, t_sheet);
				// 合并项
				Range[] ranges = c_sheet.getMergedCells();
				copyMergedCells(t_sheet, ranges);
				sheetlastindex++;
			}
			targetbook.write();
		} finally {
			if (targetbook != null)
				targetbook.close();
		}
	}

	private static void copyMergedCells(WritableSheet t_sheet, Range[] ranges)
			throws RowsExceededException, WriteException {
		for (int i = 0; i < ranges.length; i++) {
			Range range = ranges[i];
			int toprow = range.getTopLeft().getRow();
			int topcol = range.getTopLeft().getColumn();
			int bottomrow = range.getBottomRight().getRow();
			int bottomcol = range.getBottomRight().getColumn();
			t_sheet.mergeCells(topcol, toprow, bottomcol, bottomrow);
		}
	}

	private static void copyCell(Sheet c_sheet, WritableSheet t_sheet) throws RowsExceededException, WriteException {
		int c_row = c_sheet.getRows();// 源sheet行数
		int c_col = c_sheet.getColumns();// 源sheet列数

		// 获取单元格
		Cell[][] cellArray = getCells(c_sheet, 0, c_row, 0, c_col);
		for (int i = 0; i < cellArray.length; i++) {
			Cell[] obj = cellArray[i];
			for (int j = 0; j < obj.length; j++) {
				Cell c_cell = obj[j];
				CellType c_cell_type = c_cell.getType();
				if (CellType.BOOLEAN == c_cell_type || c_cell_type == CellType.BOOLEAN_FORMULA) {
					BooleanCell c_n = (BooleanCell) c_cell;
					// 创建原始格式，不带任何cellformat
					// Boolean w_bool = new Boolean(c_n.getColumn(),
					// c_n.getRow(), c_n.getValue(), new
					// WritableCellFormat(c_n.getCellFormat()));
					Boolean w_bool = new Boolean(c_n.getColumn(), c_n.getRow(), c_n.getValue());
					t_sheet.addCell(w_bool);
				} else if (c_cell_type == CellType.DATE || c_cell_type == CellType.DATE_FORMULA) {
					DateCell d_c = (DateCell) c_cell;
					// DateTime d_time = new DateTime(d_c.getColumn(),
					// d_c.getRow(), d_c.getDate(), new
					// WritableCellFormat(d_c.getCellFormat()));
					DateTime d_time = new DateTime(d_c.getColumn(), d_c.getRow(), d_c.getDate());
					t_sheet.addCell(d_time);
				} else if (c_cell_type == CellType.NUMBER || c_cell_type == CellType.NUMBER_FORMULA) {
					NumberCell c_n = (NumberCell) c_cell;
					// Number n = new Number(c_n.getColumn(), c_n.getRow(),
					// c_n.getValue(), new
					// WritableCellFormat(c_n.getCellFormat()));
					Number n = new Number(c_n.getColumn(), c_n.getRow(), c_n.getValue());
					// if (c_n.getCellFormat() != null)
					// n.setCellFormat(c_n.getCellFormat());
					t_sheet.addCell(n);
				} else if (c_cell_type == CellType.LABEL) {
					LabelCell l_c = (LabelCell) c_cell;
					Label l = new Label(l_c.getColumn(), l_c.getRow(), l_c.getContents());
					CellFormat c_c_format = l_c.getCellFormat();
					Font c_c_font = c_c_format.getFont();
					WritableFont w_font = new WritableFont(c_c_font);
					WritableCellFormat w_c_format = new WritableCellFormat();
					w_c_format.setAlignment(c_c_format.getAlignment());
					// w_c_format.setBackground(c_c_format.getBackgroundColour());
					// w_c_format.setBackground(c_c_format.getBackgroundColour(),
					// c_c_format.getPattern());
					w_c_format.setFont(w_font);
					w_c_format.setIndentation(c_c_format.getIndentation());
					w_c_format.setOrientation(c_c_format.getOrientation());
					w_c_format.setWrap(c_c_format.getWrap());
					w_c_format.setVerticalAlignment(c_c_format.getVerticalAlignment());
					l.setCellFormat(w_c_format);
					t_sheet.addCell(l);
				} else if (c_cell_type == CellType.EMPTY) {
					EmptyCell e_c = new EmptyCell(c_cell.getColumn(), c_cell.getRow());
					t_sheet.addCell(e_c);
				} else if (c_cell_type == CellType.STRING_FORMULA) {
					LabelCell l_c = (LabelCell) c_cell;
					// Label l = new Label(l_c.getColumn(), l_c.getRow(),
					// l_c.getContents(), new
					// WritableCellFormat(l_c.getCellFormat()));
					Label l = new Label(l_c.getColumn(), l_c.getRow(), l_c.getContents());
					t_sheet.addCell(l);
				} else if (c_cell_type == CellType.FORMULA_ERROR) {

				} else if (c_cell_type == CellType.ERROR) {

				}
				//
			}
		}
		// for (int i = 0; i < c_col; i++)
		// {
		// CellView cellview = c_sheet.getColumnView(i);
		// t_sheet.setColumnView(i, cellview);
		// }
		// for (int i = 0; i < c_row; i++)
		// {
		// CellView cellview = c_sheet.getRowView(i);
		// t_sheet.setRowView(i, cellview);
		// }
		// // 复制超链接�
		// Hyperlink[] links = c_sheet.getHyperlinks();
		// for (int i = 0; i < links.length; i++)
		// {
		// Hyperlink link = links[i];
		// new WritableHyperlink(link, t_sheet);
		// }
	}

	private static Cell[][] getCells(Sheet c_sheet, int startrow, int endrow, int startcol, int endcol) {
		Cell[][] cellArray = new Cell[endrow - startrow][endcol - startcol];
		int maxRow = getRows(c_sheet);
		int maxCol = getColumns(c_sheet);
		for (int i = startrow; i < endrow && i < maxRow; i++) {
			for (int j = startcol; j < endcol && j < maxCol; j++) {
				cellArray[i - startrow][j - startcol] = c_sheet.getCell(j, i);
			}
		}
		return cellArray;
	}

	private static int getRows(Sheet sheet) {
		return sheet == null ? 0 : sheet.getRows();
	}

	/**
	 * @param sheet
	 * @return
	 */
	private static int getColumns(Sheet sheet) {
		return sheet == null ? 0 : sheet.getColumns();
	}

	/**
	 * @param sheet
	 * @param setting
	 */
	private static void copySheetSettingToSheet(WritableSheet sheet, SheetSettings setting) {
		SheetSettings sheetSettings = sheet.getSettings();
		sheetSettings.setAutomaticFormulaCalculation(setting.getAutomaticFormulaCalculation());
		sheetSettings.setBottomMargin(setting.getBottomMargin());
		sheetSettings.setCopies(setting.getCopies());
		sheetSettings.setDefaultColumnWidth(setting.getDefaultColumnWidth());
		sheetSettings.setDefaultRowHeight(setting.getDefaultRowHeight());
		sheetSettings.setDisplayZeroValues(setting.getDisplayZeroValues());
		sheetSettings.setFitHeight(setting.getFitHeight());
		sheetSettings.setFitToPages(setting.getFitToPages());
		sheetSettings.setFitWidth(setting.getFitWidth());

		HeaderFooter footer = setting.getFooter();
		if (footer != null) {
			sheetSettings.setFooter(footer);
		}
		sheetSettings.setFooterMargin(setting.getFooterMargin());
		HeaderFooter header = setting.getHeader();
		if (header != null) {
			sheetSettings.setHeader(header);
		}
		sheetSettings.setHeaderMargin(setting.getHeaderMargin());
		sheetSettings.setHidden(setting.isHidden());
		sheetSettings.setHorizontalCentre(setting.isHorizontalCentre());
		sheetSettings.setHorizontalFreeze(setting.getHorizontalFreeze());
		sheetSettings.setHorizontalPrintResolution(setting.getHorizontalPrintResolution());
		sheetSettings.setLeftMargin(setting.getLeftMargin());
		sheetSettings.setNormalMagnification(setting.getNormalMagnification());
		PageOrientation pageOrientation = setting.getOrientation();
		if (pageOrientation != null) {
			sheetSettings.setOrientation(pageOrientation);
		}
		sheetSettings.setPageBreakPreviewMagnification(setting.getPageBreakPreviewMagnification());
		sheetSettings.setPageBreakPreviewMode(setting.getPageBreakPreviewMode());
		sheetSettings.setPageStart(setting.getPageStart());
		PaperSize paperSize = setting.getPaperSize();
		if (paperSize != null) {
			sheetSettings.setPaperSize(setting.getPaperSize());
		}

		sheetSettings.setPassword(setting.getPassword());
		sheetSettings.setPasswordHash(setting.getPasswordHash());
		Range printArea = setting.getPrintArea();
		if (printArea != null) {
			sheetSettings.setPrintArea(printArea.getTopLeft() == null ? 0 : printArea.getTopLeft().getColumn(),
					printArea.getTopLeft() == null ? 0 : printArea.getTopLeft().getRow(),
					printArea.getBottomRight() == null ? 0 : printArea.getBottomRight().getColumn(),
					printArea.getBottomRight() == null ? 0 : printArea.getBottomRight().getRow());
		}

		sheetSettings.setPrintGridLines(setting.getPrintGridLines());
		sheetSettings.setPrintHeaders(setting.getPrintHeaders());

		Range printTitlesCol = setting.getPrintTitlesCol();
		if (printTitlesCol != null) {
			sheetSettings.setPrintTitlesCol(
					printTitlesCol.getTopLeft() == null ? 0 : printTitlesCol.getTopLeft().getColumn(),
					printTitlesCol.getBottomRight() == null ? 0 : printTitlesCol.getBottomRight().getColumn());
		}
		Range printTitlesRow = setting.getPrintTitlesRow();
		if (printTitlesRow != null) {
			sheetSettings.setPrintTitlesRow(
					printTitlesRow.getTopLeft() == null ? 0 : printTitlesRow.getTopLeft().getRow(),
					printTitlesRow.getBottomRight() == null ? 0 : printTitlesRow.getBottomRight().getRow());
		}

		sheetSettings.setProtected(setting.isProtected());
		sheetSettings.setRecalculateFormulasBeforeSave(setting.getRecalculateFormulasBeforeSave());
		sheetSettings.setRightMargin(setting.getRightMargin());
		sheetSettings.setScaleFactor(setting.getScaleFactor());
		sheetSettings.setSelected(setting.isSelected());
		sheetSettings.setShowGridLines(setting.getShowGridLines());
		sheetSettings.setTopMargin(setting.getTopMargin());
		sheetSettings.setVerticalCentre(setting.isVerticalCentre());
		sheetSettings.setVerticalFreeze(setting.getVerticalFreeze());
		sheetSettings.setVerticalPrintResolution(setting.getVerticalPrintResolution());
		sheetSettings.setZoomFactor(setting.getZoomFactor());
	}
}

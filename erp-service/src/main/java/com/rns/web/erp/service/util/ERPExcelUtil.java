package com.rns.web.erp.service.util;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import com.rns.web.erp.service.bo.api.ERPSalaryInfo;
import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPUser;

public class ERPExcelUtil {
	
	private static final short CELL_FILL = CellStyle.SOLID_FOREGROUND;
	private static final short FOREGROUND_COLOR = HSSFColor.BLUE.index;

	public static HSSFWorkbook generateEmployeeSalarySheet(List<ERPUser> employees) {

		if (CollectionUtils.isEmpty(employees) || employees.get(0).getFinancial() == null) {
			return null;
		}
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("salary_sheet");
		sheet.setDefaultColumnWidth(30);

		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		style.setFillForegroundColor(FOREGROUND_COLOR);
		style.setFillPattern(CELL_FILL);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);

		// create header row
		HSSFRow header = sheet.createRow(0);
		
		int colIndex = 0;

		header.createCell(colIndex).setCellValue("Employee Id");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Employee Name");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Designation");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Account number");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("PF Account number");
		header.getCell(colIndex++).setCellStyle(style);
		
		header.createCell(colIndex).setCellValue("Gross salary");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Payable salary");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Basic");
		header.getCell(colIndex++).setCellStyle(style);

		for(ERPSalaryInfo salaryInfo:employees.get(0).getFinancial().getBenefits()) {
			header.createCell(colIndex).setCellValue(salaryInfo.getRule());
			header.getCell(colIndex++).setCellStyle(style);
		}
		
		header.createCell(colIndex).setCellValue("Total Benefits(A)");
		header.getCell(colIndex++).setCellStyle(style);
		
		for(ERPSalaryInfo salaryInfo:employees.get(0).getFinancial().getDeductions()) {
			header.createCell(colIndex).setCellValue(salaryInfo.getRule());
			header.getCell(colIndex++).setCellStyle(style);
		}
		
		header.createCell(colIndex).setCellValue("Total Deductions(B)");
		header.getCell(colIndex++).setCellStyle(style);
		
		header.createCell(colIndex).setCellValue("Net salary(A-B)");
		header.getCell(colIndex++).setCellStyle(style);
		
		
		int rowCount = 1;

		for (ERPUser employee : employees) {
			int colCount = 0;
			HSSFRow aRow = sheet.createRow(rowCount++);
			aRow.createCell(colCount++).setCellValue(employee.getId());
			aRow.createCell(colCount++).setCellValue(employee.getName());
			aRow.createCell(colCount++).setCellValue(employee.getDesignation());
			if(employee.getFinancial() != null) {
				aRow.createCell(colCount++).setCellValue(employee.getFinancial().getAccountNumber());
				aRow.createCell(colCount++).setCellValue(employee.getFinancial().getPfNumber());
				aRow.createCell(colCount++).setCellValue(CommonUtils.getStringValue(employee.getFinancial().getSalary()));
				aRow.createCell(colCount++).setCellValue(CommonUtils.getStringValue(employee.getFinancial().getAmountPayable()));
				if(employee.getFinancial().getBasic() != null) {
					aRow.createCell(colCount++).setCellValue(CommonUtils.getStringValue(employee.getFinancial().getBasic().getAmount()));
				}
				if(CollectionUtils.isNotEmpty(employee.getFinancial().getBenefits())) {
					for(ERPSalaryInfo salaryInfo:employee.getFinancial().getBenefits()) {
						aRow.createCell(colCount++).setCellValue(CommonUtils.getStringValue(salaryInfo.getAmount()));
					}
				}
				aRow.createCell(colCount++).setCellValue(CommonUtils.getStringValue(employee.getFinancial().getTotalBenefits()));
				if(CollectionUtils.isNotEmpty(employee.getFinancial().getDeductions())) {
					for(ERPSalaryInfo salaryInfo:employee.getFinancial().getDeductions()) {
						aRow.createCell(colCount++).setCellValue(CommonUtils.getStringValue(salaryInfo.getAmount()));
					}
				}
				aRow.createCell(colCount++).setCellValue(CommonUtils.getStringValue(employee.getFinancial().getTotalDeductions()));
				aRow.createCell(colCount++).setCellValue(CommonUtils.getStringValue(employee.getFinancial().getAmountPayable()));
			}
			
		}
		return workbook;
	}

	public static HSSFWorkbook generateEmployeeLeavesSheet(List<ERPUser> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return null;
		}
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("leaves_sheet");
		sheet.setDefaultColumnWidth(30);

		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		style.setFillForegroundColor(FOREGROUND_COLOR);
		style.setFillPattern(CELL_FILL);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);

		// create header row
		HSSFRow header = sheet.createRow(0);
		
		int colIndex = 0;
		
		header.createCell(colIndex).setCellValue("Sr. No");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Employee Id");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Employee Name");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Designation");
		header.getCell(colIndex++).setCellStyle(style);
		
		for(ERPLeaveCategory category:employees.get(0).getLeaveCount()) {
			header.createCell(colIndex).setCellValue(category.getName());
			header.getCell(colIndex++).setCellStyle(style);
		}
		
		header.createCell(colIndex).setCellValue("Total");
		header.getCell(colIndex++).setCellStyle(style);

		/*header.createCell(colIndex).setCellValue("Leave Type");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Leave from");
		header.getCell(colIndex++).setCellStyle(style);
		
		header.createCell(colIndex).setCellValue("Leave to");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("No of days");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Reason");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Without Pay days");
		header.getCell(colIndex++).setCellStyle(style);*/
		
		int rowCount = 1;

		for (ERPUser employee : employees) {
			int colCount = 0;
			HSSFRow aRow = sheet.createRow(rowCount++);
			aRow.createCell(colCount++).setCellValue(rowCount - 1);
			aRow.createCell(colCount++).setCellValue(employee.getId());
			aRow.createCell(colCount++).setCellValue(employee.getName());
			aRow.createCell(colCount++).setCellValue(employee.getDesignation());
			for(ERPLeaveCategory category:employee.getLeaveCount()) {
				aRow.createCell(colCount++).setCellValue(category.getCount());
			}
			aRow.createCell(colCount++).setCellValue(employee.getTotalLeaves());
		}
		return workbook;
	}

	public static HSSFWorkbook generateEmployeeLeavesSheet(ERPUser user) {
		if(user == null) {
			return null;
		}
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("leaves_details");
		sheet.setDefaultColumnWidth(30);

		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		style.setFillForegroundColor(FOREGROUND_COLOR);
		style.setFillPattern(CELL_FILL);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);

		// create header row
		HSSFRow header = sheet.createRow(0);
		
		int colIndex = 0;

		header.createCell(colIndex).setCellValue("Sr. No");
		header.getCell(colIndex++).setCellStyle(style);
		
		header.createCell(colIndex).setCellValue("Leave from");
		header.getCell(colIndex++).setCellStyle(style);
		
		header.createCell(colIndex).setCellValue("Leave to");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("No of days");
		header.getCell(colIndex++).setCellStyle(style);
		
		header.createCell(colIndex).setCellValue("Leave Type");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Reason");
		header.getCell(colIndex++).setCellStyle(style);
		
		header.createCell(colIndex).setCellValue("Applied on");
		header.getCell(colIndex++).setCellStyle(style);
		
		header.createCell(colIndex).setCellValue("Status");
		header.getCell(colIndex++).setCellStyle(style);

		header.createCell(colIndex).setCellValue("Without Pay days");
		header.getCell(colIndex++).setCellStyle(style);
		
		int rowCount = 1;
		
		if(CollectionUtils.isEmpty(user.getLeaves())) {
			return workbook;
		}

		for (ERPLeave leave : user.getLeaves()) {
			int colCount = 0;
			HSSFRow aRow = sheet.createRow(rowCount++);
			aRow.createCell(colCount++).setCellValue(rowCount - 1);
			aRow.createCell(colCount++).setCellValue(leave.getFromString());
			aRow.createCell(colCount++).setCellValue(leave.getToString());
			aRow.createCell(colCount++).setCellValue(leave.getNoOfDays());
			if(leave.getType() != null) {
				aRow.createCell(colCount++).setCellValue(leave.getType().getName());
			}
			aRow.createCell(colCount++).setCellValue(leave.getReason());
			aRow.createCell(colCount++).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(leave.getAppliedDate()));
			aRow.createCell(colCount++).setCellValue(leave.getStatus());
			aRow.createCell(colCount++).setCellValue(leave.getWithoutPay());
		}
		return workbook;
	}

}

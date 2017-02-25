package com.rns.web.erp.service.util;

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
import com.rns.web.erp.service.bo.domain.ERPUser;

public class ERPExcelUtil {
	
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
		style.setFillForegroundColor(HSSFColor.BLUE.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
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

}

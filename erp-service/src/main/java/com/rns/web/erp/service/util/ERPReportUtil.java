package com.rns.web.erp.service.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.rns.web.erp.service.bo.api.ERPSalaryInfo;
import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPUser;

public class ERPReportUtil {
	
	public static InputStream getSalarySlip(ERPUser employee) {
		try {
		    String contentPath = "report/salary_slip.html";
			String result = CommonUtils.readFile(contentPath);
			String cssString = CommonUtils.readFile("report/report.css");
			if (employee != null) {
				result = StringUtils.replace(result, "{name}", CommonUtils.getStringValue(employee.getName()));
				result = StringUtils.replace(result, "{employeeId}", CommonUtils.getStringValue(employee.getId()));
				result = StringUtils.replace(result, "{email}", CommonUtils.getStringValue(employee.getEmail()));
				result = StringUtils.replace(result, "{phone}", CommonUtils.getStringValue(employee.getPhone()));
				result = StringUtils.replace(result, "{designation}", CommonUtils.getStringValue(employee.getDesignation()));
				if(employee.getCompany() != null) {
					result = StringUtils.replace(result, "{companyName}", CommonUtils.getStringValue(employee.getCompany().getName()));
					if(employee.getCompany().getFilter() != null) {
						result = StringUtils.replace(result, "{year}", CommonUtils.getStringValue(employee.getCompany().getFilter().getYear()));
						result = StringUtils.replace(result, "{month}", CommonUtils.getStringValue(new DateFormatSymbols().getMonths()[employee.getCompany().getFilter().getMonth()]));
					}
				}
				result = setEmployeeFinancial(employee, result);
			}
			InputStream is = generatePdf(result, cssString);
		    return is;
		    //file.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}
	
	

	private static InputStream generatePdf(String result, String cssString) throws DocumentException, IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		//OutputStream file = new FileOutputStream(new File("Test.pdf"));
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
		document.open();
		//XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(result));
		//HTMLWorker htmlWorker = new HTMLWorker(document);
		//htmlWorker.parse(new StringReader(result));
		
		CSSResolver cssResolver = new StyleAttrCSSResolver();
		CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(cssString.getBytes()));
		cssResolver.addCss(cssFile);
		
		// HTML
		HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
 
		// Pipelines
		PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
		HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
		CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
 
		// XML Worker
		XMLWorker worker = new XMLWorker(css, true);
		XMLParser p = new XMLParser(worker);
		p.parse(new ByteArrayInputStream(result.getBytes()));
		
		document.close();
		InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		return is;
	}

	private static String setEmployeeFinancial(ERPUser employee, String result) {
		if(employee.getFinancial() == null) {
			result = StringUtils.replace(result, "{benefits}", "");
			result = StringUtils.replace(result, "{deductions}", "");
			return result;
		}
		result = StringUtils.replace(result, "{pfNumber}", CommonUtils.getStringValue(employee.getFinancial().getPfNumber()));
		result = StringUtils.replace(result, "{employeeSalary}", CommonUtils.getStringValue(employee.getFinancial().getSalary()));
		result = StringUtils.replace(result, "{payableSalary}", CommonUtils.getStringValue(employee.getFinancial().getAmountPayable()));
		result = StringUtils.replace(result, "{pfNumber}", CommonUtils.getStringValue(employee.getFinancial().getPfNumber()));
		if(employee.getFinancial().getBasic()== null || employee.getFinancial().getBasic().getAmount() == null) {
			return result;
		}
		result = StringUtils.replace(result, "{basicSalary}", employee.getFinancial().getBasic().getAmount().toString());
		StringBuilder benefits = new StringBuilder();
		if(CollectionUtils.isNotEmpty(employee.getFinancial().getBenefits())) {
			Integer count = 1;
			for(ERPSalaryInfo salaryInfo:employee.getFinancial().getBenefits()) {
				benefits.append("<tr>").append("<td>").append(count).append("</td>")
									   .append("<td>").append(salaryInfo.getRule()).append("</td>")
									   .append("<td>").append(salaryInfo.getAmount()).append("</td>").append("</tr>");
				count++;
			}
			result = StringUtils.replace(result, "{totalBenefits}", employee.getFinancial().getTotalBenefits().toString());
		}
		result = StringUtils.replace(result, "{benefits}", benefits.toString());
		StringBuilder deductions = new StringBuilder();
		if(CollectionUtils.isNotEmpty(employee.getFinancial().getDeductions())) {
			Integer count = 1;
			for(ERPSalaryInfo salaryInfo:employee.getFinancial().getDeductions()) {
				deductions.append("<tr>").append("<td>").append(count).append("</td>")
									   .append("<td>").append(salaryInfo.getRule()).append("</td>")
									   .append("<td>").append(salaryInfo.getAmount()).append("</td>").append("</tr>");
				count++;
			}
			result = StringUtils.replace(result, "{totalDeductions}", CommonUtils.getStringValue(employee.getFinancial().getTotalDeductions()));
		}
		result = StringUtils.replace(result, "{deductions}", deductions.toString());
		return result;
	}
	
	public static InputStream getBankStatement(ERPCompany company) {
		if(company == null || CollectionUtils.isEmpty(company.getEmployees())) {
			return null;
		}
		try {
			String contentPath = "report/bank_statement.html";
			String result = CommonUtils.readFile(contentPath);
			String cssString = CommonUtils.readFile("report/report.css");
			result = StringUtils.replace(result, "{companyName}", CommonUtils.getStringValue(company.getName()));
			if (company.getFilter() != null) {
				result = StringUtils.replace(result, "{year}", CommonUtils.getStringValue(company.getFilter().getYear()));
				result = StringUtils.replace(result, "{month}", CommonUtils.getStringValue(new DateFormatSymbols().getMonths()[company.getFilter().getMonth()]));
			}
			if(company.getFinancial() != null) {
				result = StringUtils.replace(result, "{companyBank}", CommonUtils.getStringValue(company.getFinancial().getBankName()));
				result = StringUtils.replace(result, "{companyBankBranch}", CommonUtils.getStringValue(company.getFinancial().getBranchName()));
				result = StringUtils.replace(result, "{companyAccountNumber}", CommonUtils.getStringValue(company.getFinancial().getAccountNumber()));
			}
			result = StringUtils.replace(result, "{date}", new SimpleDateFormat(ERPConstants.DATE_FORMAT).format(new Date()));
			StringBuilder bankData = new StringBuilder();
			Integer count = 0;
			for(ERPUser employee: company.getEmployees()) {
				if(employee.getFinancial() == null){
					continue;
				}
				bankData.append("<tr>");
				bankData.append("<td>").append(count++).append("</td>")
						.append("<td>").append(employee.getName()).append("</td>")
						.append("<td>").append(employee.getFinancial().getAccountNumber()).append("</td>")
						.append("<td>").append(employee.getFinancial().getAmountPayable()).append("</td>");
				bankData.append("</tr>");
			}
			result = StringUtils.replace(result, "{employees}", bankData.toString());
			InputStream is = generatePdf(result, cssString);
			return is;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

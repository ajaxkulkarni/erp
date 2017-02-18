package com.rns.web.erp.service.bo.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rns.web.erp.service.bo.api.ERPSalaryInfo;

public class ERPFinancial {
	
	private Integer id;
	private String pan;
	private String accountNumber;
	private String bankName;
	private String pfNumber;
	private BigDecimal salary;
	private ERPSalaryInfo basic;
	private List<ERPSalaryInfo> benefits;
	private List<ERPSalaryInfo> deductions;
	private BigDecimal totalBenefits;
	private BigDecimal totalDeductions;
	private BigDecimal amountPayable;
	
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getPfNumber() {
		return pfNumber;
	}
	public void setPfNumber(String pfNumber) {
		this.pfNumber = pfNumber;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigDecimal getSalary() {
		return salary;
	}
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
	public ERPSalaryInfo getBasic() {
		return basic;
	}
	public void setBasic(ERPSalaryInfo basic) {
		this.basic = basic;
	}
	public List<ERPSalaryInfo> getBenefits() {
		if(benefits == null) {
			benefits = new ArrayList<ERPSalaryInfo>();
		}
		return benefits;
	}
	public void setBenefits(List<ERPSalaryInfo> benefits) {
		this.benefits = benefits;
	}
	public List<ERPSalaryInfo> getDeductions() {
		if(deductions == null) {
			deductions = new ArrayList<ERPSalaryInfo>();
		}
		return deductions;
	}
	public void setDeductions(List<ERPSalaryInfo> deductions) {
		this.deductions = deductions;
	}
	public BigDecimal getTotalBenefits() {
		if(totalBenefits == null) {
			totalBenefits = BigDecimal.ZERO;
		}
		return totalBenefits;
	}
	public void setTotalBenefits(BigDecimal totalBenefits) {
		this.totalBenefits = totalBenefits;
	}
	public BigDecimal getTotalDeductions() {
		if(totalDeductions == null) {
			totalDeductions = BigDecimal.ZERO;
		}
		return totalDeductions;
	}
	public void setTotalDeductions(BigDecimal totalDeductions) {
		this.totalDeductions = totalDeductions;
	}
	public BigDecimal getAmountPayable() {
		return amountPayable;
	}
	public void setAmountPayable(BigDecimal amountPayable) {
		this.amountPayable = amountPayable;
	}

}

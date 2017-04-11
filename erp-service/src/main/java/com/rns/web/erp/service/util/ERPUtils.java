package com.rns.web.erp.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rns.web.erp.service.bo.domain.ERPFilter;
import com.rns.web.erp.service.bo.domain.ERPFinancial;
import com.rns.web.erp.service.bo.domain.ERPSalaryInfo;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPEmployeeSalarySlips;
import com.rns.web.erp.service.dao.domain.ERPEmployeeSalaryStructure;
import com.rns.web.erp.service.dao.impl.ERPUserDAO;

public class ERPUtils {
	

	public static void saveEmployeeSalarySlips(ERPFilter erpFilter, Session session, ERPUserDAO dao,
			List<ERPEmployeeDetails> employees) {
		if(CollectionUtils.isNotEmpty(employees)) {
			for(ERPEmployeeDetails employeeDetails: employees) {
				ERPUser employee = ERPDataConverter.getEmployee(employeeDetails);
				if(employee == null) {
					continue;
				}
				setEmployeeFinancial(session, employee, employeeDetails);
				if(employee.getFinancial() == null || employee.getFinancial().getAmountPayable() == null) {
					continue;
				}
				Transaction tx = session.beginTransaction();
				ERPEmployeeSalarySlips salarySlip = null;
				salarySlip = dao.getEmployeeSalarySlip(employeeDetails.getId(),erpFilter.getYear(),erpFilter.getMonth(), session);
				if(salarySlip == null) {
					salarySlip = new ERPEmployeeSalarySlips();
				}
				salarySlip.setAmountPaid(employee.getFinancial().getAmountPayable());
				salarySlip.setEmployee(employeeDetails);
				salarySlip.setUpdatedDate(new Date());
				salarySlip.setMonth(erpFilter.getMonth());
				salarySlip.setYear(erpFilter.getYear());
				List<ERPSalaryInfo> benefits = employee.getFinancial().getBenefits();
				if(benefits == null) {
					benefits = new ArrayList<ERPSalaryInfo>();
				}
				benefits.add(employee.getFinancial().getBasic());
				salarySlip.setBenefits(CommonUtils.getSalaryInfoString(benefits));
				salarySlip.setDeductions(CommonUtils.getSalaryInfoString(employee.getFinancial().getDeductions()));
				session.persist(salarySlip);
				tx.commit();
			}
		}
	}
	
	public static void setEmployeeFinancial(Session session, ERPUser user, ERPEmployeeDetails emp) {
		ERPFinancial financial = user.getFinancial();
		if(financial == null || financial.getSalary() == null) {
			return;
		}
		List<ERPEmployeeSalaryStructure> salaryStructures = emp.getStructures();
		if(CollectionUtils.isEmpty(salaryStructures)) {
			return;
		}
		for(ERPEmployeeSalaryStructure structure: salaryStructures) {
			if(structure.getSalaryStructure() == null) {
				continue;
			}
			if(StringUtils.equalsIgnoreCase("Basic",structure.getSalaryStructure().getRule())) {
				ERPSalaryInfo basic = ERPDataConverter.getSalaryInfo(structure);
				/*if(basic.getAmount() == null) {
					basic.setAmount(financial.getSalary().multiply(basic.getPercentage().divide(new BigDecimal(100))));
				}*/
				financial.setBasic(basic);
			}
		}
		if(financial.getBasic() == null || financial.getBasic().getAmount() == null) {
			return;
		}
		financial.setTotalBenefits(financial.getBasic().getAmount());
		for(ERPEmployeeSalaryStructure structure: salaryStructures) {
			if(structure.getSalaryStructure() == null) {
				continue;
			}
			if(StringUtils.equalsIgnoreCase("Basic",structure.getSalaryStructure().getRule())) {
				continue;
			}
			ERPSalaryInfo salaryInfo = ERPDataConverter.getSalaryInfo(structure);
			/*if(AMOUNT_TYPE_AMOUNT.equals(salaryInfo.getAmountType())) {
				salaryInfo.setAmount(salaryInfo.getAmount());
			} else if(salaryInfo.getPercentage() != null && salaryInfo.getPercentage().compareTo(BigDecimal.ZERO) > 0) {
				salaryInfo.setAmount(financial.getBasic().getAmount().multiply(salaryInfo.getPercentage().divide(new BigDecimal(100))));
			}*/
			if(StringUtils.equals("add", salaryInfo.getType())) {
				financial.getBenefits().add(salaryInfo);
				if(salaryInfo.getAmount() != null) {
					financial.setTotalBenefits(salaryInfo.getAmount().add(financial.getTotalBenefits()));
				}
			} else {
				financial.getDeductions().add(salaryInfo);
				if(salaryInfo.getAmount() != null) {
					financial.setTotalDeductions(salaryInfo.getAmount().add(financial.getTotalDeductions()));
				}
			}
		}
		if(user.getWithoutPayLeaves() != null && user.getWithoutPayLeaves() > 0) {
			ERPSalaryInfo salaryInfo = new ERPSalaryInfo();
			salaryInfo.setRule("Withoutpay Leaves");
			salaryInfo.setAmount(user.getFinancial().getSalary().divide(new BigDecimal(30),RoundingMode.HALF_UP).multiply((new BigDecimal(user.getWithoutPayLeaves()))));
			financial.getDeductions().add(salaryInfo);
			financial.setTotalDeductions(salaryInfo.getAmount().add(financial.getTotalDeductions()));
		}
		ERPSalaryInfo salaryInfo = new ERPSalaryInfo();
		salaryInfo.setAmount(financial.getSalary().subtract(financial.getTotalBenefits()));
		salaryInfo.setRule("Other allowances");
		financial.getBenefits().add(salaryInfo);
		financial.setTotalBenefits(financial.getTotalBenefits().add(salaryInfo.getAmount()));
		financial.setAmountPayable(financial.getSalary().subtract(financial.getTotalDeductions()));
	}
	
	

}

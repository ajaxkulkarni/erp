package com.rns.web.erp.service.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.rns.web.erp.service.dao.domain.ERPCompanyDetails;
import com.rns.web.erp.service.dao.domain.ERPCompanyLeavePolicy;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPEmployeeLeave;
import com.rns.web.erp.service.dao.domain.ERPEmployeeLeaveBalance;
import com.rns.web.erp.service.dao.domain.ERPEmployeeSalarySlips;
import com.rns.web.erp.service.dao.domain.ERPEmployeeSalaryStructure;
import com.rns.web.erp.service.dao.domain.ERPLeaveType;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.domain.ERPSalaryStructure;
import com.rns.web.erp.service.util.ERPConstants;

public class ERPUserDAO {
	
	public ERPLoginDetails getLoginDetails(String email, Session session) {
		Query query = session.createQuery("from ERPLoginDetails where email=:email");
		query.setString("email", email);
		List<ERPLoginDetails> login = query.list();
		if (CollectionUtils.isEmpty(login)) {
			return null;
		}
		return login.get(0);
	}

	public ERPEmployeeLeave getLeaveDetails(Integer id, Session session) {
		Query query = session.createQuery("from ERPEmployeeLeave where id=:id");
		query.setInteger("id", id);
		List<ERPEmployeeLeave> leave = query.list();
		if (CollectionUtils.isEmpty(leave)) {
			return null;
		}
		return leave.get(0);
	}

	public List<ERPEmployeeDetails> getCompanyEmployees(Integer id, Session session) {
		Query query = session.createQuery("from ERPEmployeeDetails where company.id=:id AND status IS NULL OR status!=:deleted");
		query.setInteger("id", id);
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		return query.list();
	}

	public List<ERPCompanyLeavePolicy> getCompanyLeaveTypes(Session session, Integer id) {
		Query query = session.createQuery("from ERPCompanyLeavePolicy where company.id=:id order by ID");
		query.setInteger("id", id);
		return query.list();
	}
	
	public List<ERPLeaveType> getAllLeaveTypes(Session session) {
		return session.createQuery("from ERPLeaveType order by ID").list();
	}

	public List<ERPEmployeeLeave> getEmployeeLeaveDetails(Session session, Integer id, Date date1, Date date2) {
		String queryString = "from ERPEmployeeLeave where employee.id=:id AND status!=:cancelled";
		if(date1 !=null && date2!= null) {
			queryString = queryString + " AND (fromDate>=:date1 AND fromDate<=:date2 OR toDate>=:date1 AND toDate<=:date2)";
		}
		Query query = session.createQuery(queryString);
		query.setInteger("id", id);
		query.setString("cancelled", ERPConstants.LEAVE_STATUS_CANCELLED);
		if(date1 !=null && date2!= null) {
			query.setDate("date1", date1);
			query.setDate("date2", date2);
		}
		return query.list();
	}

	public int getEmployeeLeaveCount(Session session, Integer id, Integer type, Date date1, Date date2, String countType) {
		//String countType = "noOfDays";
		String queryString = "select sum(" + countType + ") from ERPEmployeeLeave where employee.id=:id AND type.id=:type AND status!=:cancelled";
		if(date1 !=null && date2!= null) {
			queryString = queryString + " AND (fromDate>=:date1 AND fromDate<=:date2 OR toDate>=:date1 AND toDate<=:date2)";
		}
		Query query = session.createQuery(queryString);
		query.setInteger("id", id);
		query.setInteger("type", type);
		query.setString("cancelled", ERPConstants.LEAVE_STATUS_CANCELLED);
		if(date1 !=null && date2!= null) {
			query.setDate("date1", date1);
			query.setDate("date2", date2);
		}
		List list = query.list();
		if(CollectionUtils.isNotEmpty(list)) {
			Number number = (Number) list.get(0);
			if(number != null) {
				return number.intValue();
			}
		}
		return 0;
	}

	public ERPCompanyLeavePolicy getCompanyLeavePolicy(Integer type, Integer company, Session session) {
		Query query = session.createQuery("from ERPCompanyLeavePolicy where company.id=:company AND type.id=:type");
		query.setInteger("company", company);
		query.setInteger("type", type);
		List<ERPCompanyLeavePolicy> list = query.list();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	public ERPEmployeeDetails getEmployeeById(Integer id, Session session) {
		Query query = session.createQuery("from ERPEmployeeDetails where id=:id");
		query.setInteger("id", id);
		List<ERPEmployeeDetails> list = query.list();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	public List<ERPEmployeeLeave> getEmployeeLeaves(Session session, Integer id, Date from, Date to) {
		Query query = session.createQuery("from ERPEmployeeLeave where employee.id=:id AND fromDate<=:from_date AND toDate>=:from_date AND status!=:cancelled");
		query.setInteger("id", id);
		query.setDate("from_date", from);
		query.setString("cancelled", ERPConstants.LEAVE_STATUS_CANCELLED);
		List<ERPEmployeeLeave> dates = query.list();
		if(CollectionUtils.isNotEmpty(dates)) {
			return dates;
		}
		query = session.createQuery("from ERPEmployeeLeave where employee.id=:id AND fromDate<=:to_date AND toDate>=:to_date AND status!=:cancelled");
		query.setInteger("id", id);
		query.setDate("to_date", to);
		query.setString("cancelled", ERPConstants.LEAVE_STATUS_CANCELLED);
		return query.list();
	}

	public ERPSalaryStructure getSalaryStructure(Integer id, Session session) {
		Query query = session.createQuery("from ERPSalaryStructure where id=:id order by id");
		query.setInteger("id", id);
		List<ERPSalaryStructure> list = query.list();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
	
	public List<ERPSalaryStructure> getCompanySalaryStructure(Integer companyId, Session session) {
		Query query = session.createQuery("from ERPSalaryStructure where company.id=:companyId order by id");
		query.setInteger("companyId", companyId);
		return query.list();
	}

	/*public void removeAllSalaryStructure(Integer id, Session session) {
		Query query = session.createQuery("delete from ERPSalaryStructure where company.id=:companyId");
		query.setInteger("companyId", id);
		query.executeUpdate();
	}*/
	
	public void removeAllEmployeeSalaryStructure(Integer id, Session session) {
		Query query = session.createQuery("delete from ERPEmployeeSalaryStructure where employee.id=:id");
		query.setInteger("id", id);
		query.executeUpdate();
	}

	public Integer getWithoutPayCount(Session session, Integer id, Date date1, Date date2) {
		String queryString = "select sum(withoutPay) from ERPEmployeeLeave where employee.id=:id AND status!=:cancelled";
		if(date1 !=null && date2!= null) {
			queryString = queryString + " AND (fromDate>=:date1 AND fromDate<=:date2 OR toDate>=:date1 AND toDate<=:date2)";
		}
		Query query = session.createQuery(queryString);
		query.setInteger("id", id);
		query.setString("cancelled", ERPConstants.LEAVE_STATUS_CANCELLED);
		if(date1 !=null && date2!= null) {
			query.setDate("date1", date1);
			query.setDate("date2", date2);
		}
		List list = query.list();
		if(CollectionUtils.isNotEmpty(list)) {
			Number number = (Number) list.get(0);
			if(number != null) {
				return number.intValue();
			}
		}
		return 0;
	}

	public ERPCompanyDetails getCompany(Integer id, Session session) {
		Query query = session.createQuery("from ERPCompanyDetails where id=:companyId");
		query.setInteger("companyId", id);
		List<ERPCompanyDetails> list = query.list();
		if(CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	public ERPEmployeeSalaryStructure getEmployeeSalaryStructure(Integer structureId, Integer employeeId, Session session) {
		Query query = session.createQuery("from ERPEmployeeSalaryStructure where salaryStructure.id=:structureId AND employee.id=:employeeId");
		query.setInteger("structureId", structureId);
		query.setInteger("employeeId", employeeId);
		List<ERPEmployeeSalaryStructure> list = query.list();
		if(CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	public ERPEmployeeSalarySlips getEmployeeSalarySlip(Integer id, Integer year, Integer month, Session session) {
		Query query = session.createQuery("from ERPEmployeeSalarySlips where month=:month AND year=:year AND employee.id=:employeeId");
		query.setInteger("month", month);
		query.setInteger("year", year);
		query.setInteger("employeeId", id);
		List<ERPEmployeeSalarySlips> list = query.list();
		if(CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	public List<ERPEmployeeDetails> getAllEmployees(Session session) {
		Query query = session.createQuery("from ERPEmployeeDetails");
		return query.list();
	}

	public ERPEmployeeLeaveBalance getEmployeeLeaveBalance(Integer employeeId, Integer leaveType, Session session) {
		Query query = session.createQuery("from ERPEmployeeLeaveBalance where employee.id=:employeeId AND type.id=:leaveType");
		query.setInteger("employeeId", employeeId);
		query.setInteger("leaveType", leaveType);
		List<ERPEmployeeLeaveBalance> list = query.list();
		if(CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}
	
	public List<ERPEmployeeLeaveBalance> getAllEmployeeLeaveBalances(Session session) {
		Query query = session.createQuery("from ERPEmployeeLeaveBalance");
		return query.list();
	}

	public ERPEmployeeDetails getEmployeeByEmail(String email, Session session) {
		Query query = session.createQuery("from ERPEmployeeDetails where email=:email AND status!=:deleted");
		query.setString("email", email);
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		List<ERPEmployeeDetails> list = query.list();
		if(CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}
	
	public ERPEmployeeDetails getEmployeeByRegId(String regId, Session session) {
		Query query = session.createQuery("from ERPEmployeeDetails where regId=:regId AND status!=:deleted");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setString("regId", regId);
		List<ERPEmployeeDetails> list = query.list();
		if(CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

}

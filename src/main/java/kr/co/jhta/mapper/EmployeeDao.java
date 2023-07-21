package kr.co.jhta.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import kr.co.jhta.vo.Employee;

@Mapper
public interface EmployeeDao {

	List<Employee> getAllEmployees();
	List<Employee> getEmployeesByJobId(String jobId);
	List<Employee> getEmployeeByDeptId(int deptId);
	void insertEmployee(Employee employee);
	
	int getTotalRows(Map<String, Object> param);
	List<Employee> getEmployees(Map<String, Object> param);
	Employee getEmployeeById(int empId);
	Employee getEmployeeByEmail(String email);
	
	
}

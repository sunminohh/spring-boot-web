package kr.co.jhta.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import kr.co.jhta.dto.Pagination;
import kr.co.jhta.mapper.DepartmentDao;
import kr.co.jhta.mapper.EmployeeDao;
import kr.co.jhta.mapper.EmployeeFileDao;
import kr.co.jhta.mapper.JobDao;
import kr.co.jhta.vo.Department;
import kr.co.jhta.vo.Employee;
import kr.co.jhta.vo.EmployeeFile;
import kr.co.jhta.vo.Job;
import kr.co.jhta.web.form.AddEmployeeFileForm;
import kr.co.jhta.web.form.AddEmployeeForm;
import kr.co.jhta.web.form.AddJobForm;
import kr.co.jhta.web.model.EmployeeList;

/**
 * 부서관리, 직원관리, 직종관리 관련 업무로직이 구현된 클래스이다.
 * @author jhta
 *
 */
@Service
public class HrService {
	
	@Value("${hr.employee.xls.save-directory}")
	private String directory;

	@Autowired
	private DepartmentDao departmentDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private EmployeeFileDao employeeFileDao;
	
	@Autowired
	private JobDao jobDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * 모든 부서목록을 반환한다.
	 * @return 부서목록
	 */
	public List<Department> getAllDepartments() {
		return departmentDao.getDepartments();
	}
	
	
	/**
	 * 지정된 부서아이디에 해당하는 부서정보를 반환한다.
	 * @param deptId 부서아이디
	 * @return 부서정보
	 */
	public Department getDepartment(int deptId) {
		return departmentDao.getdepartmentById(deptId);
	}
	
	/**
	 * 모든 직종목록을 반환한다.
	 * @return 직종목록
	 */
	public List<Job> getAllJobs() {
		return jobDao.getJobs();
	}
	
	public List<Employee> getAllEmployees() {
		return employeeDao.getAllEmployees();
	}
	
	
	/**
	 * 지정된 직종아이디에 종사하는 직원목록을 반환한다.
	 * @param jobId 직종아이디
	 * @return 해당 직종에 종사하는 직원목록 
	 */
	public List<Employee> getEmployeesByJobId(String jobId) {
		return employeeDao.getEmployeesByJobId(jobId);
	}
	
	/**
	 *  지정된 부서아이디의 부서에 소속된 사원목록을 반환한다.
	 * @param deptId 부서 아이디
	 * @return 해당 부서에 소속된 직원목록
	 */
	public List<Employee> getEmployeeByDeptId(int deptId) {
		return employeeDao.getEmployeeByDeptId(deptId);
	}
	

	/**
	 * 폼입력값을 포함하는 Form객체를 전달받아서 신규 직종정보를 추가한다.
	 * @param form 신규 직종저보가 포함된 Form 객체
	 */
	public void createJob(AddJobForm form) {
		Job job = new Job();
		BeanUtils.copyProperties(form, job);
		
		jobDao.insertJob(job);
	}

	/**
	 * 폼입력값을 포함하는 Form객체를 전달받아서 신규 직원정보를 추가한다.
	 * @param form 신규 직원정보다 포함된 Form객체
	 */
	public void createEmployee(AddEmployeeForm form) {
		Employee employee= new Employee();
		// jobId, departmentId, managerId를 제외한 다른 프로퍼티값이 전부 form에서 employees로 복사됨
		BeanUtils.copyProperties(form, employee);
		
		// 비밀번호를 암호화해서 저장시키기
		String encryptedPassword = passwordEncoder.encode(form.getPassword());
		employee.setEncryptedPassword(encryptedPassword); 
		
		Job job = new Job(form.getJobId());
		employee.setJob(job);
		
		if (form.getDepartmentId() != null) {
			Department dept = new Department(form.getDepartmentId());
			employee.setDepartment(dept);
		}
		
		if (form.getManagerId() != null) {
			Employee manager = new Employee(form.getManagerId());
			employee.setManager(manager);
		}
		
		
		employeeDao.insertEmployee(employee);
	}
	
	/**
	 * 직원 검색에 필요한 파라미터정보를 전달받아서 직원목록을 조회한다.
	 * <p> 
	 * Map객체는 "sort", "rows", "page", "opt", "keyword" 정보를 포함하고 있다.
	 * 
	 * @param param 요청파라미터 정보를 포함하는 Map객체 
	 * @return 페이징, 직원목록 정보가 포함된 EmployeeList 객체  
	 */
	public EmployeeList getEmployees(Map<String, Object> param) {
		
		int totalRows = employeeDao.getTotalRows(param);
		
		int page = (int) param.get("page");
		int rows = (int) param.get("rows");
		Pagination pagination = new Pagination(rows, page, totalRows);
		
		int begin = pagination.getBegin();
		int end = pagination.getEnd();
		param.put("begin", begin);
		param.put("end", end);
		
		List<Employee> employees = employeeDao.getEmployees(param);
		EmployeeList result = new EmployeeList();
		
		result.setPagination(pagination);
		result.setEmployees(employees);
		
		return result;
	}
	
	/**
	 * 직원아이디를 전달받아서 직원상세정보를 반환한다.
	 * @param empId 직원아이디
	 * @return 직원상세정보가 포함된 Employee객체
	 */
	public Employee getEmployee(int empId) {
		return employeeDao.getEmployeeById(empId);
	}
	
	/**
	 * 일괄등록폼에서 전달한 정보를 저장하는 폼객체를 전달받아서 정보를 테이블에 저장하고,
	 * 첨부파일도 지정된 폴더에 저장한다.
	 * @param form
	 */
	public void createEmployeeFile(AddEmployeeFileForm form) throws Exception {
		
		// 폼 입력값 조회하기(제목, 업로드된 첨부파일정보)
		String title = form.getTitle();
		MultipartFile xlsfile = form.getXlsfile();
		
		// 첨부파일 업로드 여부 체크하기
		if (!xlsfile.isEmpty()) {
			// 파일명 조회하기
			String filename = xlsfile.getOriginalFilename();
			
			// EmployeeFile 객체를 생서해서 제목, 첨부파일명을 저장해서 테이블에 저장시킨다.
			EmployeeFile employeeFile = new EmployeeFile();
			employeeFile.setTitle(title);
			employeeFile.setName(filename);
			
			employeeFileDao.insertEmployeeFile(employeeFile);
			
			// 첨부파일을 지정된 폴더에 저장시키기
			File file = new File(directory, filename);
			xlsfile.transferTo(file);
		}
	}
	
	/**
	 * 모든 직원등록파일을 반환한다.
	 * @return 직원등록파일 목록 
	 */
	public List<EmployeeFile> getAllEmployeeFiles() {
		return employeeFileDao.getEmployeeFiles();
	}
	
	public void addEmployees(int fileId) throws Exception {
		EmployeeFile employeeFile = employeeFileDao.getEmployeeFileById(fileId);
		String filename = employeeFile.getName();
		File file = new File(directory, filename);
		
		// 지정된 파일이 가르키는 엑셀파일을 표현하는 Workbook 객체 생성
		Workbook workbook = new XSSFWorkbook(file);
		
		// 엑셀파일에서 데이터가 표현된 시트를 표현하는 객체 획득하기
		Sheet sheet = workbook.getSheet("Sheet1"); 
		
		for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			
			String firstName = row.getCell(0).getStringCellValue();
			String lastName = row.getCell(1).getStringCellValue();
			String email = row.getCell(2).getStringCellValue();
			String phoneNumber = row.getCell(3).getStringCellValue();
			Date hireDate = row.getCell(4).getDateCellValue();
			String jobId = row.getCell(5).getStringCellValue();
			double salary = row.getCell(6).getNumericCellValue();
			String commCellValue = row.getCell(7).getStringCellValue();
			Double commissionPct =  StringUtils.hasText(commCellValue) ? Double.parseDouble(commCellValue) : null;
			int managerId = (int) (row.getCell(8).getNumericCellValue());
			int departmentId = (int) (row.getCell(9).getNumericCellValue());
			
			Employee employee = new Employee();
			employee.setFirstName(firstName);
			employee.setLastName(lastName);
			employee.setEmail(email);
			employee.setPhoneNumber(phoneNumber);
			employee.setHireDate(hireDate);
			employee.setJob(new Job(jobId));
			employee.setSalary(salary);
			employee.setCommissionPct(commissionPct);
			employee.setManager(new Employee(managerId));
			employee.setDepartment(new Department(departmentId));
			
			employeeDao.insertEmployee(employee);
		}
		
		workbook.close();
		
	}
	
	public EmployeeFile getEmployeeFile(int fileId) {
		return employeeFileDao.getEmployeeFileById(fileId); 
	}
	
	
	
}











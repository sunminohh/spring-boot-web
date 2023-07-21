package kr.co.jhta.web.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddEmployeeForm {

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phoneNumber;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date hireDate;
	private Integer departmentId;
	private String jobId;
	private Integer managerId;
	private Double salary;
	private Double commissionPct;
}

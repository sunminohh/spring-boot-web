package kr.co.jhta.vo;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("Department")
public class Department {

	private int id;
	private String name;
	private Employee manager;
	private Integer loc;
	
	public Department() {}
	
	public Department(int id) {
		this.id = id;
	}
}

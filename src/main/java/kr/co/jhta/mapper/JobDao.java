package kr.co.jhta.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.jhta.vo.Job;

@Mapper
public interface JobDao {

	List<Job> getJobs();
	void insertJob(Job job);
}

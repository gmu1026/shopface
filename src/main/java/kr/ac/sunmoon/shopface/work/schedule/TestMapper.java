package kr.ac.sunmoon.shopface.work.schedule;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
	public List<Test> select();
}

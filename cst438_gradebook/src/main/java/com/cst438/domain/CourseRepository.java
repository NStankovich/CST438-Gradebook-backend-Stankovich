package com.cst438.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends CrudRepository <Course, Integer> {
	
	public Course findById(int id);
	
	@Query("select c from Course c where c.instructor=:email")
	List<Course> findAllByEmail(@Param("email") String email);

}

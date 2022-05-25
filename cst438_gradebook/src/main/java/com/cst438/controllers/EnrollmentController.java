package com.cst438.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@RestController
public class EnrollmentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
		Enrollment enrollment = new Enrollment();
		enrollment.setCourse(courseRepository.findById(enrollmentDTO.course_id));
		enrollment.setStudentEmail(enrollmentDTO.studentEmail);
		enrollment.setStudentName(enrollmentDTO.studentName);
		return createEnrollmentDTO(enrollmentRepository.save(enrollment));
	}
	
	private EnrollmentDTO createEnrollmentDTO(Enrollment enrollment) {
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
		enrollmentDTO.id = enrollment.getId();
		enrollmentDTO.course_id = enrollment.getCourse() == null ? null : enrollment.getCourse().getCourse_id();
		enrollmentDTO.studentEmail = enrollment.getStudentEmail();
		enrollmentDTO.studentName = enrollment.getStudentName();
		return enrollmentDTO;
	}
}
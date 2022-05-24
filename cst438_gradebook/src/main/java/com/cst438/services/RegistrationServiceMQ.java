package com.cst438.services;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;


public class RegistrationServiceMQ extends RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	// ----- configuration of message queues

	@Autowired
	Queue registrationQueue;


	// ----- end of configuration of message queue

	// receiver of messages from Registration service
	
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
		System.out.println("Recieved: " + enrollmentDTO);
		Enrollment enrollment = new Enrollment();
		enrollment.setCourse(courseRepository.findById(enrollmentDTO.course_id));
		enrollment.setStudentEmail(enrollmentDTO.studentEmail);
		enrollment.setStudentName(enrollmentDTO.studentName);
		createEnrollmentDTO(enrollmentRepository.save(enrollment));
	}

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
		System.out.println("Sending: " + courseDTO);
		this.rabbitTemplate.convertSendAndReceive("registration-queue", courseDTO);
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
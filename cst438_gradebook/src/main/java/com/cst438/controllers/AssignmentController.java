package com.cst438.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO getAssignment( @RequestParam("id") int id) {
		AssignmentDTO assignment = new AssignmentDTO(assignmentRepository.findById(id));

		if(assignment == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment #" + id + " does not exist.");
		}

		return assignment;
	}
	
	/**
	 * As an instructor for a course , I can add a new assignment for my course.  
	 * The assignment has a name and a due date.
	 */
	
	@PostMapping("/assignment")
	@Transactional
	public AssignmentDTO addAssignment( @RequestBody AssignmentDTO newAssignmentDTO) {
		Assignment assignment = new Assignment();
		assignment.setName(newAssignmentDTO.name);
		assignment.setDueDate(newAssignmentDTO.due_date);
		assignment.setCourse(courseRepository.findById(newAssignmentDTO.course_id));
		return new AssignmentDTO(assignmentRepository.save(assignment));
	}
	
	/**
	 * As an instructor, I can change the name of the assignment for my course.
	 */
	
	@PutMapping("/assignment/rename/{id}")
	@Transactional
	public AssignmentDTO renameAssignment( @PathVariable(value="id") int id, @RequestParam("name") String name) {
		Assignment assignment = assignmentRepository.findById(id);

		if(assignment == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment #" + id + " does not exist.");
		}

		assignment.setName(name);
		return new AssignmentDTO(assignmentRepository.save(assignment));
	}
	
	/**
	 * As an instructor, I can delete an assignment  
	 * for my course (only if there are no grades for the assignment).
	 */
	
	@PutMapping("/assignment/delete/{id}")
	@Transactional
	public void deleteAssignment( @PathVariable(value="id") int id) {
		Assignment assignment = assignmentRepository.findById(id);

		if(assignment == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment #" + id + " does not exist.");
		}
		
		if (assignment.getNeedsGrading()==1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignments that need grading cannot be deleted.");
		}
		else {
			assignmentRepository.delete(assignment);
		}
	}

}

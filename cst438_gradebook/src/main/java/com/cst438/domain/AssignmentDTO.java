package com.cst438.domain;

import java.sql.Date;

public class AssignmentDTO {
	public int id;
	public Date due_date;
	public String name;
	public int needs_grading;
	public int course_id;
	
	public AssignmentDTO() {
		this.id = 0;
		this.due_date = null;
		this.name = null;
		this.needs_grading = 0;
		this.course_id = 0;
	}
	
	public AssignmentDTO(Date due_date, String name) {
		this.id = 0;
		this.due_date = due_date;
		this.name = name;
		this.needs_grading = 0;
		this.course_id = 0;
	}
	
	public AssignmentDTO(Date due_date, String name, int course_id) {
		this.id = 0;
		this.due_date = due_date;
		this.name = name;
		this.needs_grading = 0;
		this.course_id = course_id;
	}
	
	public AssignmentDTO(Date due_date, String name, int course_id, int needs_grading) {
		this.id = 0;
		this.due_date = due_date;
		this.name = name;
		this.needs_grading = needs_grading;
		this.course_id = course_id;
	}
	
	public AssignmentDTO(Assignment assignment) {
		this.id = assignment.getId();
		this.due_date = assignment.getDueDate();
		this.name = assignment.getName();
		this.needs_grading = assignment.getNeedsGrading();
		this.course_id = assignment.getCourse().getCourse_id();
	}
	@Override
	public String toString() {
		return "AssignmentDTO [name=" + name + ", due_date=" + due_date + ", course_id="
				+ course_id + ", needs_grading=" + needs_grading + "]";
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		AssignmentDTO other = (AssignmentDTO) obj;
		if (id != other.id)
			return false;

		if (due_date == null) {
			if (other.due_date != null)
				return false;
		} else if (!due_date.equals(other.due_date))
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		if (needs_grading != other.needs_grading)
			return false;

		if (course_id != other.course_id)
			return false;

		return true;
	}
}

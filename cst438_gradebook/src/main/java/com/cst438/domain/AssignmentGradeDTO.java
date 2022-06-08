package com.cst438.domain;


public class AssignmentGradeDTO {
		public String name;
		public int gradeID;
		public int courseID;
		public String courseName;
		public String dueDate;
		public String score;

		public AssignmentGradeDTO() {};

		public AssignmentGradeDTO(AssignmentGrade grade) {
			this.name = grade.getAssignment().getName();
			this.gradeID = grade.getId();
			this.dueDate = grade.getAssignment().getDueDate().toString();
			this.score = grade.getScore();
			this.courseName = grade.getAssignment().getCourse().getTitle();
		}
}
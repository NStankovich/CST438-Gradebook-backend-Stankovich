package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;

import java.sql.Date;



@SpringBootTest
public class EndToEndTestAddAssignment {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment Alpha";
	public static final Date TEST_ASSIGNMENT_DATE = Date.valueOf("2021-01-01");
	public static final String TEST_ASSIGNMENT_DATE_STRING = "01012021";
	public static final int TEST_COURSE_ID = 99999;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addCourseTest() throws Exception {

//		Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(TEST_COURSE_ID);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle("Test Course");

		courseRepository.save(c);

		Assignment x = null;
        do {
      	  x = assignmentRepository.findByName(TEST_ASSIGNMENT_NAME);
            if (x != null)
                assignmentRepository.delete(x);
        } while (x != null);


		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);

		try {
			// locate input element for add assignment button
			WebElement we = driver.findElement(By.xpath("//a[@name='AddAssignment']"));
		 	we.click();
		 	Thread.sleep(SLEEP_DURATION);

		 	// enter values into fields and click submit
			we = driver.findElement(By.xpath("//input[@name='name']"));
			we.sendKeys(TEST_ASSIGNMENT_NAME);
			we = driver.findElement(By.xpath("//input[@name='due_date']"));
			we.sendKeys(TEST_ASSIGNMENT_DATE_STRING);
			we = driver.findElement(By.xpath("//input[@name='course_id']"));
			we.sendKeys(String.valueOf(TEST_COURSE_ID));
			we = driver.findElement(By.xpath("//input[@name='submit']"));
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			// check that added assignment exists
			Assignment a = assignmentRepository.findByName(TEST_ASSIGNMENT_NAME);
			assertNotNull(a, "Test Assignment Not Found"); 
			assertEquals(TEST_COURSE_ID,a.getCourse(),"Assignment found but course ID is incorrect.");
			assertTrue(TEST_ASSIGNMENT_DATE.equals(a.getDueDate()),"Assignment found but due date is incorrect.");

		} catch (Exception ex) {
			throw ex;
		} finally {

			// clean up database.
			/*Assignment a = assignmentRepository.findByName(TEST_ASSIGNMENT_NAME);
			if (a!=null) assignmentRepository.delete(a);*/

			driver.quit();
		}

	}
}

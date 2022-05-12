package com.cst438;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import com.cst438.controllers.AssignmentController;
import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ContextConfiguration(classes = {AssignmentController.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestAssignment {

	public static final int TEST_ID = 1;
	public static final Date TEST_DUE_DATE = Date.valueOf("2020-01-01");
	public static final String TEST_NAME = "Test Name";
	public static final String ASSIGNMENT_URL = "/assignment";
	public static final String RENAME_URL = "/assignment/rename";
	public static final String DELETE_URL = "/assignment/delete";
	private static Assignment assignment;
	
	@MockBean
	AssignmentRepository assignmentRepository;

	@Autowired
	private MockMvc mvc;

	@BeforeEach
	public void setUp() {
		assignment = new Assignment();
		assignment.setId(TEST_ID);
		assignment.setDueDate(TEST_DUE_DATE);
		assignment.setName(TEST_NAME);
	}
	
	@Test
	public void getExistingAssignment() throws Exception {
		given(assignmentRepository.findById(TEST_ID)).willReturn(assignment);

		MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
				.get(ASSIGNMENT_URL + "?id=" + TEST_ID)
				.accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

		AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

		assertEquals(200, response.getStatus());
		assertEquals(TEST_ID, result.id);
	}
	
	@Test
	public void getNonExistingAssignment() throws Exception {
		given(assignmentRepository.findById(TEST_ID)).willReturn(null);

		MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
				.get(ASSIGNMENT_URL + "?id=" + TEST_ID)
				.accept(MediaType.APPLICATION_JSON))
		.andReturn().getResponse();

		assertEquals(400, response.getStatus());
		verify(assignmentRepository, times(1)).findById(TEST_ID);
	}
	
	@Test
	void addNewAssignment() throws Exception {
		given(assignmentRepository.findById(TEST_ID)).willReturn(null);
		given(assignmentRepository.save(any(Assignment.class))).willReturn(assignment);

		AssignmentDTO assignmentDTO = new AssignmentDTO(TEST_DUE_DATE, TEST_NAME);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.post(ASSIGNMENT_URL).content(asJsonString(assignmentDTO))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		MockHttpServletResponse response = mvc
				.perform(builder)
				.andReturn()
				.getResponse();

		AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);
		assertEquals(200, response.getStatus());
		assertEquals(TEST_ID, result.id);
		verify(assignmentRepository).save(any(Assignment.class));
	}
	
	@Test
	void renameExistingAssignment() throws Exception {
		given(assignmentRepository.findById(TEST_ID)).willReturn(assignment);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(RENAME_URL 
				+ "/" + TEST_ID + "?name=" + TEST_NAME);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());

		verify(assignmentRepository, times(1)).findById(TEST_ID);
	}
	
	@Test
	void renameNonExistingAssignment() throws Exception {
		given(assignmentRepository.findById(TEST_ID)).willReturn(null);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(RENAME_URL 
				+ "/" + TEST_ID + "?name=" + TEST_NAME);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isBadRequest());

		verify(assignmentRepository, times(1)).findById(TEST_ID);
	}
	
	@Test
	void deleteExistingAssignmentNoGrade() throws Exception {
		given(assignmentRepository.findById(TEST_ID)).willReturn(assignment);
		given(assignment.getNeedsGrading()==0);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(
				DELETE_URL + "/" + TEST_ID);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());

		verify(assignmentRepository, times(1)).findById(TEST_ID);
	}
	
	@Test
	void deleteExistingAssignmentGrade() throws Exception {
		given(assignmentRepository.findById(TEST_ID)).willReturn(assignment);
		given(assignment.getNeedsGrading()==1);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(
				DELETE_URL + "/" + TEST_ID);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isBadRequest());

		verify(assignmentRepository, times(1)).findById(TEST_ID);
	}
	
	@Test
	void deleteNonExistingAssignment() throws Exception {
		given(assignmentRepository.findById(TEST_ID)).willReturn(null);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(
				DELETE_URL + "/" + TEST_ID);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isBadRequest());

		verify(assignmentRepository, times(1)).findById(TEST_ID);
	}

	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T fromJsonString(String str, Class<T> valueType) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

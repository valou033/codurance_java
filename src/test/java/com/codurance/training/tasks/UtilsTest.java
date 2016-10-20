package com.codurance.training.tasks;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class UtilsTest {
    @Spy
	@InjectMocks
	Utils utils;
    
    @Mock
    Map<String, List<Task>> tasks;

    @Before
    public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

    @Test
    public void getDateFromStringTest() {
        // given 
    	String dateString = "2016-01-08";
    	String pattern = "yyyy-MM-dd";
    	LocalDate dateExpected = LocalDate.of(2016, Month.JANUARY, 8);
    	
    	// when 
    	LocalDate dateResult = utils.getDateFromString(dateString, pattern);
    	
    	// then
    	Assertions.assertThat(dateResult).isEqualTo(dateExpected);
    }
    
    @Test
    public void getStringFromLocalDateTest() {
        // given getStringFromLocalDate
    	LocalDate date = LocalDate.of(2016, Month.JANUARY, 8);; 
    	String pattern = "yyyy-MM-dd";
    	String dateExpected = "2016-01-08";
    	
    	// when 
    	String dateResult = utils.getStringFromLocalDate(date, pattern);
    	
    	// then
    	Assertions.assertThat(dateResult).isEqualTo(dateExpected);
    }
}
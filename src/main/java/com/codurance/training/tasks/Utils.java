package com.codurance.training.tasks;

import java.time.LocalDate;
import java.util.regex.*;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class Utils {

	/**
	 * Determines if string doesn't contain special characters and numbers
	 * @param string
	 * @return
	 */
	public static boolean isPatternOk(String string) {
		if (string == null) {
			return true;
		} else {
			Pattern pattern = Pattern.compile("[^A-Za-z]");
	        Matcher matcher = pattern.matcher(string);
			if (matcher.find()){
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * Determines if string is numeric or not
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    Long l = Long.parseLong(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

	/**
	 * Transform a String into a LocalDate
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public static LocalDate getDateFromString(String dateString, String pattern) {
		String[] dateStringTab = dateString.split("-", 3);
		int dateYear = Integer.parseInt(dateStringTab[0]);
		Month dateMonth = Month.of(Integer.valueOf(dateStringTab[1]));

		int dateDay = Integer.parseInt(dateStringTab[2]);
		LocalDate date = LocalDate.of(dateYear, dateMonth, dateDay);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		String text = date.format(formatter);
		LocalDate parsedDate = LocalDate.parse(text, formatter);

		return parsedDate;
	}

	/**
	 * Transform a LocalDate into a String
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getStringFromLocalDate(LocalDate date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		String dateString = null;
		if (date != null) {
			dateString = date.format(formatter);
		} else {
			dateString = " ";
		}
		return dateString;
	}

}

package com.example.budgetplanning;

import android.util.Log;
import android.widget.Toast;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.GregorianCalendar;

//DateQueryHelper class is designed to help parsing date values and create date based complex queries in
//firebase database with date format yyyymmdd.
public class DateQueryHelper {

	DateQueryHelper() {

	}
	
	//Method takes date as 3 integers represenring year, month and day and returns string
	//of dd/mm/yyyy format, adding zeroes in days and month if nessecary. Returns null if
	//requested date is invalid. 
	public String dateParseToString(int year, int month, int day) {
		if (checkDateIsCorrect(year, month, day)) {
			String date = day+"/"+month+"/"+year;
			if (month <10) {
				date = day+"/0"+month+"/" + year;
			}
			if (day<10) {
				date ="0"+date;	
			}
			return date;
		}
		return null;
	}

	//Method takes date as 1 integer with yyyymmdd format (expecting it be in right format)
	//and returns string of dd/mm/yyyy format, or null if requested date is invalid.
	public String dateParseToString(int databaseDate)  {
		String inputDate = String.valueOf(databaseDate);
		if (inputDate.length()==8) {
			String day = inputDate.substring(6);
			String month = inputDate.substring(4,6);
			String year = inputDate.substring(0,4);
			if (checkDateIsCorrect(year, month, day)) {
				String date = day+"/"+month+"/"+year;
				return date;
			}
		}
		return null;
	}

	//Method takes date as string with dd/mm/yyyy format and returns a single integer date value
	//of yyyymmdd format or -1 if requested date is invalid.
	public int dateParseToDatabaseDate(String date) {
		if (date.length()==10 && date.substring(2,3).equals("/") && date.substring(5,6).equals("/")) {
			String year = date.substring(6);
			String month = date.substring(3,5);
			String day = date.substring(0,2);
			if (checkDateIsCorrect(year, month, day)) {
				return Integer.parseInt(year+month+day);
			}
		}	
		return -1;	 
	}

	//Method takes date as 3 integers representing year, month and day and returns date 
	//value as a single Integer of yyyymmdd format or -1 if requested date is invalid
	public int dateParseToDatabaseDate(int year, int month, int day) {
		if (checkDateIsCorrect(year, month, day)) {
			return Integer.parseInt(String.valueOf(year)+String.valueOf(month)+String.valueOf(day));
		}
		return -1;
	}

	//Method takes date as 3 integers representing year, month and day and returns 
	//Boolean true if date turns out to be a real date, or false if it is invalid
	public Boolean checkDateIsCorrect(int year, int month, int day) {
		if (year<0 || month < 1 || month>12) {
			return false;
		}
		Calendar calendar = new GregorianCalendar(year, month, 1);
		if (day<1 || day>31) {return false;	}
		return true;
	}

	//Method takes date as 3 strings representing year, month and day and
	//after parsing them to integers date calls main checkDateIsCorrect method.
	public Boolean checkDateIsCorrect(String year, String month, String day) {
		return (checkDateIsCorrect(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
	}

	//Method takes date as a single string of dd/mm/yyyy format and
	//after parsing it to integers date calls main checkDateIsCorrect method.
	public Boolean checkDateIsCorrect(String date) {
		if (date.length()==10 && date.substring(2,3).equals("/") && date.substring(5,6).equals("/")) {
			return (checkDateIsCorrect(Integer.parseInt(date.substring(6)), 
							Integer.parseInt(date.substring(3,5)), 
							Integer.parseInt(date.substring(0,2))));
		}	
		return false;
	}

	//Method takes date as a single integer of yyyymmdd format and
	//after parsing it to 3 integers date calls main checkDateIsCorrect method.
	public Boolean checkDateIsCorrect(int databaseDate) {
		if (String.valueOf(databaseDate).length()==8 && databaseDate > 0) {
			return (checkDateIsCorrect(Integer.parseInt(String.valueOf(databaseDate).substring(4)),
							Integer.parseInt(String.valueOf(databaseDate).substring(2,4)),
							Integer.parseInt(String.valueOf(databaseDate).substring(0,2))));

		}
		return false;
	}
}
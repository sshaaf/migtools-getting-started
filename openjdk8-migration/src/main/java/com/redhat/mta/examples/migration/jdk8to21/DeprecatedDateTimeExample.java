package com.redhat.mta.examples.migration.jdk8to21;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Legacy date/time operations using deprecated Date API.
 * Date constructors and methods deprecated in favor of java.time
 */
public class DeprecatedDateTimeExample {
    
    private SimpleDateFormat legacyFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter modernFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public Date getCurrentDate() {
        return new Date();
    }
    
    public String formatDate(Date date) {
        return legacyFormatter.format(date);
    }
    
    public Date parseDate(String dateString) throws ParseException {
        return legacyFormatter.parse(dateString);
    }
    
    public int[] getDateComponents(Date date) {
        // Using deprecated methods - these are removed/deprecated
        @SuppressWarnings("deprecation")
        int year = date.getYear() + 1900;
        @SuppressWarnings("deprecation")
        int month = date.getMonth() + 1;
        @SuppressWarnings("deprecation")
        int day = date.getDate();
        
        return new int[]{year, month, day};
    }
    
    public Date createSpecificDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // Month is 0-based in Calendar
        return calendar.getTime();
    }
    
    public Date addDaysToDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
    
    public boolean isDateBefore(Date date1, Date date2) {
        return date1.before(date2);
    }
    
    public long getDateDifference(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }
    
    // Example of modern java.time usage for comparison
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
    
    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(modernFormatter);
    }
    
    public LocalDateTime parseDateTime(String dateString) {
        return LocalDateTime.parse(dateString + "T00:00:00");
    }
    
    public LocalDateTime addDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }
}

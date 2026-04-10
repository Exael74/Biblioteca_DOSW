package edu.eci.dosw.tdd.util;

import java.time.LocalDateTime;

public class DateUtil {
    
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
    
    public static LocalDateTime getReturnDate(LocalDateTime loanDate, int daysToAdd) {
        if (loanDate == null) {
            throw new IllegalArgumentException("Loan date cannot be null");
        }
        return loanDate.plusDays(daysToAdd);
    }
}
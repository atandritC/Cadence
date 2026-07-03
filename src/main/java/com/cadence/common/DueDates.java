package com.cadence.common;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DueDates {

    private DueDates() {
    }

    public static Long remainingDays(LocalDate dueDate) {
        if (dueDate == null)
            return null;
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate); // negative if past
    }

    public static boolean isOverdue(LocalDate dueDate, boolean terminal) {
        // not overdue if there's no due date, or the work is already finished/cancelled
        return dueDate != null && !terminal && LocalDate.now().isAfter(dueDate);
    }

}
package com.shreshtha.expensetracker.model;

import java.time.LocalDate;
import java.util.Objects;

public class MonthKey {

    private final int year;
    private final int month;

    private MonthKey(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public static MonthKey current() {
        LocalDate now = LocalDate.now();
        return new MonthKey(now.getYear(), now.getMonthValue());
    }

    public static MonthKey from(int year, int month) {
        return new MonthKey(year, month);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonthKey)) return false;
        MonthKey key = (MonthKey) o;
        return year == key.year && month == key.month;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month);
    }

    @Override
    public String toString() {
        return year + "-" + month;
    }
}

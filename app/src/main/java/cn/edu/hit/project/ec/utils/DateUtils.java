package cn.edu.hit.project.ec.utils;

import android.support.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date add(@Nullable Date date, int field, int diff) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.add(field, diff);
        return calendar.getTime();
    }

    public static Date add(int field, int diff) {
        return add(null, field, diff);
    }

    /**
     * Calculate the difference between two dates of a giving field, ignoring lower fields
     */
    public static int diff(int field, Date date1, Date date2) {
        if (field == Calendar.YEAR || field == Calendar.MONTH || field == Calendar.DATE) {
            LocalDate d1 = new LocalDate(date1);
            LocalDate d2 = new LocalDate(date2);
            switch (field) {
                case Calendar.YEAR:
                    return Years.yearsBetween(d1.withDayOfYear(1), d2.withDayOfYear(1)).getYears();
                case Calendar.MONTH:
                    return Months.monthsBetween(d1.withDayOfMonth(1), d2.withDayOfMonth(1)).getMonths();
                case Calendar.DATE:
                    return Days.daysBetween(d1, d2).getDays();
            }
        } else if (field == Calendar.HOUR || field == Calendar.MINUTE) {
            DateTime dt1 = new DateTime(date1);
            DateTime dt2 = new DateTime(date2);
            switch (field) {
                case Calendar.HOUR:
                    return Hours.hoursBetween(
                            dt1.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0),
                            dt2.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
                    ).getHours();
                case Calendar.MINUTE:
                    return Minutes.minutesBetween(
                            dt1.withSecondOfMinute(0).withMillisOfSecond(0),
                            dt2.withSecondOfMinute(0).withMillisOfSecond(0)
                    ).getMinutes();
            }
        }
        return 0;
    }

    /**
     * Test if two dates are identical from a certain field and above
     */
    public static boolean equals(int field, Date date1, Date date2) {
        return diff(field, date1, date2) == 0;
    }

    /**
     * Get start datetime of a giving date under a certain scale
     */
    public static Date startOf(Date date, int scale) {
        DateTime dt = new DateTime(date);
        switch (scale) {
            case Calendar.YEAR:
                dt = dt.monthOfYear().withMinimumValue();
            case Calendar.MONTH:
                dt = dt.dayOfMonth().withMinimumValue();
            case Calendar.DATE:
                dt = dt.hourOfDay().withMinimumValue();
            case Calendar.HOUR:
                dt = dt.minuteOfHour().withMinimumValue();
            case Calendar.MINUTE:
                dt = dt.secondOfMinute().withMinimumValue();
            case Calendar.SECOND:
                dt = dt.millisOfSecond().withMinimumValue();
            default: break;
        }
        return dt.toDate();
    }

    /**
     * Get start datetime of a giving date under certain scale
     */
    public static Date endOf(Date date, int scale) {
        DateTime dt = new DateTime(date);
        switch (scale) {
            case Calendar.YEAR:
                dt = dt.monthOfYear().withMaximumValue();
            case Calendar.MONTH:
                dt = dt.dayOfMonth().withMaximumValue();
            case Calendar.DATE:
                dt = dt.hourOfDay().withMaximumValue();
            case Calendar.HOUR:
                dt = dt.minuteOfHour().withMaximumValue();
            case Calendar.MINUTE:
                dt = dt.secondOfMinute().withMaximumValue();
            case Calendar.SECOND:
                dt = dt.millisOfSecond().withMaximumValue();
            default: break;
        }
        return dt.toDate();
    }
}

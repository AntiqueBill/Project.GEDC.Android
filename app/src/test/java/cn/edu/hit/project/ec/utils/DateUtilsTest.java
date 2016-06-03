package cn.edu.hit.project.ec.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class DateUtilsTest {

    @Test
    public void testAdd() throws Exception {
        Calendar origin = Calendar.getInstance();
        origin.set(2016, 5, 31);
        Calendar result = Calendar.getInstance();
        result.setTime(DateUtils.add(origin.getTime(), Calendar.DATE, 1));
        assertEquals(result.get(Calendar.MONTH), 6);
    }

    @Test
    public void testDiff() throws Exception {
        Calendar dt1 = Calendar.getInstance();
        Calendar dt2 = Calendar.getInstance();

        dt1.set(2015, Calendar.DECEMBER, 31, 23, 59, 59);
        dt2.set(2016, Calendar.JANUARY, 1, 0, 0, 0);
        assertEquals(1, DateUtils.diff(Calendar.YEAR, dt1.getTime(), dt2.getTime()));

        dt1.set(2016, Calendar.JANUARY, 31, 23, 59, 59);
        dt2.set(2016, Calendar.FEBRUARY, 1, 0, 0, 0);
        assertEquals(1, DateUtils.diff(Calendar.MONTH, dt1.getTime(), dt2.getTime()));

        dt1.set(2016, Calendar.JANUARY, 1, 23, 59, 59);
        dt2.set(2016, Calendar.JANUARY, 2, 0, 0, 0);
        assertEquals(1, DateUtils.diff(Calendar.DATE, dt1.getTime(), dt2.getTime()));

        dt1.set(2016, Calendar.JANUARY, 1, 1, 59, 59);
        dt2.set(2016, Calendar.JANUARY, 1, 2, 0, 0);
        assertEquals(1, DateUtils.diff(Calendar.HOUR, dt1.getTime(), dt2.getTime()));

        dt1.set(2016, Calendar.JANUARY, 1, 1, 0, 59);
        dt2.set(2016, Calendar.JANUARY, 1, 1, 1, 0);
        assertEquals(1, DateUtils.diff(Calendar.MINUTE, dt1.getTime(), dt2.getTime()));
    }

    @Test
    public void testEquals() throws Exception {
        Calendar dt1 = Calendar.getInstance();
        Calendar dt2 = Calendar.getInstance();

        dt1.set(2016, Calendar.DECEMBER, 31, 23, 59, 59);
        dt2.set(2016, Calendar.JANUARY, 1, 0, 0, 0);
        assertTrue(DateUtils.equals(Calendar.YEAR, dt1.getTime(), dt2.getTime()));

        dt1.set(2016, Calendar.JANUARY, 31, 23, 59, 59);
        dt2.set(2016, Calendar.JANUARY, 1, 0, 0, 0);
        assertTrue(DateUtils.equals(Calendar.MONTH, dt1.getTime(), dt2.getTime()));

        dt1.set(2016, Calendar.JANUARY, 1, 23, 59, 59);
        dt2.set(2016, Calendar.JANUARY, 1, 0, 0, 0);
        assertTrue(DateUtils.equals(Calendar.DATE, dt1.getTime(), dt2.getTime()));

        dt1.set(2016, Calendar.JANUARY, 1, 1, 59, 59);
        dt2.set(2016, Calendar.JANUARY, 1, 1, 0, 0);
        assertTrue(DateUtils.equals(Calendar.HOUR, dt1.getTime(), dt2.getTime()));

        dt1.set(2016, Calendar.JANUARY, 1, 1, 0, 59);
        dt2.set(2016, Calendar.JANUARY, 1, 1, 0, 0);
        assertTrue(DateUtils.equals(Calendar.MINUTE, dt1.getTime(), dt2.getTime()));
    }
}
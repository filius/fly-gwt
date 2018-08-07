package ru.fly.client;/*
 * Copyright 2015 Valeriy Filatov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;

import java.util.Date;

/**
 * @author fil
 */
public class CDateJUnitTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return null;
    }

    @Test
    public void getWeek() throws Exception {
        assertEquals(1, CDate.newDate(2016, 1, 1).getWeek());
        assertEquals(46, CDate.newDate(2016, 11, 16).getWeek());
    }

    @Test
    public void getDayOfWeek() throws Exception {
        assertEquals(1, CDate.newDate(2016, 11, 14).getDayOfWeek());
        assertEquals(3, CDate.newDate(2016, 11, 16).getDayOfWeek());
        assertEquals(7, CDate.newDate(2016, 11, 20).getDayOfWeek());
    }

    @Test
    public void testMonthName() {
        CDate january = CDate.newDate(2016, 1, 1);
        assertEquals("January", january.getMonthName());
        CDate december = CDate.newDate(2016, 12, 1);
        assertEquals("December", december.getMonthName());
    }

    @Test
    public void testGetYearPeriod() {
        //один год полный, второй не полный, не хватает месяца
        assertEquals(1, CDate.newDate(2000, 2, 1).getYearPeriod(CDate.newDate(2002, 1, 1)));

        //попадание в день рождения
        assertEquals(2, CDate.newDate(2000, 2, 1).getYearPeriod(CDate.newDate(2002, 2, 1)));

        //прошел месяц после дня рождения
        assertEquals(2, CDate.newDate(2000, 2, 1).getYearPeriod(CDate.newDate(2002, 3, 1)));

        //за день до дня рождения
        assertEquals(1, CDate.newDate(2000, 2, 1).getYearPeriod(CDate.newDate(2002, 2, 1).addDay(-1)));

        //прошел день после дня рождения
        assertEquals(2, CDate.newDate(2000, 2, 1).getYearPeriod(CDate.newDate(2002, 2, 2)));

        assertEquals(2, CDate.newDate(2002, 1, 31).withMonth(2).getMonth());

        assertEquals(2003, CDate.newDate(2002, 9, 31).addMonth(4).getYear());

        assertEquals("01.10.2015#00:00:00", CDate.newDate(2016, 1, 1).clearTime().addMonth(-3).toStringFull());

        assertEquals("01.01.2015#00:00:00", CDate.newDate(2016, 1, 1).clearTime().addMonth(-12).toStringFull());
    }

    @Test
    public void testEquals() {
        Date d1 = null;
        Date d2 = null;
        assertTrue(CDate.equals(d1, d2));
        CDate cd1 = null;
        CDate cd2 = null;
        assertTrue(CDate.equals(cd1, cd2));
        assertFalse(CDate.equals(CDate.newDate(2016, 1, 1).asDate(), null));
        assertFalse(CDate.equals(null, CDate.newDate(2016, 1, 1).asDate()));
        assertTrue(CDate.equals(CDate.newDate(2016, 1, 1).asDate(), CDate.newDate(2016, 1, 1).asDate()));
    }
}

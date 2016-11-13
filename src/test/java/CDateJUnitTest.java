/*
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

import org.junit.Assert;
import org.junit.Test;
import ru.fly.shared.CDate;

import java.util.Date;

/**
 * @author fil
 */
public class CDateJUnitTest extends Assert {

    @Test
    public void testMonthName() {
        CDate january = new CDate(1, 1, 2016);
        assertEquals("January", january.getMonthName());
        CDate december = new CDate(1, 12, 2016);
        assertEquals("December", december.getMonthName());
    }

    @Test
    public void testGetYearPeriod() {
        //один год полный, второй не полный не хватает месяца
        assertEquals(1, new CDate(1, 2, 2000).getYearPeriod(new CDate(1, 1, 2002)));

        //попадание в день рождения
        assertEquals(2, new CDate(1, 2, 2000).getYearPeriod(new CDate(1, 2, 2002)));

        //прошел месяц после дня рождения
        assertEquals(2, new CDate(1, 2, 2000).getYearPeriod(new CDate(1, 3, 2002)));

        //за день до дня рождения
        assertEquals(1, new CDate(1, 2, 2000).getYearPeriod(new CDate(1, 2, 2002).addDay(-1)));

        //прошел день полсе дня рождения
        assertEquals(2, new CDate(1, 2, 2000).getYearPeriod(new CDate(2, 2, 2002)));

        assertEquals(2, new CDate(31, 1, 2002).withMonth(2).getMonth());

        assertEquals(2003, new CDate(31, 9, 2002).addMonth(4).getYear());

        assertEquals("01.10.2015#00:00:00", new CDate(1, 1, 2016).clearTime().addMonth(-3).toStringFull());

        assertEquals("01.01.2015#00:00:00", new CDate(1, 1, 2016).clearTime().addMonth(-12).toStringFull());
    }

    @Test
    public void testEquals() {
        Date d1 = null;
        Date d2 = null;
        assertTrue(CDate.equals(d1, d2));
        assertFalse(CDate.equals(new CDate(1, 1, 2016).asDate(), null));
        assertFalse(CDate.equals(null, new CDate(1, 1, 2016).asDate()));
        assertTrue(CDate.equals(new CDate(1, 1, 2016).asDate(), new CDate(1, 1, 2016).asDate()));
    }

}

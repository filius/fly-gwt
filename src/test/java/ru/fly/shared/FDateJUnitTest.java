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
package ru.fly.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fil
 */
public class FDateJUnitTest {

    @Test
    public void testGetYearPeriod() {
        //один год полный, второй не полный не хватает месяца
        assertEquals(1, new FDate(1, 2, 2000).getYearPeriod(new FDate(1, 1, 2002)));

        //попадание в день рождения
        assertEquals(2, new FDate(1, 2, 2000).getYearPeriod(new FDate(1, 2, 2002)));

        //прошел месяц после дня рождения
        assertEquals(2, new FDate(1,2,2000).getYearPeriod(new FDate(1,3,2002)));

        //за день до дня рождения
        assertEquals(1, new FDate(1,2,2000).getYearPeriod(new FDate(1,2,2002).addDay(-1)));

        //прошел день полсе дня рождения
        assertEquals(2, new FDate(1,2,2000).getYearPeriod(new FDate(2,2,2002)));

        assertEquals(2, new FDate(31, 1, 2002).setMonth1(2).getMonth());

        assertEquals(2003, new FDate(31, 9, 2002).addMonth(4).getYear());

        assertEquals("01.10.2015#00:00:00", new FDate(1, 1, 2016).clearTime().addMonth(-3).toStringFull());

        assertEquals("01.01.2015#00:00:00", new FDate(1, 1, 2016).clearTime().addMonth(-12).toStringFull());
    }

    @Test
    public void testEquals() {
        assertTrue(FDate.equals(null, null));
        assertFalse(FDate.equals(new FDate(1, 1, 2016).asDate(), null));
        assertFalse(FDate.equals(null, new FDate(1, 1, 2016).asDate()));
        assertTrue(FDate.equals(new FDate(1, 1, 2016).asDate(), new FDate(1, 1, 2016).asDate()));
    }
}

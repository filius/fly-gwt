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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: fil
 * Date: 20.08.13
 * Time: 15:40
 */
@SuppressWarnings("deprecation")
public class FDate {

    private static final Map<Integer, String> monthNames = new HashMap<Integer, String>(){{
        put(0, "Январь");
        put(1, "Февраль");
        put(2, "Март");
        put(3, "Апрель");
        put(4, "Май");
        put(5, "Июнь");
        put(6, "Июль");
        put(7, "Август");
        put(8, "Сентябрь");
        put(9, "Октябрь");
        put(10, "Ноябрь");
        put(11, "Декабрь");
    }};

    private static final long SECOND = 1000;
    private static final long MINUTE = 60000;
    private static final long HOUR = 3600000;
    private static final long DAY = 86400000;

    private static final int DAYS_IN_WEEK = 7;
    private static final int MAX_DAY_OF_WEEK = 6;
    private static final int ISO_THURSDAY = 4;

    private long time;

    public FDate(){
        this(new Date());
    }

    public FDate(Date date){
        setDate(date);
    }

    public FDate(long time){
        setTime(time);
    }

    public FDate(int day, int month, int year){
        this(new Date(year - 1900, month - 1, day));
    }

    public Date asDate(){
        return new Date(time);
    }

    public long asLong(){
        return time;
    }

    public FDate setDate(Date date){
        setTime(date.getTime());
        return this;
    }

    public FDate setTime(long time){
        this.time = time;
        return this;
    }

    /**
     * = futureDate - this
     * @param futureDate - дата которая в будущем
     * @return
     */
    public long getMillisecondPeriod(FDate futureDate){
        return futureDate.time - time;
    }

    /**
     * = futureDate - this
     * @param futureDate - дата которая в будущем
     * @return
     */
    public long getMinutePeriod(FDate futureDate){
        return getMillisecondPeriod(futureDate) / MINUTE;
    }

    /**
     * = futureDate - this
     * @param futureDate - дата которая в будущем
     * @return
     */
    public long getDayPeriod(FDate futureDate){
        return getMillisecondPeriod(futureDate) / DAY;
    }

    /**
     * = futureDate - this
     * @param futureDate - дата которая в будущем
     * @return
     */
    public int getYearPeriod(FDate futureDate){
        int year = futureDate.getYear() - getYear() - 1;
        int pastMonth = getMonth();
        int futureMonth = futureDate.getMonth();
        if(futureMonth > pastMonth){
            year++;
        }else if(pastMonth == futureMonth){
            int pastDay = getDay();
            int futureDay = futureDate.getDay();
            if(futureDay >= pastDay){
                year++;
            }
        }
        return year;
    }

    public boolean before(FDate d){
        return time < d.time;
    }

    public boolean after(FDate d){
        return time > d.time;
    }

    public FDate clone(){
        return new FDate(time);
    }

    public FDate setDay(int day){
        Date d = new Date(time);
        d.setDate(day);
        time = d.getTime();
        return this;
    }

    public FDate setMonth(int month){
        Date d = new Date(time);
        d.setMonth(month);
        time = d.getTime();
        return this;
    }

    public FDate setYear(int year){
        Date d = new Date(time);
        d.setYear(year);
        time = d.getTime();
        return this;
    }

    public FDate addMinute(long minute){
        time += (MINUTE * minute);
        return this;
    }

    public FDate addDay(long day){
        time += (DAY * day);
        return this;
    }

    public FDate addMonth(int month){
        Date d = new Date(time);
        int years = 0;
        int newm = d.getMonth() + month;
        if(newm > 11){
            while(newm > 11){
                newm -= 12;
                years++;
            }
        }
        if(newm < -11){
            while(newm < -11){
                newm += 12;
                years--;
            }
        }
        if(years != 0){
            d.setYear(d.getYear()+years);
        }
        d.setMonth(newm);
        time = d.getTime();
        return this;
    }

    public FDate addYear(int year){
        Date d = new Date(time);
        d.setYear(d.getYear()+year);
        time = d.getTime();
        return this;
    }

    public int getYear(){
        return new Date(time).getYear()+1900;
    }

    public int getWeek() {
        // ISO week day (Mon=1 to Sun=7)
        FDate d = clone();
        final int dayOfWeek = 1 + (d.asDate().getDay() + MAX_DAY_OF_WEEK) % DAYS_IN_WEEK;
        d.addDay(ISO_THURSDAY - dayOfWeek);
        final int year = d.asDate().getYear();
        final FDate jan1 = new FDate(new Date(year, 0, 1));
        return 1 + (int)jan1.getDayPeriod(d) / DAYS_IN_WEEK;
    }

    /**
     * 1 - 31
     * @return
     */
    public int getDay(){
        return new Date(time).getDate();
    }

    /**
     * 1 - понедельник
     * 2 - воскресенье
     * @return
     */
    public int getDayOfWeek(){
        int day = new Date(time).getDay();
        return day == 0 ? 7 : day;
    }

    public int getDaysInMonth(){
        FDate d1 = clone().clearTime().addDay(-getDay()+1);
        FDate d2 = d1.clone().addMonth(1);
        return (int)d1.getDayPeriod(d2);
    }

    /**
     * 1 - январь
     * 12 - декабрь
     * @return
     */
    public int getMonth(){
        return new Date(time).getMonth()+1;
    }

    /**
     * "01" - январь
     * "12" - декабрь
     * @return
     */
    public String getMonthString(){
        int m = new Date(time).getMonth()+1;
        return m<10?("0"+m):String.valueOf(m);
    }

    public String getMonthName(){
        return monthNames.get(new Date(time).getMonth());
    }

    public FDate clearTime(){
        Date d = new Date(time);
        time -= ( d.getHours() * HOUR + d.getMinutes() * MINUTE + d.getSeconds() * SECOND );
        time = time / 1000 * 1000; // обнуление миллисекунд
        return this;
    }

    @Override
    public String toString() {
        return getDay()+"."+getMonthString()+"."+getYear();
    }
}

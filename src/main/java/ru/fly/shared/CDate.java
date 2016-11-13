package ru.fly.shared;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

import java.util.Date;

/**
 * date wrapper in immutable style.
 *
 * @author fil
 */
@SuppressWarnings("deprecation")
public class CDate {

    private static final long SECOND = 1000;
    private static final long MINUTE = 60000;
    private static final long HOUR = 3600000;
    private static final long DAY = 86400000;

    private static final int SUBTRACT_YEAR = 1900;
    private static final int MONTH_IN_YEAR = 12;
    private static final int DAYS_IN_WEEK = 7;
    private static final int MAX_DAY_OF_WEEK = 6;
    private static final int ISO_THURSDAY = 4;
    private static DateTimeFormat monthNameFormat = new DateTimeFormat("MMMM", new DefaultDateTimeFormatInfo()) {
    };

    private static String[] monthNames = new String[MONTH_IN_YEAR];

    static {
        for (int i = 0; i < MONTH_IN_YEAR; i++) {
            monthNames[i] = monthNameFormat.format(new Date(0, i, 1));
        }
    }

    /**
     * static wrapper for current date-time.
     *
     * @return - CDate for current date-time
     */
    public static CDate now() {
        return new CDate();
    }

    /**
     * check equals of two objects.
     *
     * @param o1 - first Date object
     * @param o2 - second Date object
     * @return - boolean, TRUE if equals
     */
    public static boolean equals(Date o1, Date o2) {
        return (o1 == null && o2 == null) || (o1 != null && o2 != null && o1.getTime() == o2.getTime());
    }

    /**
     * check equals of two objects.
     *
     * @param o1 - first CDate object
     * @param o2 - second CDate object
     * @return - boolean, TRUE if equals
     */
    public static boolean equals(CDate o1, CDate o2) {
        return (o1 == null && o2 == null) || (o1 != null && o2 != null && o1.asLong() == o2.asLong());
    }

    private long time;

    /**
     * default.
     */
    public CDate() {
        this(new Date().getTime());
    }

    /**
     * .
     *
     * @param date - Date value
     */
    public CDate(Date date) {
        this(date == null ? new Date().getTime() : date.getTime());
    }

    /**
     * .
     *
     * @param time - long representation of time
     */
    public CDate(long time) {
        this.time = time;
    }

    /**
     * .
     *
     * @param day   - day value [1:31]
     * @param month - month value [1:12]
     * @param year  - year value (without subtract 1900)
     */
    public CDate(int day, int month, int year) {
        this(new Date(year - SUBTRACT_YEAR, month - 1, day));
    }

    /**
     * convert object os Date type.
     *
     * @return - Date
     */
    public Date asDate() {
        return new Date(time);
    }

    /**
     * return time as long value.
     *
     * @return - long value of date-time
     */
    public long asLong() {
        return time;
    }

    /**
     * clone object.
     *
     * @return - CDate clone
     */
    public CDate clone() {
        return new CDate(time);
    }

    /**
     * check current date before another.
     *
     * @param d - another date
     * @return - boolean, TRUE if before
     */
    public boolean before(CDate d) {
        return time < d.time;
    }

    /**
     * check current date after another.
     *
     * @param d - another date
     * @return - boolean, TRUE if after
     */
    public boolean after(CDate d) {
        return time > d.time;
    }

    /**
     * clear time part in date-time object.
     *
     * @return - CDate with zero time
     */
    public CDate clearTime() {
        long t = time;
        t -= (getHours() * HOUR + getMinutes() * MINUTE + getSeconds() * SECOND);
        // обнуление миллисекунд
        return new CDate(t / SECOND * SECOND);
    }

    public int getYear() {
        return new Date(time).getYear() + SUBTRACT_YEAR;
    }

    /**
     * set year of date.
     *
     * @param year - year value
     * @return - CDate
     */
    public CDate withYear(int year) {
        Date d = new Date(time);
        d.setYear(year - SUBTRACT_YEAR);
        return new CDate(d);
    }

    /**
     * add year.
     *
     * @param year - year value
     * @return - CDate
     */
    public CDate addYear(int year) {
        Date d = new Date(time);
        d.setYear(d.getYear() + year);
        return new CDate(d);
    }

    /**
     * return month of date.
     * 1 - january
     * 12 - december
     *
     * @return - month number [1:12]
     */
    public int getMonth() {
        return new Date(time).getMonth() + 1;
    }

    /**
     * set month.
     * 1 - january
     * 12 - december
     *
     * @param month - month value
     * @return - CDate
     */
    public CDate withMonth(int month) {
        Date d = new Date(time);
        int oldMonth = d.getMonth() + 1;
        int delta = month - oldMonth;
        if (delta != 0) {
            d.setMonth(month - 1);
            int newMonth = d.getMonth() + 1;
            // after month switch from 31.01.2002 to day 28.02.2002 for example, date make switch to 03.03.2002, fix it
            if (newMonth != oldMonth + delta) {
                d.setDate(0);
            }
        }
        return new CDate(d);
    }

    /**
     * add month.
     *
     * @param month - month value, also negative values allowed
     * @return - CDate
     */
    public CDate addMonth(int month) {
        int years = 0;
        int newMonth = getMonth() + month;
        if (newMonth > MONTH_IN_YEAR) {
            while (newMonth > MONTH_IN_YEAR) {
                newMonth -= MONTH_IN_YEAR;
                years++;
            }
        }
        if (newMonth < 1) {
            while (newMonth < 1) {
                newMonth += MONTH_IN_YEAR;
                years--;
            }
        }
        return clone().addYear(years).withMonth(newMonth);
    }

    /**
     * return week number of month.
     *
     * @return - int week number
     */
    public int getWeek() {
        // ISO week day (Mon=1 to Sun=7)
        CDate d = clone();
        final int dayOfWeek = 1 + (d.asDate().getDay() + MAX_DAY_OF_WEEK) % DAYS_IN_WEEK;
        d.addDay(ISO_THURSDAY - dayOfWeek);
        final int year = d.asDate().getYear();
        final CDate jan1 = new CDate(new Date(year, 0, 1));
        return 1 + (int) jan1.getDayPeriod(d) / DAYS_IN_WEEK;
    }

    /**
     * return day of week.
     * 1 - monday
     * 7 - sunday
     *
     * @return - int
     */
    public int getDayOfWeek() {
        int day = getDay();
        return day == 0 ? DAYS_IN_WEEK : day;
    }

    /**
     * return day of month.
     *
     * @return - int [1:31]
     */
    public int getDay() {
        return new Date(time).getDate();
    }

    /**
     * set day of month.
     *
     * @param day - day value
     * @return - CDate
     */
    public CDate withDay(int day) {
        Date d = new Date(time);
        d.setDate(day);
        return new CDate(d);
    }

    /**
     * add days.
     *
     * @param day - day value, also negative values allowed
     * @return - CDate
     */
    public CDate addDay(long day) {
        return new CDate(time + (DAY * day));
    }

    /**
     * return hours.
     *
     * @return - int
     */
    public int getHours() {
        return new Date(time).getHours();
    }

    /**
     * set hours.
     *
     * @param hours - hours value
     * @return - CDate
     */
    public CDate withHours(int hours) {
        Date d = new Date(time);
        d.setHours(hours);
        return new CDate(d);
    }

    /**
     * add hours.
     *
     * @param hour - hours value, also negative values allowed
     * @return - CDate
     */
    public CDate addHour(long hour) {
        return new CDate(time + (HOUR * hour));
    }

    /**
     * return minutes.
     *
     * @return - int
     */
    public int getMinutes() {
        return new Date(time).getMinutes();
    }

    /**
     * set minutes.
     *
     * @param minutes - minutes value
     * @return - CDate
     */
    public CDate withMinutes(int minutes) {
        Date d = new Date(time);
        d.setMinutes(minutes);
        return new CDate(d);
    }

    /**
     * add minutes.
     *
     * @param minute - minutes value, also negative values allowed
     * @return - CDate
     */
    public CDate addMinute(long minute) {
        return new CDate(time + (MINUTE * minute));
    }

    /**
     * return seconds.
     *
     * @return - int
     */
    public int getSeconds() {
        return new Date(time).getSeconds();
    }

    /**
     * set seconds.
     *
     * @param seconds - second value
     * @return - CDate
     */
    public CDate withSeconds(int seconds) {
        Date d = new Date(time);
        d.setSeconds(seconds);
        return new CDate(d);
    }

    /**
     * add seconds.
     *
     * @param sec - seconds value, also negative values allowed
     * @return - CDate
     */
    public CDate addSeconds(long sec) {
        return new CDate(time + (SECOND * sec));
    }

    /**
     * return delta of milliseconds from two dates.
     *
     * @param futureDate - usually date in future
     * @return - long delta
     */
    public long getMillisecondPeriod(CDate futureDate) {
        return futureDate.time - time;
    }

    /**
     * return delta of minutes from two dates.
     *
     * @param futureDate - usually date in future
     * @return - long delta
     */
    public long getMinutePeriod(CDate futureDate) {
        return getMillisecondPeriod(futureDate) / MINUTE;
    }

    /**
     * return delta of days from two dates.
     *
     * @param futureDate - usually date in future
     * @return - long delta
     */
    public long getDayPeriod(CDate futureDate) {
        return getMillisecondPeriod(futureDate) / DAY;
    }

    /**
     * return delta of years from two dates.
     *
     * @param futureDate - usually date in future
     * @return - int delta
     */
    public int getYearPeriod(CDate futureDate) {
        int year = futureDate.getYear() - getYear() - 1;
        int pastMonth = getMonth();
        int futureMonth = futureDate.getMonth();
        if (futureMonth > pastMonth) {
            year++;
        } else if (pastMonth == futureMonth) {
            if (futureDate.getDay() >= getDay()) {
                year++;
            }
        }
        return year;
    }

    /**
     * return delta of months from two dates.
     *
     * @param futureDate - usually date in future
     * @return - int delta
     */
    public int getMonthPeriod(CDate futureDate) {
        int months = (futureDate.getYear() - getYear()) * MONTH_IN_YEAR;
        months += futureDate.getMonth() - getMonth();
        if (futureDate.getDay() >= getDay()) {
            months++;
        }
        return months;
    }

    /**
     * return days number in month.
     *
     * @return - int
     */
    public int getDaysInMonth() {
        CDate d1 = clearTime().addDay(-getDay() + 1);
        CDate d2 = d1.addMonth(1);
        return (int) d1.getDayPeriod(d2);
    }

    /**
     * return string representation of day.
     *
     * @return - String [01:31]
     */
    public String getDayString() {
        return supl(getDay());
    }

    /**
     * return string representation of month.
     * january - "01"
     * december - "12"
     *
     * @return - String
     */
    public String getMonthString() {
        return supl(getMonth());
    }

    /**
     * return string name of month .
     *
     * @return - String
     */
    public String getMonthName() {
        return monthNames[getMonth() - 1];
    }

    @Override
    public String toString() {
        return getDayString() + "." + getMonthString() + "." + getYear();
    }

    /**
     * longer version of toString().
     *
     * @return - String
     */
    public String toStringFull() {
        return getDayString() + "." + getMonthString() + "." + getYear()
                + "#" + supl(getHours()) + ":" + supl(getMinutes()) + ":" + supl(getSeconds());
    }

    // ------------ privates -----------

    /**
     * fill string with "0" if length shorter than 2.
     *
     * @param obj - target object
     * @return - String
     */
    private String supl(Object obj) {
        String ret = obj.toString();
        if (ret.length() < 2) {
            return "0" + ret;
        } else {
            return ret;
        }
    }
}

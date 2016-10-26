package com.ldy.werty.util;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    private static final String TAG = TimeUtil.class.getSimpleName();

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public final static SimpleDateFormat dateFormater4 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    private static final long MINUTE_AGO = 60 * 1000; // 1分钟以前

    private static final long HOUR_AGO = 60 * 60 * 1000; // 1小时以前

    private static final long DAY_AGO = 24 * 60 * 60 * 1000; // 24小时以前

    private static final long FIVE_DAY_AGO = 5 * 24 * 60 * 60 * 1000; // 5天以前
    private static final long TWO_DAY_AGO = 2 * 24 * 60 * 60 * 1000;

    /**
     * 返回多少时间 多少秒前 多少小时前
     *
     * @param time
     * @return
     */
    public static String formatTimestamp(long time) {
        long currentTime = System.currentTimeMillis();
        long serverTime = time * 1000;
        long intervalTime = currentTime - serverTime;
        if (intervalTime < MINUTE_AGO) {
            long l = (intervalTime % (1000 * 60)) / 1000;
            if (l > 0)
                return l + "秒前";
            else
                return "1秒前";
        } else if (intervalTime < HOUR_AGO) {
            return (intervalTime % (1000 * 60 * 60)) / (1000 * 60) + "分钟前";
        } else if (intervalTime < DAY_AGO) {
            return (intervalTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60) + "小时前";
        } else if (intervalTime < FIVE_DAY_AGO) {
            return intervalTime / (1000 * 60 * 60 * 24) + "天前";
        } else {
            return FORMAT.format(new Date(serverTime));
        }
    }

    /**
     * 私信返回多少天
     *
     * @param time
     * @return
     */
    public static String formatTimestamp2(long time) {
        long currentTime = System.currentTimeMillis();
        long serverTime = time * 1000;
        long intervalTime = currentTime - serverTime;
        if (intervalTime < MINUTE_AGO) {
            long l = (intervalTime % (1000 * 60)) / 1000;
            if (l > 0)
                return l + "秒前";
            else
                return "1秒前";
        } else if (intervalTime < HOUR_AGO) {
            return (intervalTime % (1000 * 60 * 60)) / (1000 * 60) + "分钟前";
        } else if (intervalTime < DAY_AGO) {
            return (intervalTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60) + "小时前";
        } else if (intervalTime < TWO_DAY_AGO) {
            return intervalTime / (1000 * 60 * 60 * 24) + "天前";
        } else {
            return FORMAT.format(new Date(serverTime));
        }
    }

    /**
     * 计算两个时间的的时间差
     *
     * @param timestamp1
     * @param timestamp2
     * @return xx小时
     */
    public static String getTimeHourDiff(long timestamp1, long timestamp2) {

        String dataStr = "";
        long hours;
        long minutes;

        long diffMinutes = Math.abs(timestamp1 / MINUTE_AGO - timestamp2 / MINUTE_AGO) * MINUTE_AGO;//这样得到的差值是分钟级别
        long diffSeconds = Math.abs(timestamp1 / 1000 - timestamp2 / 1000) * 1000;//这样得到的差值是 秒 级别

        hours = diffMinutes / HOUR_AGO;
        minutes = (diffMinutes - hours * HOUR_AGO) / MINUTE_AGO;

        if (1 <= hours) {
            if (0 == minutes) {
                dataStr = hours + "小时";
            } else {
                dataStr = hours + "小时" + minutes + "分";
            }
        } else {

            minutes = diffMinutes / MINUTE_AGO;
            if (1 <= minutes) {
                dataStr = minutes + "分钟";
            } else {
                long seconds = diffSeconds / 1000;
                dataStr = seconds + "秒";
            }
        }
        return dataStr;
    }

    /**
     * 返回当前是 周几
     *
     * @param timestamp
     * @return
     */
    public static String getWeek(long timestamp) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(timestamp);
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 格式化日期
     *
     * @param template  ：格式。"yyyy-MM-dd HH:mm"等
     * @param timestamp ：时间戳。1970年1月1日后的毫秒数
     * @return
     */
    public static String format(String template, long timestamp) {
        SimpleDateFormat formate = new SimpleDateFormat(template, Locale.CHINA);
        return formate.format(new Date(timestamp));
    }

    public static String format1(long timestamp) {
        SimpleDateFormat formate = new SimpleDateFormat("y年M月d日", Locale.CHINA);
        return formate.format(new Date(timestamp));
    }

    public static String format2(long timestamp) {
        SimpleDateFormat formate = new SimpleDateFormat("y年M月d日", Locale.CHINA);
        LogUtil.d(timestamp + "|" + timestamp * 1000 + "|" + getCurrentTime());
        return formate.format(new Date(timestamp * 1000));
    }

    public static String format3(long timestamp) {
        SimpleDateFormat formate = new SimpleDateFormat("M月d日", Locale.CHINA);
        LogUtil.d(timestamp + "|" + getCurrentTime());
        return formate.format(new Date(timestamp));
    }

    public static String format4(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        LogUtil.d(timestamp + "|" + getCurrentTime());
        return format.format(new Date(timestamp));
    }

    /**
     * 通过字符串 获取Data时间
     *
     * @param timeStr
     * @return
     */
    public static Date parse(String timeStr) {
        try {
            return FORMAT.parse(timeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String timestempToStringMore(String time) {
        return dateFormater.format(new Date(Util.parseLong(time) * 1000));
    }

    public static String timestempToStringMore2(String time) {
        return dateFormater.format(new Date(Util.parseLong(time)));
    }

    /**
     * 毫秒数转时间字符(2011-01-01格式)
     *
     * @param time
     * @return
     */
    public static String timestempToString(long time) {
        try {
            return FORMAT.format(new Date(time));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 是否为生日
     *
     * @param remoteTime
     * @param localTime
     * @return
     */
    public static boolean isBirthDay(long remoteTime, long localTime) {
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd", Locale.getDefault());
        String r_time = sf.format(remoteTime);
        String l_time = sf.format(localTime);
        return l_time.equals(r_time);
    }

    /**
     * 比较是否是同一天
     *
     * @return true相同的一天 false不同的一天
     */
    public static boolean isSameDay(long time) {
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String sp_time = sf.format(time);
        String current_time = sf.format(timeMillis);

        return sp_time.equals(current_time);
    }

    public static boolean isSameDay(String remoteTime, long localTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String sp_time = sf.format(localTime);
        return sp_time.equals(remoteTime);
    }

    public static boolean isSameDay(long remoteTime, long localTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String r_time = sf.format(remoteTime);
        String l_time = sf.format(localTime);
        return l_time.equals(r_time);
    }

    /**
     * 是否为同一个月
     *
     * @param remoteTime
     * @param localTime
     * @return
     */
    public static boolean isSameMonth(long remoteTime, long localTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String r_time = sf.format(remoteTime);
        String l_time = sf.format(localTime);
        return l_time.equals(r_time);
    }

    public static long stringToMillionSeconds(String time) {
        return stringToMillionSeconds(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * string时间戳 时间转化为 long
     *
     * @param time
     * @param template
     * @return 单位秒
     */
    public static long stringToMillionSeconds(String time, String template) {
        long millionSeconds = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.getDefault());
            millionSeconds = sdf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "timeToMillionSeconds2 millionSeconds[" + millionSeconds + "]");
        return millionSeconds / 1000;
    }

    /**
     * 获取当日零点的时间戳
     *
     * @param calendar
     * @return
     */
    public static long getMidnightMilliseconds(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 返回当前日期毫秒数
     *
     * @return
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取毫秒数
     *
     * @param dateString yyyy-MM-dd
     * @return long mills
     */
    public static long getTimeMills(String dateString) {
        String date[] = dateString.split("-");
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        try {
            calendar.set(Calendar.YEAR, Util.parseInt(date[0]));
            calendar.set(Calendar.MONTH, Util.parseInt(date[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Util.parseInt(date[2]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    public static boolean isLeapYear(int year) {
        return (year % 400 == 0) || (year % 100 != 0 && year % 4 == 0);
    }

    public static String millsecondsToStr(int seconds) {
        seconds = seconds / 1000;
        String result = "";
        int hour = 0, min = 0, second = 0;
        hour = seconds / 3600;
        min = (seconds - hour * 3600) / 60;
        second = seconds - hour * 3600 - min * 60;
        if (hour < 10) {
            result += "0" + hour + ":";
        } else {
            result += hour + ":";
        }
        if (min < 10) {
            result += "0" + min + ":";
        } else {
            result += min + ":";
        }
        if (second < 10) {
            result += "0" + second;
        } else {
            result += second;
        }
        return result;
    }

    /**
     * @param time
     * @return eg 周一 下午
     */
    public static String currentTimePeriod(long time) {
        return dayForWeek(time * 1000) + " " + hourForDay(time * 1000);
    }

    private static String dayForWeek(long time) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(time);
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 删除日期时分秒
     *
     * @param date
     * @return
     */
    public static Date changDate(Date date) {
        try {
            return dateFormater4.parse(dateFormater4.format(date));
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

    @NonNull
    private static String hourForDay(long time) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        switch (hour) {
            case 2:
            case 3:
                return "凌晨";
            case 4:
            case 5:
                return "清晨";
            case 6:
            case 7:
                return "早晨";
            case 8:
            case 9:
            case 10:
                return "上午";
            case 11:
            case 12:
                return "中午";
            case 13:
            case 14:
                return "午后";
            case 15:
            case 16:
            case 17:
                return "下午";
            case 18:
            case 19:
                return "傍晚";
            case 20:
            case 21:
                return "晚上";
            default:
                return "午夜";
        }
    }

    /**
     * 传入毫秒值返回对应年份
     *
     * @param time
     */
    public static String getYear(long time) {
        long serverTime = time * 1000;
        Date date = new Date(serverTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.getDefault());
        return format.format(date);
    }

    public static String getMMdd(long time) {
        long serverTime = time * 1000;
        Date date = new Date(serverTime);
        SimpleDateFormat format = new SimpleDateFormat("MM.dd", Locale.getDefault());
        return format.format(date);
    }

    /**
     * 返回带中文的日期
     */
    public static String formatNY_CH(long time) {
        long serverTime = time * 1000;
        Date date = new Date(serverTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

}

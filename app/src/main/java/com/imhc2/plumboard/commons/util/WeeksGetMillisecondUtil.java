package com.imhc2.plumboard.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class WeeksGetMillisecondUtil {
    Date s, e;

    public WeeksGetMillisecondUtil() {//이번주의 시작을 계산 ->milliseconds 로 변환
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        int iDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        Date dt = new Date();
        long unix_time = dt.getTime() / 1000 + 86400 * 7 * 0;//86400*7*i  i ==1 다음주  i==0 이번주 i ==-1 저번주
        long startDate = unix_time - iDayOfWeek * 86400;
        long endDate = unix_time;

        for (int y = iDayOfWeek; y < 6; y++) {
            endDate = endDate + 86400;
        }

        String startWeeks = sdf.format(new Date(startDate * 1000));
        String endWeeks = sdf.format(new Date(endDate * 1000));

        Timber.e("시작일 : " + startWeeks + " 종료일 : " + endWeeks);

        String startDay = startWeeks + "0000";
        String endDay = endWeeks + "2400";
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");//년-월-일 "timezone" 시 분 초 밀리세컨드
        try {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA);
            s = sdf2.parse(startDay);
            e = sdf2.parse(endDay);
            Timber.e("date =" + sdf2.format(s) + "millisecond = " + s.getTime() + "/" + e.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public Long getMondayMillisecond() {
        return s.getTime();
    }

    public Long getSundayMillisecond() {
        return e.getTime();
    }

    public int getDayOfWeek(Long date) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        if(date != null){
            cal.setTimeInMillis(date);
        }

        String strWeek = null;
        int num = 0;
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        Timber.e("nWeek = "+nWeek);

        switch (nWeek){
            case 1:
                strWeek = "일";
                num = 7;
                break;
            case 2:
                strWeek = "월";
                num = 1;
                break;
            case 3:
                strWeek = "화";
                num = 2;
                break;
            case 4:
                strWeek = "수";
                num = 3;
                break;
            case 5:
                strWeek = "목";
                num = 4;
                break;
            case 6:
                strWeek = "금";
                num = 5;
                break;
            case 7:
                strWeek = "토";
                num = 6;
                break;
        }

        return num;
    }

    static public String getDayOfWeekString() {
        Calendar cal = Calendar.getInstance();
        String strWeek = null;
        int num = 0;

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) {
            strWeek = "일";
            num = 7;
        } else if (nWeek == 2) {
            strWeek = "월";
            num = 1;
        } else if (nWeek == 3) {
            strWeek = "화";
            num = 2;
        } else if (nWeek == 4) {
            strWeek = "수";
            num = 3;
        } else if (nWeek == 5) {
            strWeek = "목";
            num = 4;
        } else if (nWeek == 6) {
            strWeek = "금";
            num = 5;
        } else if (nWeek == 7) {
            strWeek = "토";
            num = 6;
        }

        return strWeek;
    }

    public String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }
    public String formatMinutesAndSecond(int time){
        return (new SimpleDateFormat("mm:ss")).format(new Date(time));
    }

}

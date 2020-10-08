package com.imhc2.plumboard;

import android.net.Uri;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() throws Exception {
        //https://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java
        //Timber.e(System.currentTimeMillis());
        Timber.e((((1490674660 / 1000) / 60) % 60)+"");

        Timber.e(formatHoursAndMinutes((1490674660 / (1000 * 60)) % 60));

        Timber.e(convertSecondsToDate(1382865762));
        Timber.e(convertDate(1540277111791L, "yyyy-MM-dd-HH-mm"));
        Timber.e(doDayOfWeek());
        getWeeks();

//        int h = (int) ((startTimeInMillis / 1000) / 3600);
//        int m = (int) (((startTimeInMillis / 1000) / 60) % 60);
//        int s = (int) ((startTimeInMillis / 1000) % 60);

    }

    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }

    public static String convertSecondsToDate(long seconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(seconds);
        String formatted = formatter.format(date);
        return formatted;
    }


    private String doDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        String strWeek = null;

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) {
            strWeek = "일요일";
        } else if (nWeek == 2) {
            strWeek = "월요";
        } else if (nWeek == 3) {
            strWeek = "화요일";
        } else if (nWeek == 4) {
            strWeek = "수요일";
        } else if (nWeek == 5) {
            strWeek = "목요일";
        } else if (nWeek == 6) {
            strWeek = "금요일";
        } else if (nWeek == 7) {
            strWeek = "토요일";
        }

        return strWeek;
    }

    // 참고 : http://eversong0723.blogspot.com/2015/12/java.html
    public void getWeeks() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        int iDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        Date dt = new Date();
        long unix_time = dt.getTime() / 1000;//7이 일주일 뒤
        long startDate = unix_time - iDayOfWeek * 86400;
        long endDate = unix_time;

        for (int y = iDayOfWeek; y < 6; y++) {
            endDate = endDate + 86400;
        }

        String sStart = sdf.format(new Date(startDate * 1000));
        String sEnd = sdf.format(new Date(endDate * 1000));

        Timber.e("시작일 : " + sStart + " 종료일 : " + sEnd);

        String start = sStart + "0000";
        String end = sEnd + "2400";
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");//년-월-일 "timezone" 시 분 초 밀리세컨드
        try {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA);
            Date s = sdf2.parse(start);
            Date e = sdf2.parse(end);
            Timber.e("date =" + sdf2.format(s) + "millisecond = " + s.getTime() + "/" + e.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public String convertDate(Long dateInMilliseconds, String dateFormat) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.KOREA);
        String dateString = formatter.format(new Date(dateInMilliseconds));
        return dateString;
    }


    @Test
    public void test2() throws Exception {


        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1539320828481L);
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

        Timber.e(num + " " + strWeek);


        Uri uri = Uri.parse("https://www.naver.com/").buildUpon().appendQueryParameter("data", "tt").build();
        Timber.e(uri+"");
    }

    private String time(int milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;            //초
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);  //분
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);//시
        String time = "시 = " + hours + " 분 = " + minutes + " 초 = " + seconds;
        return time;
    }

    private String getTime(int millis) {
        return String.format("%02d min, %02d sec", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    @Test
    public void testt() throws Exception {
        Timber.e(time(1075491941));
        Timber.e(getTime(1075491941));
    }

    @Test
    public void listTest() throws Exception {
        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("c");
        list2.add("d");

        List<String> list3 = new ArrayList<>();
        list3.add("e");
        list3.add("f");
        list3.add("c");
        list3.add("g");

        Timber.e(list2.contains("a")+"");

        List<String> list22 = new ArrayList<>();
        list2.add("c");
        list2.add("d");

        List<String> list33 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("c");
        list2.add("d");


        for (int i = 0; i < list22.size(); i++) {
            Timber.e(list22.contains(list33.get(i))+"");
            Timber.e(list22.equals(list33.get(i))+"");
        }

        for (int i = 0; i < list33.size(); i++) {
            Timber.e(list33.contains(list22.get(i))+"");
        }


    }

    @Test
    public void TimeStampTest() throws Exception {
        String source = "650991600"; //DB에 저장된 유닉스타임 형식 날짜
        long t = 1547010059439L;//Long.parseLong(source + "000");
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.KOREA);

        Long sevenDays = TimeUnit.DAYS.toMillis(7);//7days to millisecond
        Date date = new Date(t + sevenDays);

        Timber.e(simpleDate.format(date));
        //assertEquals("19900819",simpleDate.format(t),"값이 다르다 실패");
    }

    @Test
    public void checkTest() throws Exception {
//        Double data = 0.014D;
//        Double d = data * 100;
//        Float a=d.floatValue();
//        if(a ==Math.floor(a)){
//            System.out.println(a.intValue()+"%");
//        }else{
//            System.out.println(a+"%");
//        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);  //어제 구하기(오늘 날짜에서 하루를 뺌.)
        String date = sdf.format(calendar.getTime());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE,0);  //오늘
        String date2 = sdf.format(calendar2.getTime());

        System.out.println(date2);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, -1);  // 오늘 날짜에서 하루를 뺌.
//        String date = sdf.format(calendar.getTime());
//        Timber.e(date);

    }
}
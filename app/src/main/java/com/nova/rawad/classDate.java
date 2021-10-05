package com.nova.rawad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class classDate {
    public static String date(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-yyyy",new Locale("EN"));
        int day = new Date().getDate();
        String date = day+"-"+dateFormat.format(new Date()).split("-")[0]+"-"+new Date().toString().split(" ")[5];
        return date;
    }

    public static String time(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",new Locale("EN"));
        String time = timeFormat.format(new Date());
        return time;
    }

    public static String addDays(String dt , int dayCount){

        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy",new Locale("EN"));
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
            c.add(Calendar.DATE, dayCount);  // number of days to add
            dt = sdf.format(c.getTime());
            return dt;
        } catch (ParseException e) {
            return null;
        }

    }

    public static String currentTimeAtMs(){
        return System.currentTimeMillis()+"";
    }
}

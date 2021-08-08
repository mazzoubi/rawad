package com.nova.smartdetectorsystem;

import java.text.SimpleDateFormat;
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
    public static String currentTimeAtMs(){
        return System.currentTimeMillis()+"";
    }
}

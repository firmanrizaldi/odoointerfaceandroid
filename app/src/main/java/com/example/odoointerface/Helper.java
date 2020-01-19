package com.example.odoointerface;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Helper {

//    string datr - helper.gettimestamp("yyyy-MM-dd HH:mm:ss")
    public static  String getTimeStamp(String pattern){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis()) ;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern) ;
        return String.valueOf(sdf.format(timestamp)) ;

    }
}

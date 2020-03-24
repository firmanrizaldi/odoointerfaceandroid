package com.example.odoointerface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class Activity_attendance extends AppCompatActivity {
    private OdooUtility odoo;
    private TextView textCheck , in, out;
    private String uid, password, serverAddress,database;
    String date, date_depan, check_in, check_out, date_out;
    Integer user_id, id_employee, id_attende;
    private long searchTaskId, createTaskId, searchAttendeId, updateTaskId, searchCheckout;
    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        textCheck = findViewById(R.id.textcheck) ;
        uid = SharedData.getKey(this,"uid") ;
        password = SharedData.getKey(this, "password") ;
        serverAddress = SharedData.getKey(this,"serverAddress");
        database = SharedData.getKey(this,"database") ;
        odoo = new OdooUtility(serverAddress,"object") ;
        date = Helper.getTimeStamp("yyy-MM-dd HH:mm:ss");
        date_depan = Helper.getTimeStamp("yyy-MM-dd");
        check_in = dateFormatIn(date_depan) ;
        check_in = dateTimeFormat(check_in);
        check_out = dateFormatOut(date_depan) ;
        check_out =dateTimeFormat(check_out) ;
        date = dateTimeFormat(date);
        user_id = Integer.valueOf(uid);

    }

    public void onAbsenNow(View view) {
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("user_id", "=", user_id)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name"
            ));
        }};
//
        searchTaskId = odoo.search_read(listener,
                database,
                uid,
                password,
                "hr.employee",
                conditions,
                fields) ;
    }

    private void searchAttende() {
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("employee_id", "=", id_employee),
                        Arrays.asList("check_in", ">=", check_in),
                        Arrays.asList("check_in", "<=", check_out)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "employee_id"
            ));
        }};
//
        searchAttendeId = odoo.search_read(listener,
                database,
                uid,
                password,
                "hr.attendance",
                conditions,
                fields) ;
    }

    private void createAttende() {
        List data = Arrays.asList(new HashMap() {{
            put("employee_id", id_employee);
            put("check_in", date);
        }});

        createTaskId = odoo.create(listener,
                database,
                uid,
                password,
                "hr.attendance",
                data);

    }
    private void searchCheckoutAttende() {
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("employee_id", "=", id_employee),
                        Arrays.asList("check_out", ">=", check_in),
                        Arrays.asList("check_out", "<=", check_out)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "employee_id"
            ));
        }};
//
        searchCheckout = odoo.search_read(listener,
                database,
                uid,
                password,
                "hr.attendance",
                conditions,
                fields) ;
    }
    private void checkOutAttendance() {
        List data = Arrays.asList(
                Arrays.asList(id_attende),
                new HashMap(){{
                    put("check_out",date) ;
                }}
        );
        updateTaskId = odoo.update(listener,
                database,
                uid,
                password,
                "hr.attendance",
                data) ;

    }
    public static String dateFormatIn(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT+7");
        outputFormat.setTimeZone(gmtTime);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            str = outputFormat.format(calendar.getTime()).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str + " 00:00:00";
    }
    public static String dateFormatOut(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT+7");
        outputFormat.setTimeZone(gmtTime);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            str = outputFormat.format(calendar.getTime()).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str + " 23:59:59";
    }
    public static String dateTimeFormat(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT+7");
        outputFormat.setTimeZone(gmtTime);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, -7);
            calendar.add(Calendar.MINUTE, 0);
            str = outputFormat.format(calendar.getTime()).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    XMLRPCCallback listener =  new XMLRPCCallback() {
        @Override
        public void onResponse(long id, Object result) {
            Looper.prepare();
//            odoo.MessageDialog(Activity_attendance.this,
//                    "Login Error. " + searchTaskId + " sama " + id);
            if (id == searchTaskId){
                Object[] classObjs = (Object[])result ;
                int length= classObjs.length;

                if (result instanceof Boolean && (Boolean) result == false) {
                    odoo.MessageDialog(Activity_attendance.this,"Login Error. Please try again");
                } else {
//                    Toast.makeText(Activity_attendance.this,"Employee ada ==>"+length, Toast.LENGTH_LONG).show();
                    for (int i=0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> classObj = (Map<String,Object>) classObjs[i];
                        id_employee = (Integer)classObj.get("id") ;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchAttende() ;
                        }
                    });

                }
            } else if (id == createTaskId) {
                Toast.makeText(Activity_attendance.this,"Berhasil Check in ", Toast.LENGTH_LONG).show();

            }else if (id == updateTaskId) {
                Toast.makeText(Activity_attendance.this,"Berhasil Check out ", Toast.LENGTH_LONG).show();

            }else if (id == searchCheckout) {
                Object[] classObjs = (Object[])result ;
                int length= classObjs.length;
                if (length > 0){
                    Toast.makeText(Activity_attendance.this,"Anda Sudah Check-out", Toast.LENGTH_LONG).show();

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkOutAttendance() ;
                        }
                    });
                }
            } else if (id == searchAttendeId) {
                Object[] classObjs = (Object[])result ;
                int length= classObjs.length;
//                Toast.makeText(Activity_attendance.this,"Attende ada ===>"+length, Toast.LENGTH_LONG).show();
                if (length > 0){
                    for (int i=0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> classObj = (Map<String,Object>) classObjs[i];
                        id_attende = (Integer)classObj.get("id") ;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchCheckoutAttende() ;
                        }
                    });
                } else {
                    Toast.makeText(Activity_attendance.this,"check_id ===>"+ id_employee, Toast.LENGTH_LONG).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createAttende() ;
                        }
                    });
                }
            }

            Looper.loop();
            odoo.MessageDialog(Activity_attendance.this,"Login Error. Please try again"+id);
        }

        @Override
        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            odoo.MessageDialog(Activity_attendance.this,
                    "Login Error. " + error.getMessage());
            Looper.loop();
        }

        @Override
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();
            odoo.MessageDialog(Activity_attendance.this,
                    "Login Error. " + error.getMessage());
            Looper.loop();
        }
    } ;




}

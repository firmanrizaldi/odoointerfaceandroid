package com.example.odoointerface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class MainActivity extends AppCompatActivity {

    private OdooUtility odoo;
    private long loginTaskId;

    EditText etServerUrl ;
    EditText etDatabase ;
    EditText etUsername ;
    EditText etPassword ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etServerUrl = findViewById(R.id.etServerUrl) ;
        etDatabase = findViewById(R.id.etDatabase) ;
        etUsername = findViewById(R.id.etUsername) ;
        etPassword = findViewById(R.id.etPassword) ;
        etServerUrl.setText("http://13.229.202.100:8069");
        etDatabase.setText("and");
        etUsername.setText("admin");
        etPassword.setText("admin");

    }

    public void onLogin(View view) {

        String serverUrl = etServerUrl.getText().toString() ;
        String database = etDatabase.getText().toString() ;
        String username = etUsername.getText().toString() ;
        String password = etPassword.getText().toString() ;

        SharedData.setKey(MainActivity.this,"password",password);
        SharedData.setKey(MainActivity.this,"username",username);
        SharedData.setKey(MainActivity.this,"serverAddress",serverUrl);
        SharedData.setKey(MainActivity.this,"database",database);


        odoo = new OdooUtility(serverUrl, "common");
        loginTaskId = odoo.login(listener, database, username, password);

    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        public void onResponse(long id, Object result) {

            Looper.prepare();
            if (id == loginTaskId) {
                if (result instanceof Boolean && (Boolean) result == false) {
                    odoo.MessageDialog(MainActivity.this,"Login Error. Please try again");
                } else {
                    String uid = result.toString();
                    SharedData.setKey(MainActivity.this, "uid", uid);
                    odoo.MessageDialog(MainActivity.this,"Login Succeed. uid=" + uid);

                    Intent intent = new Intent(MainActivity.this, MenuActivity.class) ;
                    intent.putExtra("uid", uid) ;
                    startActivity(intent);
                }
            }
            Looper.loop();
        }

        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            Log.e("LOGIN", error.getMessage());
            odoo.MessageDialog(MainActivity.this,
                    "Login Error. " + error.getMessage());
            Looper.loop();
        }

        @Override
        public void onServerError(long id, XMLRPCServerException error) {

            Looper.prepare ();

            Log. e ("LOGIN", error.getMessage());
            odoo.MessageDialog(MainActivity.this,
                    "Login Error. " + error.getMessage());
            Looper.loop ();

        }

    };




}

package com.example.odoointerface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class PartnerDetailActivity extends AppCompatActivity {
    private OdooUtility odoo;
    private String uid;
    private String password;
    private String serverAddress;
    private String database;
    private Partner partner;

    LinearLayout llbtn ;
    Button btnCreate ;

    private long searchTaskId, updateTaskId, deleteTaskId, createTaskId;
    private String name;

    EditText etName, etAddress, etPhone, etCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_detail);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddres);
        etPhone = findViewById(R.id.etPhone);
        etCountry = findViewById(R.id.etCountry);
        llbtn = findViewById(R.id.llbtn) ;
        btnCreate = findViewById(R.id.btnCreate) ;

        uid = SharedData.getKey(this, "uid");
        password = SharedData.getKey(this, "password");
        serverAddress = SharedData.getKey(this, "serverAddress");
        database = SharedData.getKey(this, "database");


        odoo = new OdooUtility(serverAddress, "object");

        name = getIntent().getStringExtra("name");

        if (name != null ){
            etName.setText(name);
            searchPartnerByName();
            llbtn.setVisibility(View.VISIBLE);
        } else{
            btnCreate.setVisibility(View.VISIBLE);
        }

        partner = new Partner();

    }

    public void searchPartnerByName() {
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("name", "=", name)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name",
                    "street",
                    "country_id",
                    "phone"
            ));
        }};

        searchTaskId = odoo.search_read(listener,
                database,
                uid,
                password,
                "res.partner",
                conditions,
                fields);
    }

    public void onUpdatePartner(View view) {
        final String name = etName.getText().toString() ;
        final String stret = etAddress.getText().toString() ;
        final String phone = etPhone.getText().toString() ;

        List data = Arrays.asList(
                Arrays.asList(partner.getId()),
                new HashMap(){{
                    put("name",name) ;
                    put("street",stret) ;
                    put("phone",phone) ;

                }}
        );
        updateTaskId = odoo.update(listener,
                database,
                uid,
                password,
                "res.partner",
                data) ;
    }

    public void onDeletePartner(View view) {
        List id = new ArrayList() ;
        id.add(partner.getId()) ;

        deleteTaskId = odoo.delete(listener,
                database,
                uid,
                password,
                "res.partner",
                id) ;

    }

    public void onCreatePartner(View view) {
        final String name = etName.getText().toString() ;
        final String stret = etAddress.getText().toString() ;
        final String phone = etPhone.getText().toString() ;

        List data = Arrays.asList(
                        new HashMap(){{
                            put("name",name) ;
                            put("street",stret) ;
                            put("phone",phone) ;

                        }}
                );
        createTaskId = odoo.create(listener,
                database,
                uid,
                password,
                "res.partner",
                data) ;

    }

    public void fillPartnerDetail() {
        etAddress.setText(partner.getStreet());
        etPhone.setText(partner.getPhone());
        etCountry.setText(partner.getCountry_name());
    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        @Override
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id == searchTaskId) {
                Object[] classObjs = (Object[]) result;
                int length = classObjs.length;

                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> classObj = (Map<String, Object>) classObjs[i];
                        partner.setData(classObj);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillPartnerDetail();
                        }
                    });
                } else {
                    Toast.makeText(PartnerDetailActivity.this, "Partner not found", Toast.LENGTH_LONG).show();
                }

            }
            else if(id == updateTaskId){
                final Boolean updateResult = (Boolean)result ;

                if (updateResult) {
                    odoo.MessageDialog(PartnerDetailActivity.this,"Berhasil update");
                    finish();
                }
                else odoo.MessageDialog(PartnerDetailActivity.this,"Gagal update");

            }
            else if(id == deleteTaskId){
                final Boolean deleteResult = (Boolean)result ;

                if (deleteResult) {
                    odoo.MessageDialog(PartnerDetailActivity.this,"Berhasil delete");
                    finish();
                }
                else odoo.MessageDialog(PartnerDetailActivity.this,"Gagal delete");
            }
            else if(id == createTaskId){
                final String createResult = (String)result ;

                if (createResult != null) {
                    odoo.MessageDialog(PartnerDetailActivity.this,"Berhasil create"+createResult);
                }
                else odoo.MessageDialog(PartnerDetailActivity.this,"Gagal create"+createResult);
            }

                Looper.loop();

            }

            @Override
            public void onError( long id, XMLRPCException error){
                Looper.prepare();
                Looper.loop();
            }

            @Override
            public void onServerError( long id, XMLRPCServerException error){
                Looper.prepare();
                Looper.loop();
            }
        } ;



}

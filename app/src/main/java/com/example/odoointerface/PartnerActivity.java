package com.example.odoointerface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class PartnerActivity extends AppCompatActivity {
    private OdooUtility odoo;
    private String uid;
    private String password;
    private String serverAddress;
    private String database ;

    private long searchTaskId;

    ListView listViewPartner ;
    List arrayListPartner ;
    List arrayListPartner_id ;

    EditText etKeyword ;
    String sourceActivity ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);

        uid = SharedData.getKey(this,"uid") ;
        password = SharedData.getKey(this, "password") ;
        serverAddress = SharedData.getKey(this,"serverAddress");
        database = SharedData.getKey(this,"database") ;

        sourceActivity = getIntent().getStringExtra("sourceActivity") ;

        odoo = new OdooUtility(serverAddress,"object") ;
        arrayListPartner = new ArrayList() ;
        arrayListPartner_id = new ArrayList() ;

        listViewPartner = findViewById(R.id.listview) ;
        etKeyword = findViewById(R.id.etKeyword) ;

        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onChangeText(s.toString()) ;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!etKeyword.getText().toString().isEmpty()) {
            onChangeText(etKeyword.getText().toString()) ;
        }else {
            onChangeText("") ;
        }

    }


    private void onChangeText(String keyword) {

        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("name", "ilike", keyword)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name"
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

    public void onSync(View view) {
        String keyword = etKeyword.getText().toString();

        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("name", "ilike", keyword)
                )
        );


        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name"
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

    public void fillListPartner() {
        ArrayAdapter<String>  adapter = new ArrayAdapter<>( this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                arrayListPartner ) ;

        listViewPartner.setAdapter(adapter);
        listViewPartner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                int position = i ;

                int partner_id = (Integer)arrayListPartner_id.get(i) ;
                String name = (String)arrayListPartner.get(position) ;
                Toast.makeText(PartnerActivity.this,"name ==>"+name, Toast.LENGTH_LONG).show();

                if (sourceActivity != null){
                    Intent intent = new Intent() ;
                    intent.putExtra("partner_id", partner_id) ;
                    intent.putExtra("partner_name",name) ;
                    setResult(2,intent);
                    finish();

                } else {
                    Intent intent = new Intent(PartnerActivity.this,PartnerDetailActivity.class) ;
                    intent.putExtra("name",name) ;
                    PartnerActivity.this.startActivity(intent);
                }



            }
        });
    }

    public void onCreate(View view) {
        startActivity(new Intent(PartnerActivity.this, PartnerDetailActivity.class));
    }


    XMLRPCCallback listener = new XMLRPCCallback() {
        @Override
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id == searchTaskId) {
                Object[] classObjs = (Object[])result ;
                int length= classObjs.length;

                if(length>0){
                    Toast.makeText(PartnerActivity.this,"partner ada ==>"+length, Toast.LENGTH_LONG).show();
                    arrayListPartner.clear();
                    arrayListPartner_id.clear();
                    for (int i=0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> classObj = (Map<String,Object>) classObjs[i];
                        arrayListPartner.add(classObj.get("name"));
                        arrayListPartner_id.add((Integer)classObj.get("id")) ;
                    }

                    filllisttt() ;
                }
                else {
                    Toast.makeText(PartnerActivity.this,"partner tidak ada", Toast.LENGTH_LONG).show();
                    arrayListPartner.clear();

                    filllisttt() ;

                }

            }


            Looper.loop();
        }

        private void filllisttt() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillListPartner() ;
                }
            });
        }

        @Override
        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            Looper.loop();
        }

        @Override
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();
            Looper.loop();
        }
    } ;



}

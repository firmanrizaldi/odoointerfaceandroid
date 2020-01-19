package com.example.odoointerface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class SalesOrderActivity extends AppCompatActivity {
    private OdooUtility odoo;
    private String uid;
    private String password;
    private String serverAddress;
    private String database ;

    private long searchTaskId;

    ListView listviewSalesOrder ;
    List arrayListSalesOrder ;

    EditText etKeyword ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order);


        uid = SharedData.getKey(this,"uid") ;
        password = SharedData.getKey(this, "password") ;
        serverAddress = SharedData.getKey(this,"serverAddress");
        database = SharedData.getKey(this,"database") ;

        odoo = new OdooUtility(serverAddress,"object") ;
        arrayListSalesOrder = new ArrayList() ;
        listviewSalesOrder = findViewById(R.id.listviewSalesOrder) ;
        etKeyword = findViewById(R.id.etKeyword) ;


    }

    public void onCreate(View view) {
    }

    private void fillSalesOrder() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                arrayListSalesOrder ) ;


        listviewSalesOrder.setAdapter(adapter);
        listviewSalesOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String soName = (String)arrayListSalesOrder.get(i) ;
//                Toast.makeText(SalesOrderActivity.this,"name ==>"+name, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SalesOrderActivity.this,SalesOrderDetailActivity.class) ;
                intent.putExtra("name",soName) ;
                SalesOrderActivity.this.startActivity(intent);
            }
        });
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
                "sale.order",
                conditions,
                fields);

    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        @Override
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id == searchTaskId) {
                Object[] classObjs = (Object[])result ;
                int length= classObjs.length;

                if (length>0){
                    arrayListSalesOrder.clear();

                    for (int i=0; i < length; i++) {
                        Map<String,Object> classObj = (Map<String,Object>) classObjs[i];
                        arrayListSalesOrder.add(classObj.get("name"));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillSalesOrder() ;
                        }
                    });
                }
                else {
                    odoo.MessageDialog(SalesOrderActivity.this,"Sales Order Not Found");
                }
            }



            Looper.loop();
        }

        @Override
        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            odoo.MessageDialog(SalesOrderActivity.this,error.getMessage());
            Looper.loop();
        }

        @Override
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();
            odoo.MessageDialog(SalesOrderActivity.this,error.getMessage());
            Looper.loop();
        }
    } ;




}

package com.example.odoointerface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class SalesOrderDetailActivity extends AppCompatActivity {
    private OdooUtility odoo;
    private String uid;
    private String password;
    private String serverAddress;
    private String database;

    private long searchTaskId, searchSOlineTaskId;
    private String name;
    private SalesOrder salesOrder;

    private List<salesOrderLine> salesOrderLines = new ArrayList<>();
    SalesOrderAdapter salesOrderAdapter;

    EditText etDate, etCustomer, etReference, etWarehouse, etTotal;

    RecyclerView recycleViewSOline;

    String date;
    int partner_id;
    String partner_name;

    public static final String activity_name = SalesOrderDetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_detail);

        etDate = findViewById(R.id.etDate);
        etCustomer = findViewById(R.id.etCustomer);
        etReference = findViewById(R.id.etReference);
        etWarehouse = findViewById(R.id.etWarehouse);
        etTotal = findViewById(R.id.etTotal);

        recycleViewSOline = findViewById(R.id.recycleViewSOline);

        uid = SharedData.getKey(this, "uid");
        password = SharedData.getKey(this, "password");
        serverAddress = SharedData.getKey(this, "serverAddress");
        database = SharedData.getKey(this, "database");

        odoo = new OdooUtility(serverAddress, "object");

        salesOrder = new SalesOrder();

        salesOrderAdapter = new SalesOrderAdapter(this, salesOrderLines);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recycleViewSOline.setLayoutManager(layoutManager);

        recycleViewSOline.setItemAnimator(new DefaultItemAnimator());
        recycleViewSOline.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleViewSOline.setAdapter(salesOrderAdapter);

        etCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesOrderDetailActivity.this, PartnerActivity.class);
                intent.putExtra("sourceActivity", activity_name);
                startActivityForResult(intent, 2);

            }
        });


        name = getIntent().getStringExtra("name");
        if (name != null) searchSalesOrderByName();
        else {
            String soLine = getIntent().getStringExtra("soLine");
            Gson gson = new Gson();
            Type listofClassObject = new TypeToken<ArrayList<salesOrderLine>>() {
            }.getType();
            salesOrderLines.addAll((Collection<? extends salesOrderLine>) gson.fromJson(soLine, listofClassObject));

            Toast.makeText(SalesOrderDetailActivity.this, "" + salesOrderLines.size(), Toast.LENGTH_LONG).show();


            date = Helper.getTimeStamp("yyy-MM-dd HH:mm:ss");
            double total = getIntent().getDoubleExtra("total_price", 0);

            etDate.setText(date);
            etTotal.setText(String.valueOf(total));

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            partner_name = data.getStringExtra("partner_name");
            partner_id = data.getIntExtra("partner_id", 0);
            etCustomer.setText(partner_name);


        }
    }

    private void fillSalesOrderDetail() {
        etDate.setText(salesOrder.getDate_order());
        etCustomer.setText(salesOrder.getPartner_name());
        etWarehouse.setText(salesOrder.getWarehouse_name());
        etReference.setText(salesOrder.getClient_order_ref());
        etTotal.setText(String.valueOf(salesOrder.getAmount_total()));


        searchSalesOrderBySOId();

    }

    public void onCreateSalesOrder(View view) {

        List data = Arrays.asList(new HashMap() {{
            put("date_order", date);
            put("partner_id", partner_id);
        }};

    }


    public void searchSalesOrderByName() {
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("name", "=", name)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name",
                    "partner_id",
                    "date_order",
                    "warehouse_id",
                    "client_order_ref",
                    "amount_total"

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

    public void searchSalesOrderBySOId() {
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("id", "=", salesOrder.getId())
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "product_id",
                    "product_uom",
                    "product_uom_qty",
                    "price_unit",
                    "price_subtotal"

            ));
        }};

        searchSOlineTaskId = odoo.search_read(listener,
                database,
                uid,
                password,
                "sale.order.line",
                conditions,
                fields);
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
                        salesOrder.setData(classObj);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillSalesOrderDetail();
                        }
                    });
                } else {
                    Toast.makeText(SalesOrderDetailActivity.this, "Sales Order not found", Toast.LENGTH_LONG).show();
                }
            } else if (id == searchSOlineTaskId) {
                Object[] classObjs = (Object[]) result;
                int length = classObjs.length;

                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        salesOrderLines.clear();
                        salesOrderLine salesOrderLine = new salesOrderLine();
                        Map<String, Object> classObj = (Map<String, Object>) classObjs[i];
                        salesOrderLine.setData(classObj);
                        salesOrderLines.add(salesOrderLine);

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            salesOrderAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Toast.makeText(SalesOrderDetailActivity.this, "Sales Order not found", Toast.LENGTH_LONG).show();
                }
            }


            Looper.loop();
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
    };

}

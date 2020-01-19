package com.example.odoointerface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.MyViewHolder> {

    private Context context ;
    private List<salesOrderLine> salesOrderLines ;

    public SalesOrderAdapter(Context context, List<salesOrderLine> salesOrderLines) {
        this.context = context;
        this.salesOrderLines = salesOrderLines;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_order_line,parent,false) ;
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        salesOrderLine salesOrderLine = salesOrderLines.get(position) ;
        holder.tvName.setText(salesOrderLine.getProduct_name());
        holder.tvQty.setText(String.valueOf(salesOrderLine.getProduct_uom_qty()));
        holder.tvUom.setText(salesOrderLine.getProduct_uom_name());

        double price_total = salesOrderLine.getProduct_uom_qty() * salesOrderLine.getPrice_unit() ;
        holder.tvPrice.setText(String.valueOf(price_total));
    }

    @Override
    public int getItemCount() {
        if (salesOrderLines != null) return salesOrderLines.size() ;
        else return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvQty, tvUom, tvPrice ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName) ;
            tvQty = itemView.findViewById(R.id.tvQty) ;
            tvUom = itemView.findViewById(R.id.tvUom) ;
            tvPrice = itemView.findViewById(R.id.tvPrice) ;
        }
    }

}

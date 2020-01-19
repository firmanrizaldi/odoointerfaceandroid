package com.example.odoointerface;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class productProductAdapter extends RecyclerView.Adapter<productProductAdapter.MyViewHolder> {

    private Context context ;
    private List<productProduct> productProductList ;

    ProductProductAdapterListener listener ;

    public productProductAdapter(Context context, List<productProduct> productProductList, ProductProductAdapterListener listener) {
        this.context = context;
        this.productProductList = productProductList;
        this.listener = listener ;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product,parent,false) ;
        return new MyViewHolder(itemView) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        productProduct productProduct = productProductList.get(position) ;
        holder.tvName.setText(productProduct.getName());
//        holder.tvUom.setText(productProduct.getUom_name());
        holder.tvPrice.setText(String.valueOf(productProduct.getLst_price()));

        if (!productProduct.getImage_medium().isEmpty()) {
            byte[] imageByteArray = Base64.decode(productProduct.getImage_medium(), Base64.DEFAULT) ;
            Glide.with(context)
                    .load(imageByteArray)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.tvProduct) ;
        }

    }

    @Override
    public int getItemCount() {
        if (productProductList != null ) return productProductList.size() ;
        else return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvQty ;
        ImageView tvProduct ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName) ;
            tvPrice = itemView.findViewById(R.id.tvPrice) ;
//            tvUom = itemView.findViewById(R.id.tvUom) ;
            tvQty = itemView.findViewById(R.id.tvQty) ;
            tvProduct = itemView.findViewById(R.id.tvProduct) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.click(productProductList.get(getAdapterPosition()));
                }
            });


        }
    }


    public interface ProductProductAdapterListener{
        void click(productProduct productProduct) ;
    }



}

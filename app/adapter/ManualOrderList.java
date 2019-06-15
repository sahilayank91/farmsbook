package com.sahil.farmsbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.model.Product;

public class ManualOrderList extends RecyclerView.Adapter<ManualOrderList.ProductViewHolder> implements RCVItemClickListener {
    private ArrayList<Product> listProduct;
    private Context context;
    private String type;

    public ManualOrderList(Context c,ArrayList<Product> listProduct){
        this.context = c;
        this.listProduct = listProduct;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_manual_order_list, parent, false);
        return new ProductViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        final Product current = listProduct.get(position);
        holder.serial.setText(String.valueOf(position+1));
        holder.name.setText(current.getName());
        String quantity = current.getUnitQuantity() + " " + current.getUnit();
        holder.quantity.setText(quantity);
        holder.brand.setText(current.getBrand());
    }


    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView serial, quantity, name,brand;
        ImageButton deleteButton;
        private ProductViewHolder(View itemView) {
            super(itemView);

            quantity = itemView.findViewById(R.id.quantity);
            name = itemView.findViewById(R.id.productName);
            brand = itemView.findViewById(R.id.brandName);
            serial = itemView.findViewById(R.id.serial);

        }


        @Override
        public void onClick(View v) {

        }
    }

}

package com.sahil.farmsbook.adapter;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.Views.SellerProfile;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.model.User;

public class SellerListAdapter extends RecyclerView.Adapter<SellerListAdapter.UserViewHolder> implements RCVItemClickListener {
    private ArrayList<User> listUser;
    private Context context;
    private String orderid;
    public SellerListAdapter(Context c,ArrayList<User> listUser, String orderid){
        this.context = c;
        this.orderid = orderid;
        this.listUser = listUser;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_seller_list, parent, false);
        return new UserViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final User current = listUser.get(position);
        holder.name.setText(current.getShop());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SellerProfile.class);
                intent.putExtra("sellerId",current.get_id());
                intent.putExtra("name",current.getFirstname() + " " + current.getLastname());
                intent.putExtra("orderId",orderid);
                intent.putExtra("shop",current.getShop());
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

        holder.logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SellerProfile.class);
                intent.putExtra("sellerId",current.get_id());
                intent.putExtra("name",current.getFirstname() + " " + current.getLastname());
                intent.putExtra("orderId",orderid);
                intent.putExtra("shop",current.getShop());
                context.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return listUser.size();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView logo;
        private UserViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.sellerName);
            logo = itemView.findViewById(R.id.sellerImage);


        }


        @Override
        public void onClick(View v) {

        }
    }

}

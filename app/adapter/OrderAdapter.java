package com.sahil.farmsbook.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.Views.PaymentActivity;
import com.sahil.farmsbook.Views.YourOrders;
import com.sahil.farmsbook.model.Order;
import com.sahil.farmsbook.utilities.Server;

import static android.view.View.GONE;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private ArrayList<Order> listOrders;
    Context context1;
    public OrderAdapter(Context context, ArrayList<Order> listOrders) {
        this.context1 = context;
        this.listOrders = listOrders;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_orders, parent, false);
        return new OrderViewHolder(rootView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        final Order current = listOrders.get(position);

        holder.orderid.setText(current.get_id());


        if(current.getPayment_method().equals("Online") && current.getPayment_status().equals("Incomplete")){
            holder.paymentstatus.setText("Pending");
        }else{
            holder.paymentstatus.setText("Pending");
        }

        holder.paymentmethod.setText(current.getPayment_method());

        holder.orderstatus.setText(current.getOrderstatus());

        String orderDate = current.getCreate_time();
        Date order = null;
        try {
            order = new SimpleDateFormat("yyyy-MM-dd").parse(orderDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(current.getOrderstatus().equals("Received") && current.getPayment_method().equals("Cash on Delivery")){
            holder.cancel.setVisibility(View.VISIBLE);
        }







        holder.orderdate.setText(order.toLocaleString().substring(0, 12));

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCancelDialog(v, holder.getAdapterPosition());
            }
        });
        holder.deliveredotp.setText(current.getDelivered_otp());

        holder.total.setText(current.getTotal());

        holder.viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    orderWithItems(v, current.getOrder());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });




        if(current.getOrderstatus().equals("Received") && current.getPayment_method().equals("Cash on Delivery")){
            holder.checkout.setVisibility(GONE);
            holder.cancel.setVisibility(View.VISIBLE);
        }
        if(current.getOrderstatus().equals("Processed") || current.getOrderstatus().equals("Completed")){
            holder.addRating.setVisibility(View.VISIBLE);
        }else{
            holder.addRating.setVisibility(View.GONE);
        }
        if(current.getOrderstatus().equals("Processed") || current.getOrderstatus().equals("Completed")|| current.getOrderstatus().equals("Cancelled")){
            holder.checkout.setVisibility(GONE);
            holder.cancel.setVisibility(GONE);
        }

        if(current.getOrderstatus().equals("Cancelled")){
            holder.viewOrder.setVisibility(GONE);
        }
//        if(current.getOrderstatus().equals("Recieved")){
//            holder.checkout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context1,"Can't Checkout as it is not confirmed by seller yet",Toast.LENGTH_SHORT).show();
//
//                }
//            });
//        }else{
//            holder.checkout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context1, CheckoutActivity.class);
//                    intent.putExtra("orderId", current.get_id());
//                    intent.putExtra("total",current.getTotal());
//                    context1.startActivity(intent);
//                    ((Activity)context1).finish();
//                }
//            });
//        }

//        if(current.getOrderstatus().equals("Confirmed") || current.getOrderstatus().equals("Processed")|| current.getOrderstatus().equals("Completed")){
//            holder.comment.setText(current.getComment());
//
//        }
        holder.checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context1, PaymentActivity.class);
                    intent.putExtra("id", current.getPaymentId());
                    intent.putExtra("longurl","https://www.instamojo.com/@farmsbooksolutionspvt_ltd/"+current.getPaymentId());
                    intent.putExtra("orderId",current.get_id());
                    context1.startActivity(intent);
                    ((Activity)context1).finish();
                }
            });
        holder.addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog(v, position);
            }
        });

    }
    private void ratingDialog(View view,final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context1);
//        LayoutInflater inflater = context1.getApplicationContext()getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) context1.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        builder.setTitle("With RatingBar");
        View dialogLayout = inflater.inflate(R.layout.dialog_rating, null);
        final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingBar);
        builder.setView(dialogLayout);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context1, "Rating is " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
                new addRating().execute(listOrders.get(position).get_id(),String.valueOf(ratingBar.getRating()));
            }
        });
        builder.show();
    }
    private void OrderwithImageView(View view, String image_url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context1);
        final ImageView imageView = new ImageView(context1);
        imageView.setImageDrawable(context1.getResources().getDrawable(R.drawable.placeholder_image));
        Glide.with(context1).load(image_url).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
        imageView.setMinimumHeight(1000);
        imageView.setMaxHeight(1200);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        builder.setPositiveButton("OK", null);
        builder.setView(imageView);
        builder.show();
    }
    private void orderWithItems (View view, String order) throws JSONException {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context1);
        builderSingle.setIcon(R.drawable.ic_account_box_black_24dp);
        builderSingle.setTitle("Items in the Order:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context1, android.R.layout.simple_list_item_1);

        JSONArray jsonArray = new JSONArray(order);
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            arrayAdapter.add("Name - " + jsonObject.get("name") + ",Brand - " + jsonObject.getString("brand") + ", Quantity -" + jsonObject.getString("quantity") + "Unit - "+jsonObject.getString("unit")+", Price - Rs "+  jsonObject.getString("total"));
        }

        builderSingle.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }
    private void openCancelDialog(View view, final int position){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context1);
        alertDialogBuilder.setMessage("Are you sure you want to cancel the Order");
        alertDialogBuilder.setIcon(R.drawable.newicon);
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                Toast.makeText(context1,"You have accepted to cancel the Order !!",Toast.LENGTH_LONG).show();
                                new CancelOrder().execute(listOrders.get(position).get_id());

                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView orderid, orderdate,paymentmethod,orderstatus,deliveredotp,total,paymentstatus;
        Button cancel,checkout,viewOrder,addRating;

        private OrderViewHolder(View itemView) {
            super(itemView);

            orderid= itemView.findViewById(R.id.order_id);
            orderdate = itemView.findViewById(R.id.order_date);
            orderstatus = itemView.findViewById(R.id.order_status);
            cancel = itemView.findViewById(R.id.cancelbutton);
            deliveredotp = itemView.findViewById(R.id.deliveredotp);
            total = itemView.findViewById(R.id.total);
            paymentmethod = itemView.findViewById(R.id.paymentmethod);
            checkout = itemView.findViewById(R.id.checkout);
            viewOrder= itemView.findViewById(R.id.viewOrder);
            addRating = itemView.findViewById(R.id.addRating);
            paymentstatus = itemView.findViewById(R.id.payment_status);

            itemView.setOnClickListener(this);
            orderstatus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }



    @SuppressLint("StaticFieldLeak")
    class addRating extends AsyncTask<String, String, String> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(context1);
            progress.setMessage("Cancelling Order");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                HashMap<String,String> params = new HashMap<>();
                params.put("_id",strings[0]);
                params.put("rating",strings[1]);
                Gson gson = new Gson();
                String json = gson.toJson(params);
                result = Server.post(context1.getResources().getString(R.string.addRating),json);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            try {

                JSONObject jsonObject =new JSONObject(s);

                String success = jsonObject.getString("success");
                if (!success.equals("true")) {
                    Toast.makeText(context1,"Some error occured in cancelling Order..Please check your internet connection",Toast.LENGTH_LONG).show();
                }else{
                   Toast.makeText(context1,"Order has been cancelled",Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(context1,YourOrders.class);
                   context1.startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }
    @SuppressLint("StaticFieldLeak")
    class CancelOrder extends AsyncTask<String, String, String> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(context1);
            progress.setMessage("Cancelling Order");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                HashMap<String,String> params = new HashMap<>();
                params.put("_id",strings[0]);
                Gson gson = new Gson();
                String json = gson.toJson(params);
                result = Server.post(context1.getResources().getString(R.string.cancelOrder),json);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            try {

                JSONObject jsonObject =new JSONObject(s);

                String success = jsonObject.getString("success");
                if (!success.equals("true")) {
                    Toast.makeText(context1,"Some error occured in cancelling Order..Please check your internet connection",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context1,"Order has been cancelled",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context1,YourOrders.class);
                    context1.startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }
}
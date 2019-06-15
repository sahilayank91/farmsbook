package com.sahil.farmsbook.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sahil.farmsbook.Views.PickupActivity;
import com.sahil.farmsbook.model.Order;
import com.sahil.farmsbook.model.Product;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;


public class SellerOrderAdapter extends RecyclerView.Adapter<SellerOrderAdapter.OrderViewHolder> {

    private ArrayList<Order> listOrders;
    Context context1;
    private String type;



    public SellerOrderAdapter(Context context, ArrayList<Order> listOrder) {
        this.context1 = context;
        this.listOrders = listOrder;

    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_pickup, parent, false);
        return new OrderViewHolder(rootView);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        final Order current = listOrders.get(position);

//        if(current.getOrderstatus().equals("Recieved")){
//            holder.navigate.setVisibility(View.GONE);
//            holder.orderTime.setVisibility(View.GONE);
//            holder.call.setVisibility(View.GONE);
//            holder.orderwork.setVisibility(View.VISIBLE);
//            holder.refuseorder.setVisibility(View.VISIBLE);
//            holder.timingtext.setVisibility(View.GONE);
//            holder.orderTime.setVisibility(View.GONE);
//        }

//        if(current.getOrderstatus().equals("Confirmed")){
//            holder.navigate.setVisibility(View.GONE);
//            holder.call.setVisibility(View.GONE);
//            holder.orderwork.setVisibility(View.GONE);
//            holder.refuseorder.setVisibility(View.GONE);
//            holder.total.setText(current.getTotal());
//            holder.orderTime.setVisibility(View.VISIBLE);
//            holder.orderTime.setText(current.getTime());
//            holder.timingtext.setVisibility(View.VISIBLE);
//        }

        if(current.getOrderstatus().equals("Processed")){
            holder.navigate.setVisibility(View.VISIBLE);
            holder.call.setVisibility(View.VISIBLE);
            holder.orderwork.setVisibility(View.VISIBLE);
            holder.refuseorder.setVisibility(View.GONE);
            holder.total.setText(current.getTotal());
//            holder.timingtext.setVisibility(View.VISIBLE);
            holder.orderTime.setVisibility(View.VISIBLE);

            holder.orderTime.setText(current.getTime());

        }

        if(current.getOrderstatus().equals("Completed")){
            holder.navigate.setVisibility(View.GONE);
            holder.call.setVisibility(View.GONE);
            holder.orderwork.setVisibility(View.GONE);
            holder.refuseorder.setVisibility(View.GONE);
            holder.total.setText(current.getTotal());
            holder.orderTime.setText(current.getTime());
        }


        holder.orderid.setText(current.get_id());
        holder.orderstatus.setText(current.getOrderstatus());
        holder.name.setText(current.getCustomer().getFirstname() + " " + current.getCustomer().getLastname());
        holder.city.setText("City - " + current.getCustomer().getCity());
        holder.locality.setText("Locality - "+current.getLocality());
        holder.flataddress.setText(current.getCustomer().getFlataddress());
        holder.pincode.setText("Pincode - " + current.getCustomer().getPincode());

        if(current.getTime()!=null){
            holder.orderTime.setText(current.getTime());
        }

        if(current.getOrderstatus().equals("Recieved")){
            holder.orderwork.setText("Confirm Order");
        }else if(current.getOrderstatus().equals("Picked")){
            holder.orderwork.setText("Enter Delivery OTP");
        }

        String jsDate = current.getCreate_time();

        Date pickupdate=null;
        try {
            pickupdate = new SimpleDateFormat("yyyy-MM-dd").parse(jsDate);
            Log.e("date",pickupdate.toString());
            pickupdate.setHours(17);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String orderDate = current.getCreate_time();
        Date order=null;
        try {
            order = new SimpleDateFormat("yyyy-MM-dd").parse(orderDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.orderdate.setText(order.toLocaleString().substring(0,12));

        holder.phone.setText(current.getCustomer().getPhone());

        holder.call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+current.getCustomer().getPhone()));
                context1.startActivity(intent);
            }
        });
        holder.navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "https://www.google.com/maps/dir/?api=1&destination=" + current.getLatitude()+","+current.getLongitude() + "&travelmode=driving&dir_action=navigate";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context1.startActivity(intent);
            }
        });
        if(current.getOrderstatus().equals("Confirmed") ||current.getOrderstatus().equals("Processed") ){
            holder.refuseorder.setVisibility(View.GONE);
        }
        holder.refuseorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCancelDialog(v,position);
            }
        });

        holder.orderwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.getStatus().equals("Recieved")){
                    confirmOrderDialog(v, current.get_id());
                }else{
                    showEnterOTPDialog(current.get_id(),current.getStatus());
                }
            }
        });


        try {
            JSONArray jsonArray = new JSONArray(current.getOrder());
            holder.itemcount.setText(String.valueOf(jsonArray.length()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

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


        holder.refuseorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCancelDialog(v,position);
            }
        });







    }

    private void OrderwithImageView(View view, String image_url) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context1,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
////        LayoutInflater inflater = context1.getLayoutInflater();
////        View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_imageview, null);
//
//        final ImageView imageView = new ImageView(context1);
//        imageView.setImageDrawable(context1.getResources().getDrawable(R.drawable.placeholder_image));
//        Glide.with(context1).load(image_url).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setMinimumHeight(60);
//        builder.setPositiveButton("OK", null);
//        builder.setView(imageView);
//        builder.show();
//



        AlertDialog.Builder builder = new AlertDialog.Builder(context1);
        final ImageView imageView = new ImageView(context1);
        imageView.setImageDrawable(context1.getResources().getDrawable(R.drawable.placeholder_image));
        Glide.with(context1).load(image_url).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
        imageView.setMinimumHeight(1600);
        imageView.setMaxHeight(1600);
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
            Product product = new Product(jsonObject);
            arrayAdapter.add("Name - " + product.getName() + ",Brand - " + product.getBrand() + ", Quantity -" + product.getQuantity() + "Unit - "+product.getUnit()+", Price - Rs "+  product.getTotal());

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
                new CancelOrder().execute();
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
                        Toast.makeText(context1,"You clicked yes button",Toast.LENGTH_LONG).show();
                            new CancelOrder().execute(listOrders.get(position).get_id());
                            listOrders.remove(position);
                            this.notify();
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
    private void showEnterOTPDialog(final String id,final String status) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context1);
        final EditText input = new EditText(context1);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        dialogBuilder.setView(input);
        dialogBuilder.setTitle("Enter OTP");
        dialogBuilder.setMessage("Enter otp below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                Toast.makeText(context1,input.getText().toString(),Toast.LENGTH_LONG).show();
               new verifyOTP(id,"Processed",input.getText().toString()).execute();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    private void confirmOrderDialog(View view,final String orderId) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context1);
        alert.setTitle("Confirm Order");

        LinearLayout layout = new LinearLayout(context1);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText comment = new EditText(context1);
        comment.setHint("Add Comment");
        comment.setMaxLines(3);
        comment.setMinHeight(60);
        layout.addView(comment);

        final EditText price = new EditText(context1);
        price.setHint("Enter total Price");
        price.setInputType(InputType.TYPE_CLASS_NUMBER);

        layout.addView(price);


        alert.setView(layout);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String orderComment, orderPrice;
                orderComment = comment.getText().toString();
                orderPrice = price.getText().toString();

                if(orderPrice.equals("") || orderComment.equals("")){
                    Toast.makeText(context1,"Please fill all the details!",Toast.LENGTH_SHORT).show();
                    return;
                }

                new confirmOrder(orderComment,orderPrice,orderId).execute();
                Toast.makeText(context1, "Order Updation in Process..", Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

    }
    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView orderid,flataddress, timingtext,orderdate,pincode,itemcount, orderdatetext,type,orderTime,orderpickupdate, orderstatus,orderservice,city,flat,locality,phone,name,total,totalText,offerapplied;
        Button navigate,edit,orderwork,refuseorder,call, viewOrder;

        private OrderViewHolder(View itemView) {
            super(itemView);
            orderid= itemView.findViewById(R.id.orderid);
            orderdate = itemView.findViewById(R.id.orderdate);
            orderstatus = itemView.findViewById(R.id.orderstatus);
            orderwork = itemView.findViewById(R.id.orderwork);
            city = itemView.findViewById(R.id.ordercity);
            locality = itemView.findViewById(R.id.locality);
            phone = itemView.findViewById(R.id.phone);
            navigate = itemView.findViewById(R.id.navigate);
            name = itemView.findViewById(R.id.cutomer_name);
            total = itemView.findViewById(R.id.total);
            pincode = itemView.findViewById(R.id.pincode);
            viewOrder= itemView.findViewById(R.id.viewOrder);
            refuseorder = itemView.findViewById(R.id.refuse_order);
            call = itemView.findViewById(R.id.call);
            orderdatetext = itemView.findViewById(R.id.orderdatetext);
            totalText = itemView.findViewById(R.id.totaltextView);
            orderTime = itemView.findViewById(R.id.order_time);
            flataddress = itemView.findViewById(R.id.flataddress);
            itemcount = itemView.findViewById(R.id.itemcount);
            refuseorder.setOnClickListener(this);
            navigate.setOnClickListener(this);
            orderwork.setOnClickListener(this);
            viewOrder.setOnClickListener(this);
//            edit = itemView.findViewById(R.id.editOrder);
            itemView.setOnClickListener(this);
            orderstatus.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

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
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @SuppressLint("StaticFieldLeak")
    class refuseOrder extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private String orderid;

        refuseOrder(String id){
            this.orderid = id;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("id",orderid);
            params.put("user", SharedPreferenceSingleton.getInstance(context1).getString("_id", "Customer"));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (jsonObject.getString("success").equals("true")) {
                    Toast.makeText(context1,"Order has been refused", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context1,R.string.error, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                result = Server.post(context1.getResources().getString(R.string.refuseOrder), json);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("Result:" + result);
            return result;
        }
    }
    @SuppressLint("StaticFieldLeak")
    class verifyOTP extends AsyncTask<String, String, String> {
        private ProgressDialog progress;

        private String stat;
        HashMap<String,String> map = new HashMap<>();
        verifyOTP(String id, String status,String otp){
            map.put("status","Completed");
            map.put("payment_status","Complete");
            map.put("_id",id);
            map.put("delivered_otp",otp);

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(context1);
            progress.setMessage("Verifying OTP");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {

                Gson gson = new Gson();
                String json = gson.toJson(map);

                result = Server.post(context1.getResources().getString(R.string.verifyDelivery),json);



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
                    Toast.makeText(context1,"Wrong OTP entered. Please check and again Enter",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context1,"Verified OTP",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context1, PickupActivity.class);
                    context1.startActivity(intent);
                    ((Activity)context1).finish();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }



    @SuppressLint("StaticFieldLeak")
    class confirmOrder extends AsyncTask<String, String, String> {
        private ProgressDialog progress;
        HashMap<String,String> map = new HashMap<>();
        confirmOrder(String comment, String price, String orderId){
            map.put("comment",comment);
            map.put("_id",orderId);
            map.put("total",price);
            map.put("status","Confirmed");

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(context1);
            progress.setMessage("Updating Order");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {

                Gson gson = new Gson();
                String json = gson.toJson(map);
                result = Server.post(context1.getResources().getString(R.string.confirmOrder),json);

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
                if(success.equals("true")){
                    Toast.makeText(context1,"Order Updated Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context1, PickupActivity.class);
                    context1.startActivity(intent);
                    ((Activity)context1).finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }
}
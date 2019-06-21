package com.sahil.farmsbook.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.model.Product;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {
    View view;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int PLACE_PICKER_REQUEST = 1;
    MapView mMapView;
    GoogleMap mGoogleMap;
    private TextView rateCardView;
    public TextView addressContainer,mTotal, address, pincode;
    Double latitude=0.0,longitude=0.0;
    Button getPlaceButton;
    private EditText orderPickupDate;
    Button btnDatePicker, btnTimePicker,checkoutButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RadioGroup radioGroup;
    Button deliverNow, deliverLater;
    String orderId, total, num_items, time="Express",method;
    Integer totalCost=0;
    CardView timingCardView;
    ArrayList<Product> listProduct = new ArrayList<>();
    String order;
    String slot = "Today";
    RadioButton rb1, rb2;
    ImageView editAddress;
    LinearLayout discountlayout, actualcostlayout,finaltotallayout;
    TextView discount,actualtotal;
    Double numDiscount = 0.0;
    ImageView info;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CheckoutActivity.this,CartActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMapView = findViewById(R.id.mapView);
        if(mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        discount = findViewById(R.id.totalDiscount);
        actualtotal = findViewById(R.id.actualtotal);
        finaltotallayout = findViewById(R.id.finaltotal);


        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);

        deliverNow = findViewById(R.id.deliverNow);
        deliverLater = findViewById(R.id.deliverLater);
        checkoutButton = findViewById(R.id.checkoutbutton);
        editAddress = findViewById(R.id.editAddressButton);

        discountlayout = findViewById(R.id.discount);
        actualcostlayout = findViewById(R.id.actualcost);

        rb1 = findViewById(R.id.radioButton1);
        rb2 = findViewById(R.id.radioButton2);

        timingCardView = findViewById(R.id.timingCardView);
        timingCardView.setVisibility(View.GONE);

        checkoutButton.setVisibility(View.GONE);


        addressContainer = findViewById(R.id.address_container);

        mTotal = findViewById(R.id.totalCost);
//
//        orderId = getIntent().getStringExtra("orderId");
        total = getIntent().getStringExtra("total");


        //Getting the cart items
        try {
            getCart();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(Integer.parseInt(total)>100 && numDiscount>0){
            actualtotal.setText(total);
            finaltotallayout.setVisibility(View.VISIBLE);
            discountlayout.setVisibility(View.VISIBLE);
            discount.setText("Rs "+String.valueOf(numDiscount));
            mTotal.setText("Rs "+String.valueOf(Double.parseDouble(total)-numDiscount));

            total = String.valueOf(Double.parseDouble(total)-numDiscount);
        }else{
            finaltotallayout.setVisibility(View.GONE);
            discountlayout.setVisibility(View.GONE);
            actualtotal.setText("Rs "+total);
        }


        mTotal.setText("Rs " + total);


        String useraddress = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("flataddress","Flat Address Not Registered");
        String userpincode = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("pincode","Pincode not available");

        if(useraddress.equals("Flat Address Not Registered")){
           Toast.makeText(CheckoutActivity.this,"Address not provided, please fill the address in Profile Section", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CheckoutActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }


        if(userpincode.equals("Pincode not available")){
            Toast.makeText(CheckoutActivity.this,"Pincode not available, please fill the pincode in Profile Section", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CheckoutActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        address.setText(useraddress);
        pincode.setText("Pincode - " + userpincode);


        requestPermission();


        getPlaceButton = (Button) findViewById(R.id.changeAddress);

        getPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = builder.build(CheckoutActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    time = rb.getText().toString();
                    Toast.makeText(CheckoutActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        Calendar rightNow = Calendar.getInstance();
        final int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        if(hour>17){
            deliverNow.setVisibility(View.GONE);
        }

        deliverNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latitude.equals(0.0) || longitude.equals(0.0)){
                    Toast.makeText(CheckoutActivity.this, "Please add the locality", Toast.LENGTH_SHORT).show();
                    return;
                }
                slot = "Today";
                timingCardView.setVisibility(View.VISIBLE);
                if(hour>11){
                    rb1.setVisibility(View.GONE);
                }
                deliverNow.setVisibility(View.GONE);
                deliverLater.setVisibility(View.GONE);
                checkoutButton.setVisibility(View.VISIBLE);

            }
        });

        deliverLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latitude.equals(0.0) || longitude.equals(0.0)){
                    Toast.makeText(CheckoutActivity.this, "Please add the locality", Toast.LENGTH_SHORT).show();
                }
                slot = "Tomorrow";
                timingCardView.setVisibility(View.VISIBLE);
                deliverNow.setVisibility(View.GONE);
                deliverLater.setVisibility(View.GONE);
                checkoutButton.setVisibility(View.VISIBLE);

            }
        });




        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AddOrder().execute();
                OpenPaymentMethodDialog();
            }
        });



        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });



        info = findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CheckoutActivity.this,"Offer valid on Vegetables and Fruits only!!", Toast.LENGTH_LONG).show();
            }
        });


    }


    private void getCart() throws JSONException{
        Gson gson = new Gson();

        order = SharedPreferenceSingleton.getInstance(CheckoutActivity.this).getString("cart","Can't find the value");


        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> cartmap = gson.fromJson(order, type);

        Integer sub = 0;
        for (HashMap.Entry<String, String> entry : cartmap.entrySet()) {
            Product product = new Product(new JSONObject(entry.getValue()));
            listProduct.add(product);
            totalCost  = totalCost  + Integer.parseInt(product.getQuantity())*Integer.parseInt(product.getPrice());
            if(!product.getType().equals("Grain")){
                numDiscount = numDiscount+(Integer.parseInt(product.getQuantity())*Integer.parseInt(product.getPrice()))*0.2;
            }
        }



    }


    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
    private void OpenPaymentMethodDialog(){
        final String[] items = {"Cash on Delivery", "Pay by Credit, Debit Card, UPI, BHIM"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setTitle("Select Role");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                method = items[item];
                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(method.equals("Cash on Delivery")){


                                new AddOrder().execute();
                        }else{

                            new AddOrderForOnlinePayment().execute();
//                            new createPaymentRequest().execute();
                        }

                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("India").snippet("Deliver here"));
        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(16).bearing(0).tilt(45).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getApplicationContext(), data);
                addressContainer.setText(place.getName().toString() + "," + place.getAddress());
                LatLng latLng =  place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                Log.e("fsdf",String.valueOf(latitude));
                mGoogleMap.clear();
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("My Home").snippet("Pick my clothes from here"));
                CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(16).bearing(0).tilt(45).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                if (place.getAttributions() == null) {
                    Toast.makeText(getApplicationContext(),"No attribution ",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),place.getAttributions().toString(),Toast.LENGTH_LONG).show();
                }
            }
        }
    }







    @SuppressLint("StaticFieldLeak")
    class AddOrder extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            params.put("customerId",SharedPreferenceSingleton.getInstance(CheckoutActivity.this).getString("_id","Jaipur"));
            params.put("status","Received");
            JSONArray jsonArray = new JSONArray(listProduct);
            Gson gson = new Gson();
            params.put("order",gson.toJson(listProduct));
            params.put("slot",slot);
            params.put("time",time);
            params.put("total",total);
            params.put("payment_method","Cash on Delivery");
            params.put("payment_status","Incomplete");
            params.put("latitude",String.valueOf(latitude));
            params.put("longitude",String.valueOf(longitude));
            params.put("locality",addressContainer.getText().toString());
            progress=new ProgressDialog(CheckoutActivity.this);
            progress.setMessage("Creating Order..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (success) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if(jsonObject.has("data")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        if(data.has("_id")){
                            SharedPreferenceSingleton.getInstance(CheckoutActivity.this).remove("cart");
                            SharedPreferenceSingleton.getInstance(CheckoutActivity.this).remove("orderId");
                            Intent intent = new Intent(getApplicationContext(), YourOrders.class);
                            startActivity(intent);
                            finish();

                        }
                    }
                    Toast.makeText(CheckoutActivity.this, "Order Submitted..Please Select Seller!!", Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {

            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.newOrder),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            Log.e("result.....:",result);
            return result;
        }
    }







    @SuppressLint("StaticFieldLeak")
    class AddOrderForOnlinePayment extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            params.put("customerId",SharedPreferenceSingleton.getInstance(CheckoutActivity.this).getString("_id","Jaipur"));
            params.put("status","Received");
            JSONArray jsonArray = new JSONArray(listProduct);
            Gson gson = new Gson();
            params.put("order",gson.toJson(listProduct));
            params.put("slot",slot);
            params.put("time",time);
            params.put("total",total);
            params.put("payment_method","Online");
            params.put("payment_status","Incomplete");
            params.put("latitude",String.valueOf(latitude));
            params.put("longitude",String.valueOf(longitude));
            params.put("locality",addressContainer.getText().toString());
            progress=new ProgressDialog(CheckoutActivity.this);
            progress.setMessage("Creating Order..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (success) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if(jsonObject.has("data")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        if(data.has("_id")){
                            orderId  = data.getString("_id");
                            SharedPreferenceSingleton.getInstance(getApplicationContext()).put("orderId", data.getString("_id"));
                            SharedPreferenceSingleton.getInstance(CheckoutActivity.this).remove("cart");
                            SharedPreferenceSingleton.getInstance(CheckoutActivity.this).remove("orderId");
                            new createPaymentRequest().execute();

                        }
                    }
                    Toast.makeText(CheckoutActivity.this, "Order Submitted..Please Select Seller!!", Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {

            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.newOrder),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            Log.e("result.....:",result);
            return result;
        }
    }


    @SuppressLint("StaticFieldLeak")
    class createPaymentRequest extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("purpose",orderId);
            params.put("buyer_name",SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("firstname","User Not Registered"));
            params.put("amount",total);
            params.put("email",SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("email","User Not Registered"));
            params.put("phone",SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("phone","User Not Registered"));
            progress=new ProgressDialog(CheckoutActivity.this);
            progress.setMessage("Creating Payment Request..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();

            try {
                s = s.substring(1,s.length()-1);
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.has("payment_request")){

                    JSONObject paymentRequest = jsonObject.getJSONObject("payment_request");
                    String url = paymentRequest.getString("longurl");

                    new UpdatePaymentId(paymentRequest.getString("id"), url, orderId, SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("firstname","User Not Registered")).execute();


//                    Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
//                    intent.putExtra("id",paymentRequest.getString("id"));
//
//
//
//                    intent.putExtra("longurl",url);
//                    intent.putExtra("orderId",orderId);
//                    intent.putExtra("buyer_name",SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("firstname","User Not Registered"));
//
//                    startActivity(intent);

                }else{
                    Toast.makeText(CheckoutActivity.this,"Some error occured!!", Toast.LENGTH_SHORT).show();
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
                Log.e("params:",json);
                result = Server.post(getResources().getString(R.string.createPaymentRequest),json);
                result = result.replaceAll("\\\\", "");
                result = result.replaceAll("\\s+","");
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }


    @SuppressLint("StaticFieldLeak")
    class UpdatePaymentId extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        String paymentId, url , orderId, buyer_name;
        UpdatePaymentId(String paymentId, String url, String orderId, String buyer_name){
            this.paymentId = paymentId;
            this.url= url;
            this.orderId = orderId;
            this.buyer_name = buyer_name;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("_id",orderId);
            params.put("paymentId",paymentId);
            progress=new ProgressDialog(CheckoutActivity.this);
            progress.setMessage("Updating payment Id....");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (success) {

                Toast.makeText(getApplicationContext(), "Payment Id updated", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("id",paymentId);
                intent.putExtra("longurl",url);
                intent.putExtra("orderId",orderId);
                intent.putExtra("buyer_name",buyer_name);

                startActivity(intent);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                result = Server.post(getResources().getString(R.string.updatePaymentId),json);

                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }





    @SuppressLint("StaticFieldLeak")
    class updateOrder extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("_id",orderId);

            progress=new ProgressDialog(CheckoutActivity.this);
            progress.setMessage("Updating the Order..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (success) {

                new createPaymentRequest().execute();

            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.processOrder),json);

                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }






}

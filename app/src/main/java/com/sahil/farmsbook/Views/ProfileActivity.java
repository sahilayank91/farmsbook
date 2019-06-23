package com.sahil.farmsbook.Views;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.model.User;
import com.sahil.farmsbook.model.UserData;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;


public class ProfileActivity extends AppCompatActivity {
    EditText firstname,lastname;
    TextView phone,email;
    EditText city,flat, shop,locality;
    EditText pincode;
    Double latitude,longitude;
    Button save;
    private ProgressDialog progress;
    String role;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int PLACE_PICKER_REQUEST = 1;
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String role = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("role", "User Not Registered");
                if(role.equals("Customer")){
                    Intent intent = new Intent(ProfileActivity.this,CustomerActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(ProfileActivity.this,SellerActivity.class);
                    startActivity(intent);
                    finish();
                }


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        pincode = findViewById(R.id.pincode);
        flat = findViewById(R.id.flataddress);
        shop = findViewById(R.id.shop);
        city = findViewById(R.id.city);
        save  = findViewById(R.id.save);
        locality = findViewById(R.id.locality);
        String fname = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("firstname", "User Not Registered");
        String lname = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("lastname", "User Not Registered");

        firstname.setText(fname);
        lastname.setText(lname);


        email.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("email", "Email Not Registered"));
//        address.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("address", "Address Not Registered"));
        phone.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("phone", "Phone Not Registered"));


        role= SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("role", "User Not Registered");

        if(role.equals("Seller")){
            flat.setVisibility(View.GONE);
            shop.setVisibility(View.VISIBLE);
        }else{
            shop.setVisibility(View.GONE);
            flat.setVisibility(View.VISIBLE);
        }


        String mFlatAddress = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("flataddress", "Flat Address Not Registered");
        String mShop =  SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("shop", "Not Available");
        String mCity =SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("city", "Not Available");
        String mPincode = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("pincode", "Not Available");
        String loc = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("locality", "Locality not provided");

        if (!loc.equals("Locality not provided")) {
            locality.setText(loc);
        }

        if(mFlatAddress.equals("Flat Address Not Registered")){
            flat.setHint("Enter Flat Address");
        }else{
            flat.setText(mFlatAddress);
        }

        if(mShop.equals("Not Available")){
            shop.setHint("Enter Shop Address");
        }else{
            shop.setText(mShop);
        }
        if(mCity.equals("Not Available")){
            city.setHint("Enter City");
        }else{
            city.setText(mCity);
        }

        if(mPincode.equals("Not Available")){
            pincode.setHint("Enter Shop Address");
        }else{
            pincode.setText(mPincode);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateUser().execute();
            }
        });
    }




    @SuppressLint("StaticFieldLeak")
    class UpdateUser extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(ProfileActivity.this);
            progress.setMessage("Updating your Details");
            progress.show();
            params.put("_id",SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("_id", "User Not Registered"));
            params.put("firstname", firstname.getText().toString());
            params.put("lastname", lastname.getText().toString());
            params.put("pincode",pincode.getText().toString());
            params.put("locality",locality.getText().toString());

            if(role.equals("Seller")){
                params.put("shop",shop.getText().toString());
            }else{
                params.put("flataddress",flat.getText().toString());
            }

            params.put("city",city.getText().toString());
            params.put("latitude",String.valueOf(latitude));
            params.put("longitude",String.valueOf(longitude));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (true) {
//                Toast.makeText(getApplicationContext(), R.string.update_success, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();

                new GetUser(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("_id", "User Not Registered")).execute();

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
                result = Server.post(getResources().getString(R.string.updateUser),json);
                Log.e("result",result);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetUser extends AsyncTask<String, String, String> {

        private ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(ProfileActivity.this);
            progress.setMessage("Updating local Database...");
//            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();

        }

        private final String mId;
        HashMap<String,String> map = new HashMap<>();
        private Boolean success = false;
        GetUser(String id) {
            mId = id;
            map.put("_id",mId);
        }

        @Override
        protected String doInBackground(String... strings) {
            // TODO: attempt authentication against a network service.
            String result="";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(map);
                result = Server.post(getResources().getString(R.string.loginById),json);
                success = true;
                UserData.getInstance(getApplicationContext()).initUserData(new User(new JSONObject(result)), getApplicationContext());
                return result;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
            // TODO: register the new account here.

        }

        @Override
        protected void onPostExecute(String s) {
            progress.dismiss();

            super.onPostExecute(s);
            if (success) {
                Toast.makeText(getApplicationContext(), R.string.update_success, Toast.LENGTH_LONG).show();
                if(role.equals("Customer")){
                    Intent intent = new Intent(ProfileActivity.this, CustomerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(ProfileActivity.this, SellerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

            } else {
                //REMOVE THIS AS TESTING IS OVER
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
            }
        }



    }








}

package com.sahil.farmsbook.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.utilities.Server;

public class SellerRegisterActivity extends AppCompatActivity {

    AutoCompleteTextView mFirstname, mLastname, mUserGST,mEmail, mPhone,mPassword,mUserAddress,mUserPincode,mConfirmPassword,mUserShopNo, mUserLandline, mUserBankAccountNumber, mUserConfirmBankAccount, mUserIfsc;
    String firstname,lastname, password,useremail, userphone, useraddress,usercity,userpincode,confirmpass;
    String landline, shop, bankaccount, confirmbankaccount,ifsc,gender,gst;
    Button submit;
    AutoCompleteTextView mUserCity;
    private RadioGroup radioGroup;
    private ProgressDialog progress;
    TextView terms;

    CheckBox checkBox;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SellerRegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SellerRegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirstname = findViewById(R.id.firstname);
        mLastname = findViewById(R.id.lastname);
        mEmail = findViewById(R.id.useremail);
        mPhone = findViewById(R.id.userphone);
        mPassword = findViewById(R.id.userpassword);
        mUserAddress = findViewById(R.id.useraddress);
        mUserCity = findViewById(R.id.city);
        mUserPincode = findViewById(R.id.pincode);
        mConfirmPassword = findViewById(R.id.confirmpassword);
        mUserShopNo = findViewById(R.id.userShopNumber);
        mUserLandline = findViewById(R.id.userlandline);
        mUserBankAccountNumber = findViewById(R.id.userBankAccount);
        mUserConfirmBankAccount = findViewById(R.id.userBankAccountConfirm);
        mUserIfsc = findViewById(R.id.userBankIFSC);
        mUserGST = findViewById(R.id.userGSTNumber);
        submit = findViewById(R.id.register);
        terms = findViewById(R.id.terms);
        checkBox = findViewById(R.id.checkBox);



        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerRegisterActivity.this,TermsAndCondition.class);
                intent.putExtra("type","Seller");
                startActivity(intent);
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
                    gender = rb.getText().toString();
                    Toast.makeText(SellerRegisterActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBox.isChecked()) {


                firstname = mFirstname.getText().toString();
                lastname = mLastname.getText().toString();
                useremail = mEmail.getText().toString();
                userphone = mPhone.getText().toString();
                password = mPassword.getText().toString();
                useraddress = mUserAddress.getText().toString();
                usercity = mUserCity.getText().toString();
                userpincode = mUserPincode.getText().toString();
                confirmpass = mConfirmPassword.getText().toString();
                landline = mUserLandline.getText().toString();
                shop = mUserShopNo.getText().toString();
                bankaccount = mUserBankAccountNumber.getText().toString();
                confirmbankaccount = mUserConfirmBankAccount.getText().toString();
                ifsc = mUserIfsc.getText().toString();
                gst = mUserGST.getText().toString();

                if(TextUtils.isEmpty(userphone)){
                        mPhone.setError("Please enter your Phone No");
                        return;
                }
                // Check for a valid password, if the user entered one.
                if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                    mPassword.setError(getString(R.string.error_invalid_password));
                    return;
                }
                // Check for a valid password, if the user entered one.
                if (!TextUtils.isEmpty(useremail) && !isPasswordValid(useremail)) {
                    mEmail.setError(getString(R.string.error_invalid_email));
                    return;
                }
                if (Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {
//                    startPhoneNumberVerification("+91" + mPhone.getText().toString());


                } else {
                    Toast.makeText(SellerRegisterActivity.this, "Please enter valid Email", Toast.LENGTH_LONG).show();
                }


                if (!password.equals(confirmpass)) {
                    Toast.makeText(SellerRegisterActivity.this, "Password doesn't match", Toast.LENGTH_LONG).show();
                    return;
                } else if (firstname == null || lastname == null || gst == null || useremail == null || userphone == null || usercity == null || userpincode == null || gender == null || shop == null || bankaccount == null || ifsc == null || landline == null) {
                    Toast.makeText(SellerRegisterActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!bankaccount.equals(confirmbankaccount)) {
                    Toast.makeText(SellerRegisterActivity.this, "Bank Account doesn't match", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    new checkIfUserExist(userphone).execute();
                }
            } else{
                    Toast.makeText(SellerRegisterActivity.this,"Please accept the terms and conditions",Toast.LENGTH_SHORT).show();
                }
            }

        });


    }
    private boolean validatePhoneNumber() {
        String phoneNumber = mPhone.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhone.setError("Invalid phone number.");
            return false;
        }

        return true;
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    @SuppressLint("StaticFieldLeak")
    class RegisterUser extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(SellerRegisterActivity.this);
            progress.setMessage("Registering");
            progress.setIndeterminate(true);
            progress.show();

            params.put("firstname", firstname);
            params.put("lastname", lastname);
            params.put("address", useraddress);
            params.put("password", password);
            params.put("phone","+91"+userphone);
            params.put("email",useremail);
            params.put("pincode",userpincode);
            params.put("city",usercity);
            params.put("role","Seller");
            params.put("bankaccount",bankaccount);
            params.put("ifsc",ifsc);
            params.put("shop",shop);
            params.put("landline",landline);
            params.put("gst",gst);

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            //getTime() returns the current date in default time zone
            Date date = calendar.getTime();
            int day = calendar.get(Calendar.DATE);
            //Note: +1 the month for current month
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            params.put("day",String.valueOf(day));
            params.put("month",String.valueOf(month));
            params.put("year",String.valueOf(year));

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.has("success")){
                    if(jsonObject.getBoolean("success")){

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success) {
                Toast.makeText(getApplicationContext(), R.string.reg_success, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SellerRegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.register),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class checkIfUserExist extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        ProgressDialog progress;
        private String phone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(SellerRegisterActivity.this);
            progress.setMessage("Checking User..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
            params.put("phone", phone);
        }

        checkIfUserExist(String phone) {
            this.phone = "+91" + phone;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);

                Log.e("result", s);
                if (jsonObject.has("success")) {
                    if (jsonObject.getString("success").equals("true")) {
                        Toast.makeText(SellerRegisterActivity.this, "User Already Exist!!", Toast.LENGTH_SHORT).show();
                    } else {
                        new RegisterUser().execute();
                    }
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
                result = Server.post(getResources().getString(R.string.checkIfUserExist), json);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Result:" + result);
            return result;
        }
    }

}

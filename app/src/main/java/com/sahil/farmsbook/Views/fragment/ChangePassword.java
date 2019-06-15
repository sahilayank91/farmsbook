package com.sahil.farmsbook.Views.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.Views.LoginActivity;
import com.sahil.farmsbook.utilities.Server;


public class ChangePassword extends Activity {


    EditText newpass,confirmpass;
    Button changepasswordbutton;
    TextView backbutton;
    String newpassword,confirmpassword;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(ChangePassword.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        newpass = findViewById(R.id.newpass);
        confirmpass  = findViewById(R.id.confirmpass);
        changepasswordbutton = findViewById(R.id.changepasswordbutton);
        backbutton = findViewById(R.id.backbutton);


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        changepasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 newpassword = newpass.getText().toString();
                 confirmpassword = confirmpass.getText().toString();


                if(newpassword.equals(confirmpassword)){
                    new ChangePass().execute();
                }


            }
        });



    }



    class ChangePass extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("newpass",newpassword);
//            params.put("phone",)

            progress=new ProgressDialog(ChangePassword.this);
            progress.setMessage("Registering..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (success) {
                Toast.makeText(ChangePassword.this, R.string.change_password_success, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(ChangePassword.this, R.string.error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.changePasswordUsingPhone),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }
}

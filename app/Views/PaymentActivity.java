package com.sahil.farmsbook.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.utilities.Server;

public class PaymentActivity extends AppCompatActivity {

    String orderId;
    String paymentId;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PaymentActivity.this,YourOrders.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(PaymentActivity.this,YourOrders.class);
        startActivity(intent);
        finish();
    }

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setTitle("Payments");
        WebView webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String longurl = getIntent().getStringExtra("longurl");
        orderId = getIntent().getStringExtra("orderId");
        paymentId = getIntent().getStringExtra("id");
        webView.loadUrl(longurl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://172.21.4.98:3000/service/payment/approveRequest/")) {
                    // magic
                    new approvePayment().execute();
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progress=new ProgressDialog(PaymentActivity.this);
                progress.setMessage("Loading Page..");
                progress.setIndeterminate(true);
                progress.setProgress(0);
                progress.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.dismiss();
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    class approvePayment extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("orderId",orderId);
            params.put("paymentId",paymentId);
            params.put("status","Processed");
            params.put("payment_status","Completed");

            progress=new ProgressDialog(PaymentActivity.this);
            progress.setMessage("Approving Payment..");
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
                if(jsonObject.has("success")){

                    if(jsonObject.getString("success").equals("true")){
                        Intent intent = new Intent(PaymentActivity.this,CompletedOrder.class);
                        startActivity(intent);
                        finish();
                    }

                }else{
                    Toast.makeText(PaymentActivity.this,"Error occured in updating the status of the order!!. Please contact for any query", Toast.LENGTH_SHORT).show();
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
                result = Server.post(getResources().getString(R.string.approvePayment),json);

                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }

}

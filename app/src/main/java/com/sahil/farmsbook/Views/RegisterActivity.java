package com.sahil.farmsbook.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.utilities.Server;


public class RegisterActivity extends AppCompatActivity {

    AutoCompleteTextView mFirstname, mLastname, mEmail, mPhone,mPassword,mUserFlat,mUserAddress,mUserPincode,mConfirmPassword;
    String firstname,lastname, password,useremail, userphone, useraddress,userflataddress,usercity,userpincode,confirmpass;
    Button submit;
    AutoCompleteTextView mUserCity;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int PLACE_PICKER_REQUEST = 1;

    private RadioGroup radioGroup;


    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog progress;
    private Double latitude,longitude;
    private String gender;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirstname = findViewById(R.id.firstname);
//        mLastname = findViewById(R.id.lastname);
//        mEmail = findViewById(R.id.useremail);
        mPhone = findViewById(R.id.userphone);
        mPassword = findViewById(R.id.userpassword);
//        mUserAddress = findViewById(R.id.useraddress);
        mUserCity = findViewById(R.id.city);
//        mUserPincode = findViewById(R.id.pincode);
        mConfirmPassword = findViewById(R.id.confirmpassword);
        submit = findViewById(R.id.register);

        setSuggestions();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firstname = mFirstname.getText().toString();
//                lastname = mLastname.getText().toString();
//                useremail = mEmail.getText().toString();
                userphone = mPhone.getText().toString();
                password = mPassword.getText().toString();
//                useraddress = mUserAddress.getText().toString();
                usercity = mUserCity.getText().toString();
//                userpincode = mUserPincode.getText().toString();
                confirmpass = mConfirmPassword.getText().toString();

                Log.e("fasdjfsdfasjfsaklfja",userphone);

                if(TextUtils.isEmpty(userphone)){
                    mPhone.setError("Please enter your Phone No");
                    return;
                }
                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                    mPassword.setError(getString(R.string.error_invalid_password));
                    return;
                }
                // Check for a valid password, if the user entered one.
//                if (TextUtils.isEmpty(useremail) && !isPasswordValid(useremail)) {
//                    mEmail.setError(getString(R.string.error_invalid_email));
//                    return;
//                }
//                if (TextUtils.isEmpty(useraddress)) {
//                    mUserAddress.setError("Enter Address");
//                    return;
//                }
                if(firstname==null || userphone==null ){
                    Toast.makeText(RegisterActivity.this,"Please fill all the details",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password.equals(confirmpass)){
                    Toast.makeText(RegisterActivity.this,"Password doesn't match",Toast.LENGTH_LONG).show();
                    return;
                }

                if (!validatePhoneNumber()) {
                    Toast.makeText(RegisterActivity.this,"Please enter valid Details",Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {

                    new checkIfUserExist(userphone).execute();

                }

//                if(Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {

//                    startPhoneNumberVerification("+91" + mPhone.getText().toString());
//                    new RegisterUser().execute();

//                    List<String> pincode = Arrays.asList(getResources().getStringArray(R.array.pincode_array));
//                    if(!pincode.contains(userpincode)){
//                        Toast.makeText(RegisterActivity.this,"Pincode does not exist in the area",Toast.LENGTH_SHORT).show();
//
//                    }else{
//                        new checkIfUserExist(userphone).execute();
//
//                    }

//                    Log.e("fasdjfsdfasjfsaklfja",userphone);


//                }else{
//                    Toast.makeText(RegisterActivity.this,"Please enter valid Email",Toast.LENGTH_LONG).show();
//                }

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
                    Toast.makeText(RegisterActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });






        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
//                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhone.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };



    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]


        progress=new ProgressDialog(RegisterActivity.this);
        progress.setMessage("Verifying Phone No..");
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
//        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]


    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button

                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                Toast.makeText(RegisterActivity.this,"Mobile No Validation Failed!! Please check your Phone No",Toast.LENGTH_LONG).show();
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                progress.setTitle("Registering");
                new RegisterUser().execute();

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }


    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhone.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhone.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progress!=null)
        progress.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progress!=null)
        progress.dismiss();
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }





    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
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



    @SuppressLint("StaticFieldLeak")
    class RegisterUser extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(RegisterActivity.this);
            progress.setMessage("Registering");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();

            params.put("firstname", firstname);
//            params.put("lastname", lastname);
//            params.put("flataddress", useraddress);
            params.put("password", password);
            params.put("phone","+91"+userphone);
//            params.put("email",useremail);
//            params.put("pincode",userpincode);
            params.put("city",usercity);
            params.put("role","Customer");
////            params.put("gender",gender);
//
//            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
//            //getTime() returns the current date in default time zone
//            Date date = calendar.getTime();
//            int day = calendar.get(Calendar.DATE);
//            //Note: +1 the month for current month
//            int month = calendar.get(Calendar.MONTH) + 1;
//            int year = calendar.get(Calendar.YEAR);
//            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
//            params.put("day",String.valueOf(day));
//            params.put("month",String.valueOf(month));
//            params.put("year",String.valueOf(year));

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                if(jsonObject.has("success")){
                    if(jsonObject.getBoolean("success")){
                        Toast.makeText(getApplicationContext(), R.string.reg_success, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else{
//                        Toast.makeText(getApplicationContext(),"Email A", Toast.LENGTH_LONG).show();

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
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.register),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }
    }

    void setSuggestions() {

        // Getting the string array from strings.xml
        String items[] = getResources().getStringArray(R.array.city);

        // New Arrays list for storing items
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < items.length; i++) {

            // Adding items to arary list
            list.add(items[i]);
        }

        // Adapter for holding the data view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                RegisterActivity.this, android.R.layout.simple_list_item_1, list);

        // Specify the minimum type of characters before drop-down list is shown

        mUserCity.setThreshold(1);
        mUserCity.scrollBy(30,20);
        // Setting adapter to both textviews
        mUserCity.setAdapter(adapter);




    }

    @SuppressLint("StaticFieldLeak")
    class checkIfUserExist extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        ProgressDialog progress;
        private String phone,email, password;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(RegisterActivity.this);
            progress.setMessage("Checking User..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
            params.put("phone",phone);
            params.put("email",useremail);
        }

        checkIfUserExist(String phone){
            this.phone = "+91" + phone;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);

                Log.e("result",s);
                if(jsonObject.has("success")){
                    if(jsonObject.getString("success").equals("true")){
                        Toast.makeText(RegisterActivity.this,"User Already Exist!!",Toast.LENGTH_SHORT).show();

                    }else{
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
                result = Server.post(getResources().getString(R.string.checkIfUserExist),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }

}

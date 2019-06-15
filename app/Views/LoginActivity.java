package com.sahil.farmsbook.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.model.User;
import com.sahil.farmsbook.model.UserData;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {
    String role;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private TextView signuptext,terms,forgotpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        setContentView(R.layout.activity_login);
        try {
            if (UserData.getInstance(getApplicationContext()).getUserData(this)) {


                String role =  SharedPreferenceSingleton.getInstance(LoginActivity.this).getString("role","User Not Registered");

                if(role.equals("Customer")){
                    Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(getApplicationContext(), SellerActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
//            }else{
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        signuptext = findViewById(R.id.signup_text);
        signuptext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        terms = findViewById(R.id.terms);
        terms.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,TermsAndCondition.class);
                intent.putExtra("type","Login");
                startActivity(intent);

            }
        });

        forgotpassword = findViewById(R.id.forgetpassword);
        forgotpassword.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });




    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isPhoneValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_phone));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private boolean isPhoneValid(String phone){
        return phone.length()==10;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });


        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
      class UserLoginTask extends AsyncTask<String, String, String> {

        private ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(LoginActivity.this);
            progress.setMessage("Logging In User..");
//            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();

        }

        private final String mPhone;
        private final String mPassword;
        HashMap<String,String> map = new HashMap<>();
        private Boolean success = false;
        UserLoginTask(String phone, String password) {
            mPhone = "+91"+phone;
            mPassword = password;
            map.put("phone",mPhone);
            map.put("password",mPassword);
        }

        @Override
        protected String doInBackground(String... strings) {
            // TODO: attempt authentication against a network service.
            String result="";
            try {
                // Simulate network access.
                Gson gson = new Gson();
                String json = gson.toJson(map);
                result = Server.post(getResources().getString(R.string.login),json);
                success = true;
                return result;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
            // TODO: register the new account here.

        }

        @Override
        protected void onPostExecute(String s) {
            progress.dismiss();
            mAuthTask = null;
            super.onPostExecute(s);
            Log.e("result",s);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                if(jsonObject.has("success")){
                    if(jsonObject.getBoolean("success")){


                        JSONObject object = jsonObject.getJSONObject("data");
                        UserData.getInstance(getApplicationContext()).initUserData(new User(object), getApplicationContext());


                        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            if (success) {

            } else {
                //REMOVE THIS AS TESTING IS OVER
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();

            }
        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }






















    private void OpenRoleAlertDialog(){
        final String[] items = {"Seller", "Customer"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Select Role");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                role = items[item];
                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(role.equals("Seller")){
                            Intent intent = new Intent(LoginActivity.this, SellerRegisterActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            startActivity(intent);
                            finish();
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
}


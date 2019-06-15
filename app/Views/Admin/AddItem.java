package com.sahil.farmsbook.Views.Admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.Views.SellerActivity;
import com.sahil.farmsbook.utilities.Server;

public class AddItem extends AppCompatActivity implements View.OnClickListener{
    AutoCompleteTextView inp_name, inp_two, inp_fifty, inp_kg,inp_brand, inp_name_hindi;
    String name, price, brandName, type, imageurl, hindiname, twofifty, fivehundred, onekg;
    Button submit;
    ImageView imageView;

    private Uri filePath;
    //Buttons
    private Button buttonChoose;
    private Button buttonUpload;
    private static final int PICK_IMAGE_REQUEST = 234;
    private StorageReference storageReference;


    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inp_name = findViewById(R.id.name);
        inp_name_hindi = findViewById(R.id.name_hindi);
        inp_brand = findViewById(R.id.brandName);
        inp_two = findViewById(R.id.twofifty);
        inp_fifty = findViewById(R.id.fivehundred);
        inp_kg = findViewById(R.id.onekg);

        imageView = findViewById(R.id.imageView);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);


        storageReference = FirebaseStorage.getInstance().getReference();
        submit = findViewById(R.id.submit);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = inp_name.getText().toString();
                twofifty = inp_two.getText().toString();
                fivehundred = inp_fifty.getText().toString();
                onekg = inp_kg.getText().toString();
                brandName = inp_brand.getText().toString();
                hindiname = inp_name_hindi.getText().toString();
                String url;
                switch (type) {
                    case "Vegetable":
                        url = "items/vegetable";
                        break;
                    case "Fruit":
                        url = "items/fruit";
                        break;
                    default:
                        url = "items/grain";
                        break;
                }
                uploadImage(url);
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
                    type = rb.getText().toString();
                    Toast.makeText(AddItem.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonChoose) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == buttonUpload) {

        }
    }

    private void uploadImage(String url) {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final StorageReference riversRef = storageReference.child(url+ filePath.getLastPathSegment());
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.e("Tuts+", "uri: " + uri.toString());
                                    imageurl= uri.toString();


                                    //Add item to the database

                                    progressDialog.dismiss();
                                    new AddProduct().execute();


                                    //Handle whatever you're going to do with the URL here
                                }
                            });

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
            Toast.makeText(AddItem.this,"Error occured!!",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class AddProduct extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            params.put("twofiftygram",twofifty);
            params.put("fivehundredgram",fivehundred);
            params.put("onekg",onekg);
            params.put("name",name);
            params.put("unit","Kg");
            params.put("type",type);
            params.put("hindiname",hindiname);
            params.put("imageurl",imageurl);
            params.put("brand",brandName);

            progress=new ProgressDialog(AddItem.this);
            progress.setMessage("Adding Product..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (success) {

                Toast.makeText(AddItem.this, "Product Successfully Uploaded", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddItem.this, SellerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(AddItem.this, R.string.error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.newProduct),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }
    }
}

package com.sahil.farmsbook.Views;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.model.UserData;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class PreLogin extends Activity {

    Button existing, newuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);
        existing = findViewById(R.id.existing);
        newuser = findViewById(R.id.newuser);
        try {
            if (UserData.getInstance(getApplicationContext()).getUserData(this)) {
                    Intent intent  = new Intent(PreLogin.this,LoginActivity.class);
                    startActivity(intent);
                    finish();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreLogin.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreLogin.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}

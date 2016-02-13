package com.example.exito.uptestnativeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Ankit on 05-02-2016.
 */
public class SplaceSignUpActivity extends AppCompatActivity{

    private TextView txt_sign_up;
    private ImageView app_icon ,icon_rignt_arrow;
    SharedPreferences accessTokenPreferences;
    String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splace_sign_up);

        txt_sign_up = (TextView)findViewById(R.id.txt_sign_up);
        app_icon = (ImageView)findViewById(R.id.app_icon);
        icon_rignt_arrow = (ImageView)findViewById(R.id.icon_rignt_arrow);

        accessTokenPreferences = getSharedPreferences(SignInActivity.MyPref, Context.MODE_PRIVATE);
        accessToken = accessTokenPreferences.getString("AccessToken", "NA");

        if(accessToken.equals("NA")) {
            txt_sign_up.setVisibility(View.VISIBLE);
        }else{
            txt_sign_up.setVisibility(View.GONE);
        }

        icon_rignt_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(accessToken.equals("NA")) {
                    Intent intent = new Intent(SplaceSignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(SplaceSignUpActivity.this, ProductActivity.class);
                    startActivity(intent);
                }
            }
        });

        txt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplaceSignUpActivity.this ,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

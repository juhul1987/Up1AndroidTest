package com.example.exito.uptestnativeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.exito.uptestnativeapp.model.SignInModel;
import com.example.exito.uptestnativeapp.model.SignupModel;
import com.example.exito.uptestnativeapp.utils.APIResponse;
import com.example.exito.uptestnativeapp.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by exito on 05-02-2016.
 */
public class SignUpActivity extends AppCompatActivity {

    private EditText edit_name,edit_email,edit_password;
    private Button btn_sign_up_via_facebook ,btn_sign_up;
    private ImageView icon_back_arrow;
    ProgressDialog dialog;
    String Email,Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edit_email = (EditText)findViewById(R.id.edit_email);
        edit_name = (EditText)findViewById(R.id.edit_name);
        edit_password = (EditText)findViewById(R.id.edit_password);

        btn_sign_up_via_facebook = (Button)findViewById(R.id.btn_sign_up_via_facebook);
        btn_sign_up = (Button)findViewById(R.id.btn_sign_up);
        icon_back_arrow = (ImageView)findViewById(R.id.icon_back_arrow);
        dialog = new ProgressDialog(SignUpActivity.this);
        btn_sign_up_via_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = edit_email.getText().toString();
                Password = edit_password.getText().toString();

                if(Email.equals("")){
                    Toast.makeText(getApplicationContext(), "Email Address Empty!", Toast.LENGTH_LONG).show();
                }else if(Password.equals("")){
                    Toast.makeText(getApplicationContext(), "Password Empty!", Toast.LENGTH_LONG).show();
                }else{
                    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                    CharSequence inputStr = Email;

                    Pattern pattern = Pattern.compile(expression,
                            Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(inputStr);
                    if (matcher.matches()) {
                        MyTaskParams myparms = new MyTaskParams(Utils.CLIENT_ID ,Email ,Password ,Utils.Connection);
                        SignupApiTask task = new SignupApiTask();
                        task.execute(myparms);

                    } else {
                        Toast.makeText(getApplicationContext(),"Please Enter valid Email Address",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        icon_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignUpActivity.this ,SplaceSignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private class SignupApiTask extends AsyncTask<MyTaskParams, Void, APIResponse> {
        ProgressDialog pd;
        private String storeEmial_Id = null;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(SignUpActivity.this,"","");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected APIResponse doInBackground(MyTaskParams... params) {
           SignupModel signup = new SignupModel();
            signup.setClient_id(params[0].client_id);
            signup.setEmail(params[0].username);
            signup.setPassword(params[0].password);
            signup.setConnection(params[0].connection);

            String data = new GsonBuilder().create().toJson(signup);
            Log.d("SignUpFregment", "Signup Request Data:- " + data);
            APIResponse apiResponse = Utils.httpPostMethod(Utils.SIGNUP_API, data);
            Log.d("SignUpFregment","response message:- " + apiResponse.getResponseString());
            Log.d("SignUpFregment","response code:- " + apiResponse.getResponseCode());

            return apiResponse;
        }

        @Override
        protected void onPostExecute(APIResponse apiResponse) {
            if(pd.isShowing()){
                pd.cancel();
            }

            if(apiResponse.getResponseCode() == 0 || apiResponse.getResponseString().equals("")){
                Toast.makeText(getApplicationContext(), "User Already Exists!", Toast.LENGTH_LONG).show();
                return;
            }else {

                SignInModel signInModel;
                Gson gson = new GsonBuilder().create();
                signInModel = gson.fromJson(apiResponse.getResponseString(), SignInModel.class);

                if (apiResponse.getResponseCode() == 200) {
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                } else if (apiResponse.getResponseCode() > 400) {
                    String error, error_description;
                    error_description = signInModel.getError_description();
                    if (!error_description.equals("")) {
                        Toast.makeText(getApplicationContext(), error_description, Toast.LENGTH_LONG).show();
                    }
                }
            }

        }
    }
    private static class MyTaskParams {
        private String client_id;
        private String username;
        private String password;
        private String connection;


        MyTaskParams(String client_id, String username, String password,String connection) {
           this.client_id = client_id;
            this.username = username;
            this.password = password;
            this.connection = connection;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this,SplaceSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

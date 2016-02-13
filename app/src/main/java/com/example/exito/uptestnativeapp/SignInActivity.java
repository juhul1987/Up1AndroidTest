package com.example.exito.uptestnativeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.exito.uptestnativeapp.model.FacebookSignInModel;
import com.example.exito.uptestnativeapp.model.SignInModel;

import com.example.exito.uptestnativeapp.utils.APIResponse;
import com.example.exito.uptestnativeapp.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by exito on 05-02-2016.
 */
public class SignInActivity extends AppCompatActivity {
    private EditText edit_name,edit_email,edit_password;
    private Button btn_sign_in;
    private LoginButton btn_sign_in_via_facebook;
    private  ImageView icon_back_arrow;
    ProgressDialog dialog;
    public SharedPreferences accessTokenPreferences;
    public SharedPreferences.Editor accessTokenPreferencesEditor;
    public final static String MyPref = "Mypref";
    String Email,Password;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Log.v("AccessToken","AccessToken Facebook="+accessToken.getToken());
            String AccessTokenFacebook = accessToken.getToken();
            Profile profile = Profile.getCurrentProfile();
            FacebookLoginTaskParams loginTaskParams = new FacebookLoginTaskParams(Utils.CLIENT_ID,AccessTokenFacebook,Utils.Facebook_Connection,Utils.Scope);
            FacebookSignInApiTask facebookSignInApiTask = new FacebookSignInApiTask();
            facebookSignInApiTask.execute(loginTaskParams);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(SignInActivity.this);
        setContentView(R.layout.activity_sign_in);

        callbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().registerCallback(callbackManager, loginResultFacebookCallback);
//        LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile", "user_friends"));

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        Log.v("AccessToken","AccessToken="+accessToken);
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        edit_email = (EditText)findViewById(R.id.edit_email);
        edit_name = (EditText)findViewById(R.id.edit_name);
        edit_password = (EditText)findViewById(R.id.edit_password);
        icon_back_arrow = (ImageView)findViewById(R.id.icon_back_arrow);
        btn_sign_in_via_facebook = (LoginButton)findViewById(R.id.btn_sign_in_via_facebook);

        btn_sign_in = (Button)findViewById(R.id.btn_sign_in);
        dialog = new ProgressDialog(SignInActivity.this);

        btn_sign_in_via_facebook.setReadPermissions("user_friends");
        btn_sign_in_via_facebook.registerCallback(callbackManager,loginResultFacebookCallback);

        accessTokenPreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        accessTokenPreferencesEditor = accessTokenPreferences.edit();
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
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
                        MyTaskParams myparms = new MyTaskParams(Utils.CLIENT_ID,Email ,Password,"",Utils.Connection,Utils.Grant_Type,Utils.Scope,"");
                        SignInApiTask task = new SignInApiTask();
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
                Intent intent = new Intent(SignInActivity.this ,SplaceSignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private class SignInApiTask extends AsyncTask<MyTaskParams, Void, APIResponse> {
        ProgressDialog pd;
        private String storeEmial_Id = null;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(SignInActivity.this,"","");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected APIResponse doInBackground(MyTaskParams... params) {
           SignInModel signInModel = new SignInModel();
            signInModel.setClient_id(params[0].client_id);
            signInModel.setUsername(params[0].username);
            signInModel.setPassword(params[0].password);
            signInModel.setId_token(params[0].id_token);
            signInModel.setConnection(params[0].connection);
            signInModel.setGrant_type(params[0].grant_type);
            signInModel.setScope(params[0].scope);
            signInModel.setDevice(params[0].device);

            String data = new GsonBuilder().create().toJson(signInModel);
            Log.d("SignInFregment", "SignIn Request Data:- " + data);
            APIResponse apiResponse = Utils.httpPostMethod(Utils.SIGNIN_API, data);
            Log.d("SignInFregment","response message:- " + apiResponse.getResponseString());
            Log.d("SignInFregment","response code:- " + apiResponse.getResponseCode());

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
                    Toast.makeText(getApplicationContext(), "Login Successfully !!", Toast.LENGTH_LONG).show();
                    String AccessToken = signInModel.getAccess_token();
                    Log.v("AccessToken", "AccessToken= " + AccessToken);
                    accessTokenPreferencesEditor.putString("AccessToken", AccessToken);
                    accessTokenPreferencesEditor.commit();
                    Intent intent = new Intent(SignInActivity.this, ProductActivity.class);
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
        private String id_token;
        private String connection;
        private String grant_type;
        private String scope;
        private String device;

        MyTaskParams(String client_id, String username, String password,String id_token,String connection,String grant_type, String scope ,String device) {
            this.client_id = client_id;
            this.username = username;
            this.password = password;
            this.id_token = id_token;
            this.connection = connection;
            this.grant_type = grant_type;
            this.scope = scope;
            this.device = device;

        }
    }

    private class FacebookSignInApiTask extends AsyncTask<FacebookLoginTaskParams,Void,APIResponse>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(SignInActivity.this,"","");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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

                FacebookSignInModel facebookSignInModel;
                Gson gson = new GsonBuilder().create();
                facebookSignInModel = gson.fromJson(apiResponse.getResponseString(), FacebookSignInModel.class);

                if (apiResponse.getResponseCode() == 200) {
                    Toast.makeText(getApplicationContext(), "Login Successfully !!", Toast.LENGTH_LONG).show();
                    String AccessToken = facebookSignInModel.getAccess_token();
                    Log.v("AccessToken", "Facebook AccessToken= " + AccessToken);
                    accessTokenPreferencesEditor.putString("AccessToken", AccessToken);
                    accessTokenPreferencesEditor.commit();
                    Intent intent = new Intent(SignInActivity.this, ProductActivity.class);
                    startActivity(intent);
                    finish();
                } else if (apiResponse.getResponseCode() > 400) {
                    String error, error_description;
                    error_description = facebookSignInModel.getError_description();
                    if (!error_description.equals("")) {
                        Toast.makeText(getApplicationContext(), error_description, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        protected APIResponse doInBackground(FacebookLoginTaskParams... facebookLoginTaskParamses) {
            FacebookSignInModel facebookSignInModel = new FacebookSignInModel();
            facebookSignInModel.setClient_id(facebookLoginTaskParamses[0].client_id);
            facebookSignInModel.setAccess_token(facebookLoginTaskParamses[0].access_token);
            facebookSignInModel.setConnection(facebookLoginTaskParamses[0].connection);
            facebookSignInModel.setScope(facebookLoginTaskParamses[0].scope);

            String data = new GsonBuilder().create().toJson(facebookSignInModel);
            Log.d("FacebookSignIn", "SignIn Request Data:- " + data);
            APIResponse apiResponse = Utils.httpPostMethod(Utils.FACEBOOK_SIGNIN_API, data);
            Log.d("FacebookSignIn", "response message:- " + apiResponse.getResponseString());
            Log.d("FacebookSignIn", "response code:- " + apiResponse.getResponseCode());

            return apiResponse;
        }
    }

    private static class FacebookLoginTaskParams{
        private String client_id;
        private String access_token;
        private String connection;
        private String scope;

        FacebookLoginTaskParams(String client_id,String access_token,String connection,String scope){
            this.client_id = client_id;
            this.access_token = access_token;
            this.connection = connection;
            this.scope = scope;
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignInActivity.this,SplaceSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
    }
}

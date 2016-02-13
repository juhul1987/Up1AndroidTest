package com.example.exito.uptestnativeapp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by exito on 11-02-2016.
 */
public class Utils {

    public static String SIGNUP_API = "https://test1up.auth0.com/dbconnections/signup";
    public static String SIGNIN_API = "https://test1up.auth0.com/oauth/ro";
    public static String FACEBOOK_SIGNIN_API = "https://test1up.auth0.com/oauth/access_token";
    public static String CLIENT_ID = "XbXVbrA8wFoE5vmGfFLAnnGI5ddv3d77";
    public static String Connection = "Username-Password-Authentication";
    public static String Facebook_Connection = "facebook";
    public static String Grant_Type = "password";
    public static String Scope = "openid";

    public static APIResponse httpPostMethod(String apiurl,String parameter){
        HttpURLConnection httpcon;
        String url = apiurl;
        String data = parameter;
        String result = null;
        APIResponse apiResponse = new APIResponse();
        try{
            //Connect
            httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            //Write
            OutputStream os = httpcon.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.close();
            os.close();
//            if(httpcon.getResponseCode()<400) {
                //Read
                BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                result = sb.toString();

            apiResponse.setResponseCode(httpcon.getResponseCode());
            apiResponse.setResponseString(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return apiResponse;
    }
}

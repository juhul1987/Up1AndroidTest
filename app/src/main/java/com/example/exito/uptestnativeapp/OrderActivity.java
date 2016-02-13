package com.example.exito.uptestnativeapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

/**
 * Created by Ankit on 05-02-2016.
 */
public class OrderActivity extends AppCompatActivity {
    private Button btn_pay;
    private ImageView image_back_arrow;
    private TextView edit_first_name,edit_last_name,edit_card_number,edit_CVV,edit_address_line_1,edit_address_line_2,edit_city,edit_state,edit_zipcode,edit_country,edit_comment;
    private Spinner spinner_expiration_month,spinner_expiration_year,spinner_currency;
    private String result;
    private String Currency,ExpiryMonth,ExpiryYear;
    public static String Token;
    public static final String PUBLISH_KEY = "pk_test_O55MKdNtSdNRtkBaum1IGtWd";
    SharedPreferences tokenSharedPreferences;
    SharedPreferences.Editor editor;
    Card card;
    private Stripe tempStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Intent intent = getIntent();
        result = intent.getStringExtra("result");
        tempStrip = new Stripe();
        btn_pay = (Button)findViewById(R.id.btn_pay);
        btn_pay.setText("Pay "+result);
        image_back_arrow = (ImageView)findViewById(R.id.image_back_arrow);
        edit_first_name = (TextView) findViewById(R.id.edit_first_name);
        edit_last_name = (TextView) findViewById(R.id.edit_last_name);
        edit_card_number = (TextView) findViewById(R.id.edit_card_number);
        edit_CVV = (TextView) findViewById(R.id.edit_CVV);
        edit_address_line_1 = (TextView) findViewById(R.id.edit_address_line_1);
        edit_address_line_2 = (TextView) findViewById(R.id.edit_address_line_2);
        edit_city = (TextView) findViewById(R.id.edit_city);
        edit_state = (TextView) findViewById(R.id.edit_state);
        edit_zipcode = (TextView) findViewById(R.id.edit_zipcode);
        edit_country = (TextView) findViewById(R.id.edit_country);
        edit_comment = (TextView) findViewById(R.id.edit_comment);
        spinner_expiration_month = (Spinner) findViewById(R.id.spinner_expiration_month);
        spinner_expiration_year = (Spinner) findViewById(R.id.spinner_expiration_year);
        spinner_currency = (Spinner) findViewById(R.id.spinner_currency);

        tokenSharedPreferences = PreferenceManager.getDefaultSharedPreferences(OrderActivity.this);
        editor = tokenSharedPreferences.edit();

        if(tokenSharedPreferences.getString("Token","NA").equals("NA")){
            edit_first_name.setText("");
            edit_country.setText("");
            edit_zipcode.setText("");
            edit_card_number.setText("");
            edit_address_line_2.setText("");
            edit_address_line_1.setText("");
            edit_city.setText("");
            edit_state.setText("");
            spinner_currency.setSelection(0);
            spinner_expiration_month.setSelection(0);
            spinner_expiration_year.setSelection(0);
        }else{
            String Name = tokenSharedPreferences.getString("Name", "NA").trim();
            if(Name.contains(" ")) {
                String fName = Name.split(" ")[0];
                String lName = Name.split(" ")[1];
                edit_first_name.setText(fName);
                edit_last_name.setText(lName);
            }else{
                edit_first_name.setText(Name);
            }
            edit_country.setText(tokenSharedPreferences.getString("Country","NA"));
            edit_zipcode.setText(tokenSharedPreferences.getString("ZipCode","NA"));
            edit_card_number.setText(tokenSharedPreferences.getString("CardNumber","NA"));
            edit_address_line_2.setText(tokenSharedPreferences.getString("AddressLine2","NA"));
            edit_address_line_1.setText(tokenSharedPreferences.getString("AddressLine1","NA"));
            edit_city.setText(tokenSharedPreferences.getString("City", "NA"));
            edit_state.setText(tokenSharedPreferences.getString("State", "NA"));

            spinner_currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Currency = adapterView.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner_expiration_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ExpiryMonth = adapterView.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner_expiration_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ExpiryYear = adapterView.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if(spinner_currency != null){
                for (int i=0;i<spinner_currency.getCount();i++){
                    if (spinner_currency.getItemAtPosition(i).toString().equalsIgnoreCase(tokenSharedPreferences.getString("Currency", "NA"))){
                        spinner_currency.setSelection(i);
                    }
                }
            }

            if(spinner_expiration_month != null){
                for (int i=0;i<spinner_expiration_month.getCount();i++){
                    if (spinner_expiration_month.getItemAtPosition(i).toString().equals(tokenSharedPreferences.getString("ExpiryMonth", "Month"))){
                        spinner_expiration_month.setSelection(i);
                    }
                }
            }
            if(spinner_expiration_year != null){
                for (int i=0;i<spinner_expiration_year.getCount();i++){
                    if (spinner_expiration_year.getItemAtPosition(i).toString().equals(tokenSharedPreferences.getString("ExpiryYear", "Year"))){
                        spinner_expiration_year.setSelection(i);
                    }
                }
            }
        }

        image_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this ,ProductActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = OrderActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(OrderActivity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String FirstName,LastName,CardNumber,CVV,AddressLine1,AddressLine2,City,State,ZipCode,Country,Comment;

                FirstName = edit_first_name.getText().toString().trim();
                LastName = edit_last_name.getText().toString().trim();
                CardNumber = edit_card_number.getText().toString().trim();
                CVV = edit_CVV.getText().toString().trim();
                if(spinner_currency != null && spinner_currency.getSelectedItemPosition() == 2){
                    Currency = spinner_currency.getSelectedItem().toString();
                }

                if(spinner_expiration_month != null && !spinner_expiration_month.getSelectedItem().equals("")){
                    ExpiryMonth = spinner_expiration_month.getSelectedItem().toString();
                }

                if(spinner_expiration_year != null && !spinner_expiration_year.getSelectedItem().equals("")){
                    ExpiryYear = spinner_expiration_year.getSelectedItem().toString();
                }

                AddressLine1 = edit_address_line_1.getText().toString().trim();
                AddressLine2 = edit_address_line_2.getText().toString().trim();
                City = edit_city.getText().toString().trim();
                State = edit_state.getText().toString().trim();
                ZipCode = edit_zipcode.getText().toString().trim();
                Country = edit_country.getText().toString().trim();
                Comment = edit_comment.getText().toString().trim();

                if(CardNumber.equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter CardNumber",Toast.LENGTH_LONG).show();
                }else if(CVV.equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter CVV",Toast.LENGTH_LONG).show();
                }else if(spinner_expiration_month.getSelectedItemPosition() == 0){
                    Toast.makeText(getApplicationContext(),"Please Select Expiration Month",Toast.LENGTH_LONG).show();
                }else if(spinner_expiration_year.getSelectedItemPosition() == 0){
                    Toast.makeText(getApplicationContext(),"Please Select Expiration Year",Toast.LENGTH_LONG).show();
                }else{
                    card = new Card(CardNumber,Integer.valueOf(ExpiryMonth),Integer.valueOf(ExpiryYear),CVV);
                    card.setName(FirstName+ " " +LastName);
                    card.setNumber(CardNumber);
                    card.setExpMonth(Integer.valueOf(ExpiryMonth));
                    card.setExpYear(Integer.valueOf(ExpiryYear));
                    card.setCVC(CVV);
                    card.setAddressLine1(AddressLine1);
                    card.setAddressLine2(AddressLine2);
                    card.setAddressCity(City);
                    card.setAddressState(State);
                    card.setAddressZip(ZipCode);
                    card.setAddressCountry(Country);
                    card.setCurrency(Currency);

                    boolean validation = card.validateCard();
                    if (validation) {
                        final ProgressDialog progressDialog = new ProgressDialog(OrderActivity.this);
                        progressDialog.setMessage("Your Transaction is InProgress");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();

                        new Stripe().createToken(
                                card,
                                PUBLISH_KEY,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        String Token = token.getId();
                                        editor.putString("Token",Token);
                                        editor.putString("Name",card.getName());
                                        editor.putString("CardNumber",card.getNumber());
                                        editor.putString("AddressLine1",card.getAddressLine1());
                                        editor.putString("AddressLine2",card.getAddressLine2());
                                        editor.putString("City",card.getAddressCity());
                                        editor.putString("State",card.getAddressState());
                                        editor.putString("ZipCode",card.getAddressZip());
                                        editor.putString("Country", card.getAddressCountry());
                                        editor.putString("Currency", card.getCurrency());
                                        editor.putString("ExpiryMonth", String.valueOf(card.getExpMonth()));
                                        editor.putString("ExpiryYear", String.valueOf(card.getExpYear()));
                                        editor.commit();
                                        progressDialog.dismiss();
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderActivity.this);
                                        builder1.setMessage(card.getName() + " Your Transaction Successfully Completed!");
                                        builder1.setTitle("Transaction Status");
                                        builder1.setCancelable(true);

                                        builder1.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(OrderActivity.this ,ProductActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                        builder1.setNegativeButton(
                                                "Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        dialog.dismiss();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();

                                        edit_first_name.setText("");
                                        edit_last_name.setText("");
                                        edit_card_number.setText("");
                                        edit_CVV.setText("");
                                        edit_address_line_1.setText("");
                                        edit_address_line_2.setText("");
                                        edit_city.setText("");
                                        edit_state.setText("");
                                        edit_country.setText("");
                                        edit_comment.setText("");
                                        edit_zipcode.setText("");
                                        spinner_currency.setSelection(0);
                                        spinner_expiration_month.setSelection(0);
                                        spinner_expiration_year.setSelection(0);
                                    }
                                    public void onError(Exception error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else if (!card.validateNumber()) {
                        Toast.makeText(getApplicationContext(),"The card number that you entered is invalid",Toast.LENGTH_LONG).show();
                    } else if (!card.validateExpiryDate()) {
                        Toast.makeText(getApplicationContext(),"The expiration date that you entered is invalid",Toast.LENGTH_LONG).show();
                    } else if (!card.validateCVC()) {
                        Toast.makeText(getApplicationContext(),"The CVC code that you entered is invalid",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"The card details that you entered are invalid",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrderActivity.this ,ProductActivity.class);
        startActivity(intent);
        finish();
    }
}

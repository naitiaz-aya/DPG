package com.lexus.depannage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.hbb20.CountryCodePicker;

public class PhoneLoginActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    private String selected_country_code = "+212";
    private EditText phoneEdittext;
    private PinView firstPinView;
    private ConstraintLayout phoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        ccp = findViewById(R.id.ccp);
        phoneEdittext = (EditText) findViewById(R.id.editTextText);

        phoneLayout = (ConstraintLayout) findViewById(R.id.phoneLayout);
        firstPinView = (PinView) findViewById(R.id.firstPinView);
        ccp.setDefaultCountryUsingPhoneCode(212);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });


        phoneEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if( charSequence.toString().length() == 10 ){
                    Toast.makeText(PhoneLoginActivity.this, "Baraka ", Toast.LENGTH_SHORT).show();
                    phoneLayout.setVisibility(View.GONE);
                    firstPinView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        firstPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if( charSequence.toString().length() == 6 ){
                    Toast.makeText(PhoneLoginActivity.this, "Wa safi", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
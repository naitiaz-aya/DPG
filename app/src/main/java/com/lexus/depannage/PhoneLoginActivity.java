package com.lexus.depannage;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;
import com.lexus.depannage.response.ApiClient;
import com.lexus.depannage.response.ApiInterface;
import com.lexus.depannage.response.Users;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneLoginActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    private String selected_country_code;
    private EditText phoneEdittext;
    private PinView firstPinView;
    private ProgressBar progressBar;
    private ConstraintLayout phoneLayout;
    private static final int CREDENTIAL_PICKER_REQUEST = 120;

    //firebase auth
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResentToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    ApiInterface apiInterface;
    private String device_token;
    private SessionManager sessionManager;

    private static final int REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        // token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            return;
                        }
                        device_token = task.getResult().getToken();
                    }
                });


        sessionManager = new SessionManager(this);

        ccp = findViewById(R.id.ccp);
        phoneEdittext = (EditText) findViewById(R.id.editTextText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        phoneLayout = (ConstraintLayout) findViewById(R.id.phoneLayout);
        firstPinView = (PinView) findViewById(R.id.firstPinView);
        mAuth = FirebaseAuth.getInstance();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                selected_country_code = ccp.getSelectedCountryCode();
            }
        });

    //OTP Callbacks

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code  = phoneAuthCredential.getSmsCode();
                if(code != null ){
                    firstPinView.setText(code);
                    signInWithAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                phoneLayout.setVisibility(View.VISIBLE);
                firstPinView.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                mVerificationId = verificationId;
                mResentToken = token;
                Toast.makeText(PhoneLoginActivity.this, "6 digital otp sent ", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                phoneLayout.setVisibility(View.GONE);
                firstPinView.setVisibility(View.VISIBLE);
            }
        };


        String[] permission = {
                READ_PHONE_NUMBERS
        };

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        }
        ///phoneEdittext.setText(telephonyManager.getLine1Number());
        //Toast.makeText(this, telephonyManager.getLine1Number(), Toast.LENGTH_SHORT).show();


        phoneEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().length() == 9) {
                    sendOtp();
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

                if (charSequence.toString().length() == 6) {

                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, firstPinView.getText().toString().trim());
                    signInWithAuthCredential(credential);
                    Toast.makeText(PhoneLoginActivity.this, "Wa safi", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void signInWithAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){


                            Call<Users> call = apiInterface.login_register(phoneEdittext.getText().toString(),"78984564646616161646461478", device_token);
                            call.enqueue(new Callback<Users>() {
                                @Override
                                public void onResponse(Call<Users> call, Response<Users> response) {
                                    if(response.isSuccessful()){
                                        Users body = response.body();
                                        Integer code = response.code();



                                        if(body.getResponse().equals("already")){
                                            sessionManager.createSession(device_token, "user", phoneEdittext.getText().toString(), "+212");
                                            Log.e("AAA","Already");
                                            Intent i = new Intent(PhoneLoginActivity.this, HomeActivity.class);
                                            startActivity(i);
                                            finish();
                                        } else if(body.getResponse().equals("new")){
                                            sessionManager.createSession(device_token, "user", phoneEdittext.getText().toString(), "+212");
                                            Log.e("AAA","New");
                                            Intent i = new Intent(PhoneLoginActivity.this, EditUserProfileActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                        else if(body.getResponse().equals("failed")){
                                            Log.e("AAA","failed");
                                            Toast.makeText(PhoneLoginActivity.this, "Login is Failed", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(PhoneLoginActivity.this, LoginActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(PhoneLoginActivity.this, "Login was Failed", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(PhoneLoginActivity.this, LoginActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }else{
                                        Toast.makeText(PhoneLoginActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Users> call, Throwable t) {
                                    Toast.makeText(PhoneLoginActivity.this, "rja3", Toast.LENGTH_SHORT).show();
                                    Log.e("error", "Failure : " + t.toString());
                                    Intent i = new Intent(PhoneLoginActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });


                        }else{

                        }
                    }
                });
    }

    private void sendOtp() {
        progressBar.setVisibility(View.VISIBLE);
        String phoneNumber = "+212" + phoneEdittext.getText().toString();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Toast.makeText(PhoneLoginActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
        Log.e("TOKEN", device_token);

    }

    @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      switch (requestCode) {
          case REQUEST_CODE: {
              if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
              } else {
                  Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
              }
          }
      }
  }

}
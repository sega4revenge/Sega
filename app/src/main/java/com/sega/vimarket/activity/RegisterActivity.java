package com.sega.vimarket.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sega.vimarket.R;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.util.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * a
 * Created by Sega on 27/12/2016.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    Spinner spinarea;
    TextInputLayout layoutuser, layoutcode, layoutpass, layoutrepass, layoutemail, layoutphone, layoutaddress;
    EditText edtuser, edtpassword, edtrepassword, edtemail, edtphone, edtaddress, edtcode;
    Button register, confirm, cancel, cancelcode;
    TextView verifytext,countdown;
    String name,email,phone,address,area,password,repass;
    String uniqueId;
    private CountDownTimer countDownTimer;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        session = new SessionManager(getApplicationContext());
        edtuser = (EditText) findViewById(R.id.username);
        edtpassword = (EditText) findViewById(R.id.password);
        edtrepassword = (EditText) findViewById(R.id.repassword);
        edtemail = (EditText) findViewById(R.id.email);
        edtphone = (EditText) findViewById(R.id.phone);
        edtaddress = (EditText) findViewById(R.id.address);
        edtcode = (EditText) findViewById(R.id.code);
        layoutcode = (TextInputLayout) findViewById(R.id.layoutcode);
        layoutuser = (TextInputLayout) findViewById(R.id.layoutuser);
        layoutpass = (TextInputLayout) findViewById(R.id.layoutpass);
        layoutrepass = (TextInputLayout) findViewById(R.id.layoutrepass);
        layoutemail = (TextInputLayout) findViewById(R.id.layoutemail);
        layoutphone = (TextInputLayout) findViewById(R.id.layoutphone);
        layoutaddress = (TextInputLayout) findViewById(R.id.layoutaddress);

        spinarea = (Spinner) findViewById(R.id.spinarea);
        layoutcode.setVisibility(View.GONE);
        cancel = (Button) findViewById(R.id.btcancel);
        cancelcode = (Button) findViewById(R.id.btcancelcode);
        verifytext = (TextView) findViewById(R.id.verifytext);
        countdown = (TextView) findViewById(R.id.countdown);
        cancelcode.setVisibility(View.GONE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cancelcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                                                                              R.array.area, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinarea.setAdapter(adapter1);
        spinarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        register = (Button) findViewById(R.id.button);
        confirm = (Button) findViewById(R.id.buttonconfirm);
        confirm.setVisibility(View.GONE);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edtcode.getText().toString();
                if (!code.isEmpty()) {

                    finishinitphone(code);
                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            name = edtuser.getText().toString().trim();
                                             email = edtemail.getText().toString().trim();
                                             password = edtpassword.getText().toString().trim();
                                             phone = edtphone.getText().toString().trim();
                                            address = edtaddress.getText().toString().trim();
                                             area = spinarea.getSelectedItem().toString().trim();
                                             repass = edtrepassword.getText().toString().trim();
                                            if (name.isEmpty()) {
                                                edtuser.setError("Invalid User");
                                            }
                                            if (repass.isEmpty() || !repass.equals(password)) {
                                                edtrepassword.setError("Password do not match");
                                                edtpassword.setError("Password do not match");
                                            }
                                            if (password.isEmpty() || password.length() < 6) {
                                                edtpassword.setError("Password must have 6 characters");
                                            }

                                            if (email.isEmpty() || !isValidEmail(email)) {
                                                edtemail.setError("Invalid Email");
                                            }
                                            if (phone.isEmpty()) {
                                                edtphone.setError("Invalid Phone");
                                            }
                                            if (address.isEmpty()) {
                                                edtaddress.setError("Invalid Address");
                                            }
                                            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() &&
                                                    !phone.isEmpty() && !address.isEmpty() && !area.isEmpty()) {
                                                if (isValidEmail(email)) {
                                                    if (password.length() >= 6 && password.equals(repass)) {
                                                        if (phone.length() >= 10 && phone.length() <= 15) {

                                                            Log.e("Click", "OK");
                                                            registerUser(name, email, password, phone, address, area);

                                                        }
                                                    }
                                                }
                                            }

                                        }


                                    }
        );

    }

    private void registerUser(final String name, final String email,
                              final String password, final String phone, final String address,
                              final String area) {
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();
        register.setClickable(false);

        //Getting the unique id generated at firebase
        final String uniqueId = FirebaseInstanceId.getInstance().getToken();
        //Finally we need to implement a method to store this unique id to our server

        // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.d(TAG, "Register Response: " + response);
                try {
                    response = response.replaceAll("NULL", "");
                    response = response.trim();
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        layoutcode.setVisibility(View.VISIBLE);
                        confirm.setVisibility(View.VISIBLE);

                        edtuser.setVisibility(View.GONE);
                        edtpassword.setVisibility(View.GONE);
                        edtrepassword.setVisibility(View.GONE);
                        edtemail.setVisibility(View.GONE);
                        edtphone.setVisibility(View.GONE);
                        edtaddress.setVisibility(View.GONE);
                        spinarea.setVisibility(View.GONE);
                        register.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);
                        layoutuser.setVisibility(View.GONE);
                        layoutaddress.setVisibility(View.GONE);
                        layoutemail.setVisibility(View.GONE);
                        layoutpass.setVisibility(View.GONE);
                        layoutphone.setVisibility(View.GONE);
                        layoutrepass.setVisibility(View.GONE);

                        cancelcode.setVisibility(View.VISIBLE);
                        String text = getResources().getText(R.string.verifytext) +" "+ phone;
                        verifytext.setText(text);
                        startCountdownTimer();

                    }
                    else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                       errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                               error.getMessage(), Toast.LENGTH_LONG).show();
                register.setClickable(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //Opening shared preference
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(AppConfig.SHARED_PREF, 0);
                //Opening the shared preferences editor to save values
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //Storing the unique id
                editor.putString(AppConfig.UNIQUE_ID, uniqueId);
                //Saving the boolean as true i.e. the device is registered
                editor.putBoolean(AppConfig.REGISTERED, true);
                //Applying the changes on sharedpreferences
                editor.apply();
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("username", name);
                params.put("email", email);
                params.put("password", password);
                params.put("phone", phone);
                params.put("address", address);
                params.put("area", area);
                params.put("firebaseid", uniqueId);
                return params;
            }
        };
        // Adding request to request queue
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(this).requestQueue.add(strReq);
    }

    private void finishinitphone(final String otp) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.URL_VERIFY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("error");
                    System.out.println(response);
                    if (result.equals("false")) {
                     Toast.makeText(getApplicationContext(),R.string.registersusscess,Toast.LENGTH_LONG).show();
                        countDownTimer.cancel();

                     /*   Intent i = new Intent(RegisterActivity.this,ProductActivity.class);
                        startActivity(i);
                        finish();*/
                        JSONObject user = json.getJSONObject("user");
                        int userid = user.getInt("userid");
                        String username = user.getString("username");
                        String email = user.getString("email");
                        String phonenumber = user.getString("phone");

                        String address = user.getString("address");
                        String area = user.getString("area");
                        String userpic = user.getString("userpic");
                        String datecreate = user
                                .getString("datecreate");
                        String rate = user.getString("rate");
                        String count = user.getString("count");

                        SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.SHARED_PREF, MODE_PRIVATE);

                        //Opening the shared preferences editor to save values
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        //Storing the unique id
                        editor.putString(AppConfig.UNIQUE_ID, uniqueId);
                        editor.putString(AppConfig.USERID_ID, userid + "");

                        //Saving the boolean as true i.e. the device is registered
                        editor.putBoolean(AppConfig.REGISTERED, true);

                        //Applying the changes on sharedpreferencs
                        editor.apply();
                        session.setLogin(true, userid, username, uniqueId, userpic, email, rate, phonenumber, address, area);
                        // Launch main activity
                        Intent intent = new Intent(RegisterActivity.this,
                                                   ProductActivity.class);

                        startActivity(intent);
                        finish();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("otp", otp + "");

                return params;
            }
        };
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(this).requestQueue.add(strReq);
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdown.setText("Time remaining : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                countdown.setText("Click here to resend sms verification");
                countdown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerUser(name, email, password, phone, address, area);;
                    }
                });
            }
        }.start();
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

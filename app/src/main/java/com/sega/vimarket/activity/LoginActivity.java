package com.sega.vimarket.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sega.vimarket.model.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sega.vimarket.R;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.provider.SQLiteHandler;
import com.sega.vimarket.util.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sega.vimarket.util.VolleySingleton.TAG;

/**a
 * Created by Sega on 26/12/2016.
 */

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText;
    EditText passEditText;
    User userobj;
    private SessionManager session;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        db = new SQLiteHandler(this);
        session = new SessionManager(getApplicationContext());
        emailEditText = (EditText) findViewById(R.id.username);
        passEditText = (EditText) findViewById(R.id.password);
        Button btnlogin = (Button) findViewById(R.id.button);
        Button register = (Button)findViewById(R.id.buttonRegister);
        Button btnforgot = (Button)findViewById(R.id.forgot);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passEditText.getText().toString().trim();
                // Check for empty data in the form
                if (!isValidEmail(email)) {
                    //Set error message for email field
                    emailEditText.setError("Invalid Email");
                }


                if (!isValidPassword(password)) {
                    //Set error message for password field
                    passEditText.setError("Password cannot be empty");
                }

                if(isValidEmail(email)&&isValidPassword(password))
                {
                    if (!session.isLoggedIn()) {
                        //registering the device
                        checkLogin(email, password);
                    } else {
                        //if the device is already registered
                        //displaying a toast
                        Toast.makeText(getApplicationContext(),
                                       getResources().getText(R.string.notfilldata), Toast.LENGTH_LONG)
                                .show();
                    }
                }


            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
        btnforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,ForgotActivity.class);
                startActivity(i);
            }
        });
        if (session.isLoggedIn()) {
            System.out.println("vao luon");
            Intent intent = new Intent(LoginActivity.this, ProductActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkLogin(final String email, final String password) {
        //Getting the unique id generated at firebase
        final String uniqueId = FirebaseInstanceId.getInstance().getToken();

        // Tag used to cancel the request

        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        // Now store the user in SQLite
                        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();

                        //Opening shared preference


                        //Starting our listener service once the device is registered
                        JSONObject user = jObj.getJSONObject("user");
                        int userid = user.getInt("userid");
                        String username = user.getString("name");
                        String email = user.getString("email");
                        String phonenumber = user.getString("phone");
                        String address = user.getString("address");
                        String area = user.getString("area");
                        String userpic = user.getString("userpic");
                        String datecreate = user
                                .getString("datecreate");
                        String rate = user.getString("rate");
                        String count = user.getString("count");
                        userobj = new User(userid,
                                           username,
                                           email,
                                           phonenumber,
                                           address,
                                           area,
                                           userpic,
                                           datecreate,
                                           rate,
                                           count);
                        //                        Log.e(TAG,userobj.phone);
                        // Inserting row in users table
                        db.addUser(userid,
                                   username,
                                   email,
                                   phonenumber,
                                   address,
                                   area,
                                   userpic,
                                   datecreate,
                                   rate,
                                   count);
                        SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.SHARED_PREF, MODE_PRIVATE);

                        //Opening the shared preferences editor to save values
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        //Storing the unique id
                        editor.putString(AppConfig.UNIQUE_ID, uniqueId);
                        editor.putString(AppConfig.USERID_ID, String.valueOf(userobj.userid));

                        //Saving the boolean as true i.e. the device is registered
                        editor.putBoolean(AppConfig.REGISTERED, true);

                        //Applying the changes on sharedpreferences
                        editor.apply();
                        session.setLogin(true, userid, username,uniqueId,userpic);
                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                                   ProductActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putParcelable("user", userobj);
                        intent.putExtras(mBundle);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                       errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: "
                            + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                               error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("firebaseid", uniqueId);
                System.out.println(email);
                System.out.println(password);
                System.out.println(uniqueId);
                return params;
            }
        };
        // Adding request to request queue
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(this).requestQueue.add(strReq);
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password
    private boolean isValidPassword(String pass) {
        return !pass.trim().equals("");
    }
}

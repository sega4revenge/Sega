package com.sega.vimarket.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.util.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ForgotActivity extends AppCompatActivity {
    TextView verifytext, forgottext;
    EditText et_code, et_email, et_password, et_repassword;
    Button confirm, cancel, forgot, cancelcode;
    String email;
    ProgressBar progress;
    LinearLayout linear;
    TextInputLayout layoutemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        et_code = (EditText) findViewById(R.id.code);
        et_email = (EditText) findViewById(R.id.email);
        progress = (ProgressBar) findViewById(R.id.progress);
        et_password = (EditText) findViewById(R.id.pass);
        forgottext = (TextView) findViewById(R.id.forgottext);
        layoutemail = (TextInputLayout) findViewById(R.id.layoutemail);
        et_repassword = (EditText) findViewById(R.id.repass);
        confirm = (Button) findViewById(R.id.buttonconfirm);
        cancel = (Button) findViewById(R.id.btcancel);
        linear = (LinearLayout) findViewById(R.id.layoutverification);
        forgot = (Button) findViewById(R.id.button);
        cancelcode = (Button) findViewById(R.id.btcancelcode);
        verifytext = (TextView) findViewById(R.id.verifytext);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString().trim();

                // Check for empty data in the form

                progress.setVisibility(View.VISIBLE);

                initiateResetPasswordProcess(email);


            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = et_code.getText().toString();
                String password = et_password.getText().toString();
                String repassword = et_repassword.getText().toString();
                if (repassword.isEmpty() || !repassword.equals(password)) {
                    et_repassword.setError(getResources().getString(R.string.pass1));
                    et_password.setError(getResources().getString(R.string.pass1));
                }
                if (password.isEmpty() || password.length() < 6) {
                    et_password.setError(getResources().getString(R.string.pass2));
                }
                if (code.isEmpty()) {
                    et_code.setError(getResources().getString(R.string.code));
                }
                if (!code.isEmpty() && !password.isEmpty() && password.length() >= 6 && password.equals(repassword)) {

                    finishResetPasswordProcess(email, code, password);
                }
                else {

                    Snackbar.make(findViewById(R.id.base), R.string.field, Snackbar.LENGTH_LONG).show();
                }
            }
        });
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


    }

    private void initiateResetPasswordProcess(final String email) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.URL_FORGOTPASSREQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    response = response.replaceAll("NULL", "");
                    response = response.trim();
                    JSONObject json = new JSONObject(response);
                    System.out.println(response);
                    String result = json.getString("result");
                    Snackbar.make(findViewById(R.id.base), json.getString("message"), Snackbar.LENGTH_LONG).show();
                    if (result.equals(ViMarket.SUCCESS)) {
                        Snackbar.make(findViewById(R.id.base), json.getString("message"), Snackbar.LENGTH_LONG).show();
                        et_email.setVisibility(View.GONE);
                        layoutemail.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);
                        forgot.setVisibility(View.GONE);
                        forgottext.setVisibility(View.GONE);
                        linear.setVisibility(View.VISIBLE);
                        verifytext.setText(getResources().getText(R.string.verifytextforgot) + "" + email);

                    }
                    else {

                        Snackbar.make(findViewById(R.id.base), json.getString("message"), Snackbar.LENGTH_LONG).show();

                    }
                    progress.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("phone", email + "");
                return params;
            }
        };
        strReq.setTag(this.getClass().getName());
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(mRetryPolicy);
        VolleySingleton.getInstance(this).requestQueue.add(strReq);
    }

    private void finishResetPasswordProcess(final String email, final String code,
                                            final String password) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.URL_FORGOTPASSCONFIRM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("result");
                    Snackbar.make(findViewById(R.id.base), json.getString("message"), Snackbar.LENGTH_LONG).show();
                    if (result.equals(ViMarket.SUCCESS)) {
                        Snackbar.make(findViewById(R.id.base), json.getString("message"), Snackbar.LENGTH_LONG).show();
                    }
                    else {

                        Snackbar.make(findViewById(R.id.base), json.getString("message"), Snackbar.LENGTH_LONG).show();

                    }
                    progress.setVisibility(View.INVISIBLE);
                    Toast.makeText(ForgotActivity.this, R.string.changepass, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("phone", email + "");
                params.put("code", code + "");
                params.put("password", password + "");
                return params;
            }
        };
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(this).requestQueue.add(strReq);
    }

}

package com.entrolabs.apsacs;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.entrolabs.apsacs.Apikey.AuthKey;
import com.entrolabs.apsacs.Apikey.AuthKey2;
import com.entrolabs.apsacs.Apikey.AuthKey3;
import com.entrolabs.apsacs.Apikey.VolleyCallback;
import com.entrolabs.apsacs.Common.AES;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;
import com.entrolabs.apsacs.URL.UrlBase;
import com.entrolabs.apsacs.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity {
    public static final String API_KEY = "park", TOKEN = "token";
    public static final int LOGIN = 1001;
    SessionManager sessionManager;
    ActivityLoginBinding binding;

    Dialog dialog;
    private String user_name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_blue));
        }
        //  Log.e("android id",android_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            InitView();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void InitView() {
        sessionManager = new SessionManager(LoginActivity.this);
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            sessionManager.storeVal("android_id", android_id);
        }catch(Exception e){e.printStackTrace();}
        if (methodRequiresPermission(Helper.perms, 111) == true) {
            Helper.l("all permissions available");
        }
        binding.TvSignin.setOnClickListener(onClickListener);
        binding.TvForgotPassword.setOnClickListener(onClickListener);


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            //startActivity(new Intent(LoginActivity.this, SplashActivity.class));
        }
        return false;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.TvSignin:
                    String username = binding.EtUserName.getText().toString();
                    String password = binding.EtPassword.getText().toString();
                    boolean res = validate();
                    if (res) {
                        if (Helper.isNetworkAvailable(getApplicationContext())) {
                            if (methodRequiresPermission(Helper.perms, 111) == true) {
                                try {
                                    JSONObject params = new JSONObject();
                                    params.put("authentication", "true");
                                    params.put("username", username);
                                    params.put("password", password);
                                    GetDATA(1, params, dialog);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            } else {
                                Helper.t(getApplicationContext(), "some permissions are missing");
                            }

                        } else {
                            Helper.t(getApplicationContext(), "need internet connection");
                        }
                    }
                    break;
                case R.id.TvForgotPassword:
                    ShowForgotpassword();
                    break;

            }
        }
    };

    private void ShowForgotpassword() {
        Dialog _dialogg = new Dialog(LoginActivity.this, R.style.SuccessFailureDialogTheme);
        _dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogg.setCancelable(true);
        _dialogg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialogg.setContentView(R.layout.forgotpassword);
        Window window = _dialogg.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _dialogg.show();

        EditText EtForgotPasswordUsername = _dialogg.findViewById(R.id.EtForgotPasswordUsername);
        TextView TvTitle = _dialogg.findViewById(R.id.TvTitle);
        EditText EtOTP = _dialogg.findViewById(R.id.EtOTP);
        AppCompatButton BtnGetOTP = _dialogg.findViewById(R.id.BtnGetOTP);
        TextView TvResend = _dialogg.findViewById(R.id.TvResend);
        LinearLayout ch_pass_layout = _dialogg.findViewById(R.id.ch_pass_layout);
        AppCompatEditText new_pass = _dialogg.findViewById(R.id.new_pass);
        AppCompatEditText conform_pass = _dialogg.findViewById(R.id.confirm_pass);
        conform_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() != null){
                    int num = new_pass.getText().toString().length();
                    String np = new_pass.getText().toString();
                    if (s.toString().length() == num){
                        if (np.equals(s.toString())){

                        }else{
                            Helper.t(getApplicationContext(),"Password Not matched");
                        }
                    }
                }else{

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        BtnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String username = EtForgotPasswordUsername.getText().toString().trim();
                    String text = BtnGetOTP.getText().toString();
                    JSONObject params = new JSONObject();

                    if (text.equalsIgnoreCase("Get OTP")) {
                        if (username.isEmpty() || username.length() < 2) {
                            Helper.t(getApplicationContext(), "Please enter username");
                        } else {
                            user_name = username;
                            params.put("forgotPassword", "true");
                            params.put("username", username);
                            if (Helper.isNetworkAvailable(LoginActivity.this)) {
                                AuthKey3.VolleyCallBack(new VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response_) {
                                        // _dialogg.dismiss();
                                        Helper.t(getApplicationContext(), "OTP has been send to registered mobile number");
                                        TvTitle.setText("Enter OTP to create new password");
                                        EtOTP.setVisibility(View.VISIBLE);
                                        ch_pass_layout.setVisibility(View.VISIBLE);
                                        BtnGetOTP.setText("Verify OTP");
                                        EtForgotPasswordUsername.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onFailure(JSONObject apierror) {
                                        try {
                                            Helper.t(getApplicationContext(), apierror.getString("error"));
                                            //TvResend.setVisibility(View.VISIBLE);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(String message) {
                                        Helper.t(getApplicationContext(), message);
                                    }

                                    @Override
                                    public void onException(String message) {

                                        Helper.t(getApplicationContext(), message);
                                    }

                                    @Override
                                    public void onLogout(String message) {
                                        Helper.t(getApplicationContext(), message);
                                    }
                                }, UrlBase.BASE_URL, params, LoginActivity.this, "show");
                            } else {
                                Helper.t(getApplicationContext(), "Need internet connection");
                            }
                        }
                    } else if (text.equalsIgnoreCase("Verify OTP")) {
                        String otp = EtOTP.getText().toString();
                        String newPass = new_pass.getText().toString();
                        String conformPass = conform_pass.getText().toString();
                        if (otp.isEmpty()) {
                            Helper.t(getApplicationContext(), "Please enter OTP");
                        }else if (newPass.equals("") && newPass.length() == 0){
                            Helper.t(getApplicationContext(),"Please the new Password");
                        }else if (conformPass.equals("") && conformPass.length() == 0){
                            Helper.t(getApplicationContext(),"Please the conform password");
                        }else if (!newPass.equals(conformPass)){
                            Helper.t(getApplicationContext(),"Please check the password");
                        }else {
                            try {
                                JSONObject params1 = new JSONObject();
                                params1.put("checkOtp", "true");
                                params1.put("username", username);
                                params1.put("otp", otp);
                                params1.put("new_password",newPass);
                                GetDATA(3, params1, _dialogg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //Resend.....
        TvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String username = EtForgotPasswordUsername.getText().toString().trim();
                    String text = BtnGetOTP.getText().toString();
                    JSONObject params = new JSONObject();

                    if (username.isEmpty() || username.length() < 2) {
                        Helper.t(getApplicationContext(), "Please enter username");
                    } else {
                        user_name = username;
                        params.put("forgotPassword", "true");
                        params.put("username", username);
                        if (Helper.isNetworkAvailable(LoginActivity.this)) {
                            AuthKey3.VolleyCallBack(new VolleyCallback() {
                                @Override
                                public void onSuccess(JSONObject response_) {
                                    // _dialogg.dismiss();
                                    Helper.t(getApplicationContext(), "OTP has been send to registered mobile number");
                                    TvTitle.setText("Enter OTP to create new password");
                                    EtOTP.setVisibility(View.VISIBLE);
                                    BtnGetOTP.setText("Verify OTP");
                                    EtForgotPasswordUsername.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailure(JSONObject apierror) {
                                    try {
                                        Helper.t(getApplicationContext(), apierror.getString("error"));
                                        //TvResend.setVisibility(View.VISIBLE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    Helper.t(getApplicationContext(), message);
                                }

                                @Override
                                public void onException(String message) {

                                    Helper.t(getApplicationContext(), message);
                                }

                                @Override
                                public void onLogout(String message) {
                                    Helper.t(getApplicationContext(), message);
                                }
                            }, UrlBase.BASE_URL, params, LoginActivity.this, "show");
                        } else {
                            Helper.t(getApplicationContext(), "Need internet connection");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    boolean validate() {
        int error = 0;
        String errorTxt = "error", freeze_date = "";

        if (binding.EtUserName.getText().toString().trim().length() == 0) {
            errorTxt = "Username number Cannot be empty";
            error++;
        } else if (binding.EtPassword.getText().toString().trim().length() == 0) {
            errorTxt = "Password Cannot be empty";
            error++;
        }

        if (error > 0) {
            error = 0;
            Helper.t(getApplicationContext(), String.valueOf(errorTxt));
            return false;
        }
        return true;
    }


    private void GetDATA(int login, JSONObject params,Dialog dialog1) {
        if (Helper.isNetworkAvailable(LoginActivity.this)) {
            AuthKey3.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        // {"result":"success","error":"","data":[{"phc_name":"Ambajipeta","phc_code":"1973","username":"ambajipeta1973","userlevel":3}]}
                        if(login == 1) {
                            Helper.l(response_.toString());
                            sessionManager.storeVal(Helper.Apsacs_Token, response_.getString("token"));
                            sessionManager.storeVal(Helper.Apsacs_status, response_.getString("status"));

                            JSONArray data_array = response_.getJSONArray("data");
                            if (data_array.length() > 0) {
                                JSONObject inner = data_array.getJSONObject(0);
                                sessionManager.createLoginSession(inner);
                                //go to registration
                                finish();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Helper.t(getApplicationContext(), "data is empty, login failed");
                            }
                        }else if(login == 3){
                            dialog1.dismiss();
                            //ChangePassword();

                        }else if(login == 4){
                            dialog1.dismiss();
                            Helper.t(getApplicationContext(),"Password successfully changed, Login with new password");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject apierror) {
                    try {
                        Helper.t(getApplicationContext(), apierror.getString("error"));
                        if(login == 3){
                            TextView TvResend = dialog1.findViewById(R.id.TvResend);
                            Button btn = dialog1.findViewById(R.id.BtnGetOTP);
                            TvResend.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String message) {
                    Helper.t(getApplicationContext(), message);
                }

                @Override
                public void onException(String message) {
                    Helper.t(getApplicationContext(), message);
                }

                @Override
                public void onLogout(String message) {
                    Helper.t(getApplicationContext(), message);
                    sessionManager.logoutUser();
                    finish();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
            }, UrlBase.BASE_URL, params, LoginActivity.this, "show");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }

    public boolean methodRequiresPermission(String[] perms, int permission) {

        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            return true;
        } else {

            Helper.l("Requesting permissions");
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Need these permissions",
                    permission, perms);
            return false;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        // Some permissions have been granted

    }

    public void testing(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> ps = new LinkedHashMap<>();
                ps.put("0","");
                return ps;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
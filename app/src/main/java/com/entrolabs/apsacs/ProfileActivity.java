package com.entrolabs.apsacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.entrolabs.apsacs.Apikey.AuthKey2;
import com.entrolabs.apsacs.Apikey.AuthKey3;
import com.entrolabs.apsacs.Apikey.VolleyCallback;
import com.entrolabs.apsacs.Bean.TwoBean;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;
import com.entrolabs.apsacs.Common.VerhoeffAlgorithm;
import com.entrolabs.apsacs.URL.UrlBase;
import com.entrolabs.apsacs.databinding.ActivityProfileBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    SessionManager sessionManager;
    String mobilepattern = "^[6-9]{1}[0-9]{9}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_blue));
        }
        binding.toolBar.tvHeading.setText("Profile");
        InitView();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        }
        return false;
    }

    private void InitView() {
        sessionManager = new SessionManager(ProfileActivity.this);
        binding.TVUsernamesT.setText(sessionManager.getStrVal(Helper.Apsacs_Username));
        binding.TVUsernameB.setText(sessionManager.getStrVal(Helper.Apsacs_Username));
        binding.TVRole.setText(sessionManager.getStrVal(Helper.Apsacs_Rolename));
        binding.TVPhoneNumber.setText(sessionManager.getStrVal(Helper.Apsacs_Mobile));
        binding.mobileEdit.setOnClickListener(onClickListener);
        binding.passChg.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_back:
                    finish();
                    startActivity(new Intent(ProfileActivity.this, BeneficiaryScreenActivity.class));
                    break;
                case R.id.mobile_edit:
                    ShowMobile();
                    break;
                case R.id.pass_chg:
                    ShowPassword();
                    break;
                
            }
        }
    };
    
    private void GetDATA(int index, JSONObject params) {
        if (Helper.isNetworkAvailable(ProfileActivity.this)) {
            AuthKey3.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        if (index == 1){
                            
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject apierror) {
                    try {
                        Helper.t(getApplicationContext(), apierror.getString("error"));
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
            }, UrlBase.BASE_URL, params, ProfileActivity.this, "show");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }

    private void ShowMobile() {
        Dialog _dialogg = new Dialog(ProfileActivity.this, R.style.SuccessFailureDialogTheme);
        _dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogg.setCancelable(false);
        _dialogg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialogg.setContentView(R.layout.mobile_layout);
        Window window = _dialogg.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _dialogg.show();
        AppCompatEditText old_mobile = _dialogg.findViewById(R.id.ET_old_mobile);
        AppCompatEditText new_mobile = _dialogg.findViewById(R.id.ET_new_mobile);
        AppCompatEditText pass = _dialogg.findViewById(R.id.ET_password);
        AppCompatEditText ETotp = _dialogg.findViewById(R.id.ET_OTP);
        AppCompatTextView getotp =_dialogg.findViewById(R.id.BT_GETOTP);
        LinearLayout mobile_layout = _dialogg.findViewById(R.id.mobile_layout);
        LinearLayout otp_layout = _dialogg.findViewById(R.id.otp_layout);
        old_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    if (!s.toString().matches(mobilepattern)) {
                        Helper.t(getApplicationContext(), "Please enter valid contact number");
                    }
                }else if (s.length() == 1){
                    if (!s.toString().matches("^[6-9]{1}$")) {
                        Helper.t(getApplicationContext(), "Please enter valid contact number");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        new_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    if (!s.toString().matches(mobilepattern)) {
                        Helper.t(getApplicationContext(), "Please enter valid contact number");
                    }
                }else if (s.length() == 1){
                    if (!s.toString().matches("^[6-9]{1}$")) {
                        Helper.t(getApplicationContext(), "Please enter valid contact number");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getotp.getText().equals("Get OTP")) {
                        String newmobile = new_mobile.getText().toString();
                        String oldmobile = old_mobile.getText().toString();
                        String password = pass.getText().toString();
                        if (newmobile.equals("") && newmobile.length() == 0){
                            Helper.t(getApplicationContext(),"Please enter the mobile number");
                        }else if (!newmobile.matches(mobilepattern)){
                            Helper.t(getApplicationContext(),"Please enter the vaild mobile number");
                        }else if (oldmobile.equals("") && oldmobile.length() == 0){
                            Helper.t(getApplicationContext(),"Please enter the mobile number");
                        }else if (!oldmobile.matches(mobilepattern)){
                            Helper.t(getApplicationContext(),"Please enter the vaild mobile number");
                        }else if (password.equals("") && password.length() == 0){
                            Helper.t(getApplicationContext(),"Please enter the password");
                        }else {

                            JSONObject params = new JSONObject();
                            params.put("mobileChange", "true");
                            params.put("old_mobile", oldmobile);
                            params.put("new_mobile", newmobile);
                            params.put("password", password);
                            if (Helper.isNetworkAvailable(ProfileActivity.this)) {
                                AuthKey3.VolleyCallBack(new VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response_) {
                                        try {
                                            mobile_layout.setVisibility(View.GONE);
                                            otp_layout.setVisibility(View.VISIBLE);
                                            getotp.setText("Verfiy OTP");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(JSONObject apierror) {
                                        try {
                                            Helper.t(getApplicationContext(), apierror.getString("error"));
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
                                }, UrlBase.BASE_URL, params, ProfileActivity.this, "show");
                            } else {
                                Helper.t(getApplicationContext(), "Need internet connection");
                            }

                        }
                    } else {

                        String password = pass.getText().toString();
                        String otps = ETotp.getText().toString();
                        if (password.equals("") && password.length() == 0){
                            Helper.t(getApplicationContext(),"Please enter the password");
                        }else {
                            JSONObject params = new JSONObject();
                            params.put("mobileChangeOTPCheck", "true");
                            params.put("otp", otps);
                            params.put("password", password);
                            if (Helper.isNetworkAvailable(ProfileActivity.this)) {
                                AuthKey3.VolleyCallBack(new VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response_) {
                                        try {
                                            _dialogg.dismiss();
                                            sessionManager.storeVal(Helper.Apsacs_Mobile,new_mobile.getText().toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(JSONObject apierror) {
                                        try {
                                            Helper.t(getApplicationContext(), apierror.getString("error"));
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
                                }, UrlBase.BASE_URL, params, ProfileActivity.this, "show");
                            } else {
                                Helper.t(getApplicationContext(), "Need internet connection");
                            }
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    private void ShowPassword() {
        Dialog _dialogg = new Dialog(ProfileActivity.this, R.style.SuccessFailureDialogTheme);
        _dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogg.setCancelable(false);
        _dialogg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialogg.setContentView(R.layout.password_change);
        Window window = _dialogg.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _dialogg.show();
        AppCompatEditText old_password = _dialogg.findViewById(R.id.ET_old_password);
        AppCompatEditText new_password = _dialogg.findViewById(R.id.ET_new_password);
        AppCompatEditText conform_password = _dialogg.findViewById(R.id.ET_conform_password);
        AppCompatTextView getotp = _dialogg.findViewById(R.id.BT_GETOTP);
        AppCompatEditText ETotp = _dialogg.findViewById(R.id.ET_OTP);
        LinearLayout mobile_layout = _dialogg.findViewById(R.id.password_layout);
        LinearLayout otp_layout = _dialogg.findViewById(R.id.otp_layout);
        conform_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() != null){
                    int num = new_password.getText().toString().length();
                    String n_pass = new_password.getText().toString();
                    if (num== s.toString().length()){
                        if (n_pass.equals(s.toString())){

                        }else{
                            Helper.t(getApplicationContext(),"Password not matched");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getotp.getText().toString().equals("Get OTP")) {
                        String oldpass = old_password.getText().toString();
                        String newpass = new_password.getText().toString();
                        String conformpass = conform_password.getText().toString();
                        if (oldpass.equals("") && oldpass.length() == 0){
                            Helper.t(getApplicationContext(),"Please enter the old password");
                        }else if (newpass.equals("") && newpass.length() == 0){
                            Helper.t(getApplicationContext(),"Please enter the new password");
                        }else if (conformpass.equals("") && conformpass.length() == 0){
                            Helper.t(getApplicationContext(),"Please enter the conform password");
                        }else if (!newpass.equals(conformpass)){
                            Helper.t(getApplicationContext(),"Password not matched");
                        }else{
                            JSONObject params = new JSONObject();
                            params.put("changeNewPasswrordOtp", "true");
                            params.put("new_password", newpass);
                            params.put("old_password",oldpass);
                            if (Helper.isNetworkAvailable(ProfileActivity.this)) {
                                AuthKey3.VolleyCallBack(new VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response_) {
                                        try {
                                            mobile_layout.setVisibility(View.GONE);
                                            otp_layout.setVisibility(View.VISIBLE);
                                            getotp.setText("verfiy OTP");

                                            //_dialogg.dismiss();
                                            //sessionManager.storeVal(Helper.Apsacs_Mobile, new_mobile.getText().toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(JSONObject apierror) {
                                        try {
                                            Helper.t(getApplicationContext(), apierror.getString("error"));
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
                                }, UrlBase.BASE_URL, params, ProfileActivity.this, "show");
                            } else {
                                Helper.t(getApplicationContext(), "Need internet connection");
                            }
                        }
                    } else {
                        String password = new_password.getText().toString();
                        String otps = ETotp.getText().toString();
                        if (password.equals("") && password.length() == 0) {
                            Helper.t(getApplicationContext(), "Please enter the password");
                        } else {
                            JSONObject params = new JSONObject();
                            params.put("checkOtp", "true");
                            params.put("otp", otps);
                            params.put("username",sessionManager.getStrVal(Helper.Apsacs_Username));
                            params.put("new_password", password);
                            if (Helper.isNetworkAvailable(ProfileActivity.this)) {
                                AuthKey3.VolleyCallBack(new VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response_) {
                                        try {
                                            _dialogg.dismiss();
                                            //sessionManager.storeVal(Helper.Apsacs_Mobile, new_mobile.getText().toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(JSONObject apierror) {
                                        try {
                                            Helper.t(getApplicationContext(), apierror.getString("error"));
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
                                }, UrlBase.BASE_URL, params, ProfileActivity.this, "show");
                            } else {
                                Helper.t(getApplicationContext(), "Need internet connection");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
package com.entrolabs.apsacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.entrolabs.apsacs.Apikey.AuthKey;
import com.entrolabs.apsacs.Apikey.AuthKey2;
import com.entrolabs.apsacs.Apikey.AuthKey3;
import com.entrolabs.apsacs.Apikey.VolleyCallback;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;
import com.entrolabs.apsacs.URL.UrlBase;
import com.entrolabs.apsacs.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    SessionManager sessionManager;



    /*@BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.LLTotalDrawer)
    LinearLayout LLTotalDrawer;
    @BindView(R.id.left_drawer)
    LinearLayout leftDrawer;
    private DrawerLayout mDrawerLayout;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_blue));
            }
            InitView();
    }

    private void InitView() {
        sessionManager = new SessionManager(MainActivity.this);
        binding.RLBeneficiaryList.setOnClickListener(onClickListener);
        binding.RLCD.setOnClickListener(onClickListener);
        binding.RLRiskAssesment.setOnClickListener(onClickListener);
        binding.RLScreening.setOnClickListener(onClickListener);
        binding.ImgNavi.setOnClickListener(onClickListener);
        binding.ImgLogout.setOnClickListener(onClickListener);
        binding.include.LLProfile.setOnClickListener(onClickListener);

    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ImgNavi:
                    binding.drawerLayout.openDrawer(Gravity.LEFT);
                    break;
                case R.id.RLBeneficiary_List:
                    finish();
                    startActivity(new Intent(MainActivity.this,BeneficiaryScreenActivity.class));
                    break;
                case R.id.RLCD:
                    //finish();
                    //startActivity(new Intent(MainActivity.this,HIVTestingActivity.class));
                    Helper.t(getApplicationContext(),"Under progress");
                    break;
                case R.id.RLScreening:
                    finish();
                    startActivity(new Intent(MainActivity.this,ScreeningActivity.class));
                    break;
                case R.id.RLRisk_Assesment:
                    finish();
                    startActivity(new Intent(MainActivity.this,RiskASSActivity.class));
                    break;
                case R.id.ImgLogout:
                    ShowLogout();
                    break;
                case R.id.LL_Profile:
                    finish();
                    startActivity(new Intent(MainActivity.this,ProfileActivity.class));

                    break;
            }
        }
    };

    private void ShowLogout() {
        Dialog _dialogg = new Dialog(MainActivity.this, R.style.SuccessFailureDialogTheme);
        _dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogg.setCancelable(false);
        _dialogg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialogg.setContentView(R.layout.logout);
        Window window = _dialogg.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _dialogg.show();

        Button BtnLogou = _dialogg.findViewById(R.id.BtnLogou);
        BtnLogou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                _dialogg.dismiss();
//                sessionManager.logoutUser();
//                finish();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    JSONObject params = new JSONObject();
                    params.put("logout", "true");
                    params.put("username", sessionManager.getStrVal(Helper.Apsacs_Username));

                    if (Helper.isNetworkAvailable(MainActivity.this)) {
                        AuthKey3.VolleyCallBack(new VolleyCallback() {
                            @Override
                            public void onSuccess(JSONObject response_) {
                                try {
                                    _dialogg.dismiss();
                                    sessionManager.logoutUser();
                                    finish();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    Helper.l("response of messge :" + response_.toString());
                                } catch (Exception e) {
                                    Helper.t(getApplicationContext(), e.toString());
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
                            public void onLogout(String messgae) {
                                Helper.t(getApplicationContext(), messgae);
                                sessionManager.logoutUser();
                                finish();
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            }
                        }, UrlBase.BASE_URL, params, MainActivity.this, "show");
                    } else {
                        Helper.t(getApplicationContext(), "Need internet connection");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Button BtnLogoutCancel = _dialogg.findViewById(R.id.BtnLogoutCancel);
        BtnLogoutCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogg.dismiss();
            }
        });
    }

}
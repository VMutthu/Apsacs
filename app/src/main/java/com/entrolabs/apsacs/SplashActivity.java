package com.entrolabs.apsacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.RootUtil;
import com.entrolabs.apsacs.Common.SessionManager;

public class SplashActivity extends AppCompatActivity {
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_blue));
        }
        InitViews();
    }
    private void InitViews() {
        sessionManager = new SessionManager(SplashActivity.this);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isLoggedIn()) {
                    finish();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));

                } else {
                    finish();
                    // startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                }
                /*if (RootUtil.isDeviceRooted()) {
                    Helper.t(getApplicationContext(), "Device is rooted cannot proceed further");
                }
                else {
                    TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String networkOperator = tm.getNetworkOperatorName();
                    if("Android".equals(networkOperator)) {
                        // Emulator
                        Helper.t(getApplicationContext(), "Cannot run in this device");
                    }
                    else {
                        if (sessionManager.isLoggedIn()) {
                            finish();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));

                        } else {
                            finish();
                            // startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                        }
                    }

                }*/

            }
        }, 3000);

    }
}
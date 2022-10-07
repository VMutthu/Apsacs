package com.entrolabs.apsacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.entrolabs.apsacs.Adapter.BeneficiaryAdapter;
import com.entrolabs.apsacs.Apikey.AuthKey;
import com.entrolabs.apsacs.Apikey.AuthKey2;
import com.entrolabs.apsacs.Apikey.AuthKey3;
import com.entrolabs.apsacs.Apikey.VolleyCallback;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;
import com.entrolabs.apsacs.URL.UrlBase;
import com.entrolabs.apsacs.databinding.ActivityScreeningBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScreeningActivity extends AppCompatActivity {
    ActivityScreeningBinding binding;
    SessionManager sessionManager;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    BeneficiaryAdapter myadapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScreeningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_blue));
        }
        InitView();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(ScreeningActivity.this, MainActivity.class));
        }
        return false;
    }

    private void InitView() {
        try {
            sessionManager = new SessionManager(ScreeningActivity.this);
            binding.toolBar.ivBack.setOnClickListener(onClickListener);
            binding.toolBar.tvHeading.setText("Screening Activity");
            JSONObject params = new JSONObject();
            params.put("getBeneficiary", "true");
            GetDATA(1, params);
            binding.BTSearch.setOnClickListener(onClickListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_back:
                    finish();
                    startActivity(new Intent(ScreeningActivity.this, MainActivity.class));
                    break;
                case R.id.BT_search:
                    try {
                        JSONObject params = new JSONObject();
                        params.put("getBeneficiaryDetailsWithSochUID", "true");
                        params.put("soch_uid", binding.ETSochId.getText().toString());
                        GetDATA(1, params);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }

        }
    };


    private void GetDATA(int index, JSONObject params) {
        if (Helper.isNetworkAvailable(ScreeningActivity.this)) {
            AuthKey3.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        if (index == 1){
                            arrayList.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String,String> hm = new HashMap<>();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                hm.put("beneficiaryid",jsonObject.getString("beneficiaryid"));
                                hm.put("name",jsonObject.getString("name_of_hrg"));
                                hm.put("soch_uid",jsonObject.getString("soch_uid"));
                                hm.put("gender",jsonObject.getString("sex"));
                                hm.put("hrg_ti_id_no",jsonObject.getString("hrg_ti_id_no"));
                                arrayList.add(hm);
                            }
                            myadapter = new BeneficiaryAdapter(arrayList, ScreeningActivity.this, new BeneficiaryAdapter.OnItemClickListner() {
                                @Override
                                public void onItemClick(HashMap<String, String> hm) {
                                    //sessionManager.storeVal(Helper.Apsacs_soch_uid,hm.get("soch_uid"));
                                    ShowScreening(hm.get("beneficiaryid"));
                                }
                            });
                            layoutManager = new LinearLayoutManager(ScreeningActivity.this);
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            binding.RVBeneficiaryList.setLayoutManager(layoutManager);
                            binding.RVBeneficiaryList.setAdapter(myadapter);
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
            }, UrlBase.BASE_URL, params, ScreeningActivity.this, "show");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }


    private void ShowScreening(String id) {
        Dialog _dialogg = new Dialog(ScreeningActivity.this, R.style.SuccessFailureDialogTheme);
        _dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogg.setCancelable(true);
        _dialogg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialogg.setContentView(R.layout.screening_layout);
        Window window = _dialogg.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _dialogg.show();
        _dialogg.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialogg.dismiss();
            }
        });
        _dialogg.findViewById(R.id.RLCS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialogg.dismiss();
                finish();
                startActivity(new Intent(ScreeningActivity.this,ClinicalServicesActivity.class)
                        .putExtra("beneficiaryid",id));
            }
        });
        _dialogg.findViewById(R.id.RLSyphilis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialogg.dismiss();
                finish();
                startActivity(new Intent(ScreeningActivity.this,ClinicalServicesActivity2.class)
                        .putExtra("beneficiaryid",id));

            }
        });
        _dialogg.findViewById(R.id.RLHIV_Testing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialogg.dismiss();
                finish();
                startActivity(new Intent(ScreeningActivity.this,HIVTestingActivity.class)
                        .putExtra("beneficiaryid",id));

            }
        });
        _dialogg.findViewById(R.id.RLHIV_Positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialogg.dismiss();
                finish();
                startActivity(new Intent(ScreeningActivity.this,HIVTesting2Activity.class)
                        .putExtra("beneficiaryid",id));

            }
        });
    }
}
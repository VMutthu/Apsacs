package com.entrolabs.apsacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;

import com.entrolabs.apsacs.Adapter.BeneficiaryAdapter;
import com.entrolabs.apsacs.Apikey.AuthKey;
import com.entrolabs.apsacs.Apikey.AuthKey2;
import com.entrolabs.apsacs.Apikey.AuthKey3;
import com.entrolabs.apsacs.Apikey.VolleyCallback;
import com.entrolabs.apsacs.Bean.TwoBean;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;
import com.entrolabs.apsacs.URL.UrlBase;
import com.entrolabs.apsacs.databinding.ActivityBeneficiaryScreenBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeneficiaryScreenActivity extends AppCompatActivity {
    ActivityBeneficiaryScreenBinding binding;
    SessionManager sessionManager;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    BeneficiaryAdapter myadapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBeneficiaryScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InitView();
        binding.toolBar.tvHeading.setText("Beneficiary Screen");
    }

    private void InitView() {

        sessionManager = new SessionManager(BeneficiaryScreenActivity.this);
        binding.TVAddBeneficiary.setOnClickListener(onClickListener);
        binding.toolBar.ivBack.setOnClickListener(onClickListener);
        try {
            JSONObject params = new JSONObject();
            params.put("getBeneficiary", "true");
            GetDATA(1, params);
        }catch (Exception e){
            e.printStackTrace();
        }
        /*binding.ETSochId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().equals("")) {
                        if (s.toString().length() > 5) {
                        }
                    } else {

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });*/
        binding.BTSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject params = new JSONObject();
                    params.put("getBeneficiaryDetailsWithSochUID", "true");
                    params.put("soch_uid", binding.ETSochId.getText().toString());
                    GetDATA(1, params);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(BeneficiaryScreenActivity.this, MainActivity.class));
        }
        return false;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_back:
                    finish();
                    startActivity(new Intent(BeneficiaryScreenActivity.this, MainActivity.class));
                    break;
                case R.id.TV_add_Beneficiary:
                    finish();
                    startActivity(new Intent(BeneficiaryScreenActivity.this,AddBeneficiaryActivity.class));
                    break;
            }
        }
    };

    private void GetDATA(int index, JSONObject params) {
        if (Helper.isNetworkAvailable(BeneficiaryScreenActivity.this)) {
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
                            myadapter = new BeneficiaryAdapter(arrayList, BeneficiaryScreenActivity.this, new BeneficiaryAdapter.OnItemClickListner() {
                                @Override
                                public void onItemClick(HashMap<String, String> hm) {
                                    //sessionManager.storeVal(Helper.Apsacs_soch_uid,hm.get("beneficiaryid"));
                                    finish();
                                    startActivity(new Intent(BeneficiaryScreenActivity.this,AddBeneficiaryActivity.class)
                                            .putExtra("beneficiaryid",hm.get("beneficiaryid")));
                                }
                            });
                            layoutManager = new LinearLayoutManager(BeneficiaryScreenActivity.this);
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
            }, UrlBase.BASE_URL, params, BeneficiaryScreenActivity.this, "show");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }
}
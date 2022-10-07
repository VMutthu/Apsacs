package com.entrolabs.apsacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.entrolabs.apsacs.Adapter.CLickCallBack2;
import com.entrolabs.apsacs.Adapter.SelectionAdapter2;
import com.entrolabs.apsacs.Apikey.AuthKey;
import com.entrolabs.apsacs.Apikey.AuthKey2;
import com.entrolabs.apsacs.Apikey.AuthKey3;
import com.entrolabs.apsacs.Apikey.VolleyCallback;
import com.entrolabs.apsacs.Bean.TwoBean;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;
import com.entrolabs.apsacs.URL.UrlBase;
import com.entrolabs.apsacs.databinding.ActivityRiskAssementBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class RiskAssementActivity extends AppCompatActivity {
    ActivityRiskAssementBinding binding;
    SessionManager sessionManager;

    ArrayList<TwoBean> AIAPW_list= new ArrayList<>();
    ArrayList<TwoBean> site_day_list= new ArrayList<>();
    ArrayList<TwoBean> site_time_list= new ArrayList<>();
    ArrayList<TwoBean> contact_list= new ArrayList<>();
    final String[] perms = {Manifest.permission.INTERNET,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String AIAPW="",site_day="",site_time="",contact="",WCA="";
    String beneficiaryid="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiskAssementBinding.inflate(getLayoutInflater());
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
            startActivity(new Intent(RiskAssementActivity.this, MainActivity.class));
        }
        return false;
    }

    private void InitView() {
        try {
            sessionManager = new SessionManager(RiskAssementActivity.this);
            if (methodRequiresPermission(perms, 111)) {
                Helper.l("permissions granted");
                //Helper.showProgressDialog(AddBeneficiaryActivity.this);
            } else {
                Helper.t(getApplicationContext(), "Please Grant required app permissions!!");
            }
            Intent i = getIntent();
            if (i == null) {
                JSONObject params = new JSONObject();
                params.put("getInjectingACT", "true");
                GetDATA(1, params);

            } else {
                beneficiaryid = i.getStringExtra("beneficiaryid");
                JSONObject params = new JSONObject();
                params.put("getInjectingACT", "true");
                GetDATA(1, params);

            }

            binding.toolBar.ivBack.setOnClickListener(onClickListener);
            binding.toolBar.tvHeading.setText("Risk Assessment");
            binding.TVAIAPW.setOnClickListener(onClickListener);
            binding.TVSiteDay.setOnClickListener(onClickListener);
            binding.TVSiteTime.setOnClickListener(onClickListener);
            binding.TVContact.setOnClickListener(onClickListener);
            binding.ftdSubmit.setOnClickListener(onClickListener);
            binding.RGWCA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.RB_yes:
                            WCA = "1";
                            break;
                        case R.id.RB_no:
                            WCA = "2";
                            break;
                    }
                }
            });
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
                    startActivity(new Intent(RiskAssementActivity.this, MainActivity.class));
                    break;
                case R.id.TV_AIAPW:
                    if (AIAPW_list.size() > 0) {
                        SelectSelection(binding.TVAIAPW, AIAPW_list, "AIAPW");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_site_day:
                    if (site_day_list.size() > 0) {
                        SelectSelection(binding.TVSiteDay, site_day_list, "site_day");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_site_time:
                    if (site_time_list.size() > 0) {
                        SelectSelection(binding.TVSiteTime, site_time_list, "site_time");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_contact:
                    if (contact_list.size() > 0) {
                        SelectSelection(binding.TVContact, contact_list, "contact");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.ftd_submit:
                    try {
                        String hiv_data = binding.ETHIVConfirematory.getText().toString();
                        String sexwork = binding.ETSexwork.getText().toString();
                        if (hiv_data.equals("") && hiv_data.length() == 0) {
                            Helper.t(getApplicationContext(), "Please enter HIV Confirematory date");
                        } else if (sexwork.equals("") && sexwork.length() == 0) {
                            Helper.t(getApplicationContext(), "Please enter sex work");
                        } else {
                            JSONObject params = new JSONObject();
                            params.put("addOrUpdateRiskAssessment", "true");
                            params.put("beneficiaryid", beneficiaryid);
                            params.put("sexual_act_per_day", hiv_data);
                            params.put("years_in_sex_work", sexwork);
                            params.put("average_injecting_act_per_week", AIAPW);
                            params.put("when_meet_in_site", site_day);
                            params.put("time", site_time);
                            params.put("consumes_alchocal", WCA);
                            params.put("whether_dropped_out", contact);
                            params.put("droped_out_date", "");
                            params.put("dropout_reason", "");
                            params.put("id", "");
                            GetDATA(5, params);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

            }

        }
    };


    private void GetDATA(int index, JSONObject params) {
        if (Helper.isNetworkAvailable(RiskAssementActivity.this)) {
            AuthKey3.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        if (index == 1){
                            AIAPW_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                AIAPW_list.add(twoBean);
                            }

                            JSONObject params = new JSONObject();
                            params.put("getWhernMetInTheSite", "true");
                            GetDATA(2, params);
                        }else if (index == 2){
                            site_day_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                site_day_list.add(twoBean);
                            }

                            JSONObject params = new JSONObject();
                            params.put("getDroppedOut", "true");
                            GetDATA(3, params);
                        }else if (index == 3){
                            contact_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                contact_list.add(twoBean);
                            }

                            JSONObject params = new JSONObject();
                            params.put("getMetInSiteTimings", "true");
                            GetDATA(4, params);
                        }else if (index == 4){
                            site_time_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                site_time_list.add(twoBean);
                            }
                            if (!beneficiaryid.equals("")) {
                                JSONObject params = new JSONObject();
                                params.put("getRiskAssessment", "true");
                                params.put("beneficiaryid",beneficiaryid);
                                GetDATA(6, params);
                            }
                        }else if (index == 5){
                            if (response_.getString("result").equals("success")){

                                if (response_.getString("status").equals("updated successfully")){
                                    finish();
                                    startActivity(new Intent(RiskAssementActivity.this, MainActivity.class));
                                    Helper.t(getApplicationContext(), "Data Updated successful");
                                }else {
                                    Helper.t(getApplicationContext(), "Data submit successfully");
                                    finish();
                                    startActivity(new Intent(RiskAssementActivity.this, MainActivity.class));
                                }
                            }else{
                                Helper.t(getApplicationContext(),"Data not submit successfully");
                            }
                        }else if (index == 6){
                            JSONArray jsonArray = response_.getJSONArray("data");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            binding.ETHIVConfirematory.setText(jsonObject.getString("sexual_act_per_day"));
                            binding.ETSexwork.setText(jsonObject.getString("years_in_sex_work"));
                            AIAPW=jsonObject.getString("average_injecting_act_per_week");
                            setspID(AIAPW_list,binding.TVAIAPW, jsonObject.getString("average_injecting_act_per_week"));
                            site_day=jsonObject.getString("when_meet_in_site");
                            setspID(site_day_list,binding.TVSiteDay, jsonObject.getString("when_meet_in_site"));
                            site_time=jsonObject.getString("when_meet_in_site_time");
                            setspID(site_time_list,binding.TVSiteTime, jsonObject.getString("when_meet_in_site_time"));
                            contact=jsonObject.getString("whether_dropped_out");
                            setspID(contact_list,binding.TVContact, jsonObject.getString("whether_dropped_out"));
                            WCA=jsonObject.getString("consumes_alchocal");
                            if (WCA.equals("1")){
                                binding.RBYes.setChecked(true);
                            }else{
                                binding.RBNo.setChecked(true);
                            }
                            //setspID(AIAPW_list,binding.TVAIAPW, jsonObject.getString(""));


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
            }, UrlBase.BASE_URL, params, RiskAssementActivity.this, "show");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }


    public void setspID(ArrayList<TwoBean> arrayList,TextView tv,String value){
        for (TwoBean t:arrayList) {
            if (t.getId().equals(value)){
                tv.setText(t.getName());
                break;
            }
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


    //custom dialog selection
    private void SelectSelection(TextView tv_attach, ArrayList<TwoBean> list_, String  selection) {

        Dialog _dialogg = new Dialog(RiskAssementActivity.this, R.style.SuccessFailureDialogTheme);
        _dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogg.setCancelable(true);
        _dialogg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialogg.setContentView(R.layout.selection_recyclerview2);
        Window window = _dialogg.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _dialogg.show();

        RecyclerView Rv_Selection = _dialogg.findViewById(R.id.Rv_Selection);
        EditText EtSearch = _dialogg.findViewById(R.id.EtSearch);

        EtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                Helper.l("searchable string : " + str);

                if (str.length() == 0) {
                    //load original
                    SectionAdapterwithData(list_, Rv_Selection, selection, _dialogg, tv_attach);

                } else if (str.length() > 2) {
                    //perform search
                    ArrayList<TwoBean> temp_name_arraylist = new ArrayList<TwoBean>();
                    for (TwoBean d : list_) {
                        String name_lower = d.getName().toLowerCase();
                        String searchable_lower = str.toLowerCase();
                        if (d.getName() != null && name_lower.contains(searchable_lower)) {
                            temp_name_arraylist.add(d);
                        }
                    }

                    if (temp_name_arraylist.size() > 0) {
                        SectionAdapterwithData(temp_name_arraylist, Rv_Selection, selection, _dialogg, tv_attach);
                    } else {
                        Helper.t(getApplicationContext(), "data not found");
                    }

                } else {
                    Helper.l("length of string " + str.length());
                }

            }
        });

        SectionAdapterwithData(list_, Rv_Selection, selection, _dialogg, tv_attach);

    }

    private void SectionAdapterwithData(ArrayList<TwoBean> list_, RecyclerView
            rv_Selection, String selection, Dialog _dialogg, TextView tv_attach) {
        try {
            SelectionAdapter2 selectionAdapter = new SelectionAdapter2(list_, RiskAssementActivity.this,selection, new CLickCallBack2() {
                @Override
                public void onClick(TwoBean result) {
                    _dialogg.dismiss();
                    tv_attach.setText(result.getName());
                    AttachDataDialog(selection, result);
                }
            });
            rv_Selection.setLayoutManager(new LinearLayoutManager(RiskAssementActivity.this));
            rv_Selection.setAdapter(selectionAdapter);
            selectionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AttachDataDialog(String selection, TwoBean result) {
        try {
            if (selection.equalsIgnoreCase("AIAPW")) {
                AIAPW = result.getId();
            }else if (selection.equalsIgnoreCase("site_day")) {
                site_day = result.getId();
            }else if (selection.equalsIgnoreCase("site_time")) {
                site_time = result.getId();
            }else if (selection.equalsIgnoreCase("contact")) {
                contact = result.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
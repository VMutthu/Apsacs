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
import com.entrolabs.apsacs.databinding.ActivityClinicalServices2Binding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class ClinicalServicesActivity2 extends AppCompatActivity {
    ActivityClinicalServices2Binding binding;
    SessionManager sessionManager;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    Calendar dateTime1 = Calendar.getInstance();


    ArrayList<TwoBean> Government_list = new ArrayList<>();
    ArrayList<TwoBean> Private_list = new ArrayList<>();

    final String[] perms = {Manifest.permission.INTERNET,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String test_center="",district="",result="",facility="";
    String beneficiaryid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClinicalServices2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_blue));
        }
        InitView();
        binding.toolBar.tvHeading.setText("Screening Activity");
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(ClinicalServicesActivity2.this, MainActivity.class));
        }
        return false;
    }

    private void InitView() {
        try {
            sessionManager = new SessionManager(ClinicalServicesActivity2.this);
            if (methodRequiresPermission(perms, 111)) {
                Helper.l("permissions granted");
                //Helper.showProgressDialog(AddBeneficiaryActivity.this);
            } else {
                Helper.t(getApplicationContext(), "Please Grant required app permissions!!");
            }
            Intent i = getIntent();
            if (i == null) {
                JSONObject params2 = new JSONObject();
                params2.put("getClinicalGovt", "true");
                GetDATA(1, params2);

            } else {
                beneficiaryid = i.getStringExtra("beneficiaryid");
                JSONObject params2 = new JSONObject();
                params2.put("getClinicalGovt", "true");
                GetDATA(1, params2);

            }
            binding.toolBar.ivBack.setOnClickListener(onClickListener);
            binding.TVDatePicker.setOnClickListener(onClickListener);
            binding.TVGovernment.setOnClickListener(onClickListener);
            binding.TVGovernment.setOnClickListener(onClickListener);
            binding.TVPrivate.setOnClickListener(onClickListener);
            binding.TVPrivate.setOnClickListener(onClickListener);
            binding.BTSubmit.setOnClickListener(onClickListener);
            //binding.TVFacility.setOnClickListener(onClickListener);
            binding.RGServiceProvider.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.RB_Government:
                            binding.TVGovernment.setText("");
                            binding.TVPrivate.setText("");
                            binding.LTGovernmentLayout.setVisibility(View.VISIBLE);
                            binding.LTPrivateLayout.setVisibility(View.GONE);
                            //binding.LTTreatmentLayout.setVisibility(View.GONE);
                            binding.submitLayout.setVisibility(View.GONE);
                            break;
                        case R.id.RB_Private:
                            binding.TVGovernment.setText("");
                            binding.TVPrivate.setText("");
                            binding.LTGovernmentLayout.setVisibility(View.GONE);
                            binding.LTPrivateLayout.setVisibility(View.VISIBLE);
                            //binding.LTTreatmentLayout.setVisibility(View.GONE);
                            binding.submitLayout.setVisibility(View.GONE);
                            break;
                    }
                }
            });
            binding.RGSyphilisResult.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.RB_Positive:
                            result = "Positive";
                            binding.ETCenterName.setText("");
                            binding.ETFacility.setText("");
                            binding.LTPositiveLayout.setVisibility(View.VISIBLE);
                            binding.LTNegativeLayout.setVisibility(View.GONE);
                            binding.submitLayout.setVisibility(View.VISIBLE);
                            break;
                        case R.id.RB_Negative:
                            result = "Negative";
                            binding.ETCenterName.setText("");
                            binding.ETFacility.setText("");
                            binding.LTPositiveLayout.setVisibility(View.GONE);
                            binding.LTNegativeLayout.setVisibility(View.VISIBLE);
                            binding.submitLayout.setVisibility(View.VISIBLE);
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
                    startActivity(new Intent(ClinicalServicesActivity2.this, MainActivity.class));
                    break;
                case R.id.TV_datePicker:
                    updateDate("");
                    break;
                case R.id.TV_Government:
                    binding.submitLayout.setVisibility(View.GONE);
                    if (Government_list.size() > 0) {
                        SelectSelection(binding.TVGovernment, Government_list, "Government");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_Private:
                    binding.submitLayout.setVisibility(View.GONE);
                    if (Private_list.size() > 0) {
                        SelectSelection(binding.TVPrivate, Private_list, "Private");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                /*case R.id.TV_Private:
                    binding.submitLayout.setVisibility(View.GONE);
                    if (Private_list.size() > 0) {
                        SelectSelection(binding.TVPrivate, Private_list, "Private");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;*/
                case R.id.BT_submit:
                    try {
                        String date = binding.TVDatePicker.getText().toString();
                        String op_id = binding.ETOPPIDNo.getText().toString();
                        String followup = binding.ETCenterName.getText().toString();
                        String due_date = binding.ETFacility.getText().toString();
                        if (date.equals("") && date.length() == 0) {
                            Helper.t(getApplicationContext(), "Please select the date");
                        } else if (op_id.equals("") && op_id.length() == 0) {
                            Helper.t(getApplicationContext(), "Please enter the OP_ID");
                        } else {
                            JSONObject params = new JSONObject();
                            params.put("addOrUpdateSyphilis", "true");
                            params.put("date", date);
                            params.put("beneficiaryid", beneficiaryid);
                            params.put("op_pid", op_id);
                            params.put("test_center", test_center);
                            params.put("dew_date", due_date);
                            params.put("result", result);
                            params.put("follow_up_date", followup);
                            params.put("facility", facility);
                            params.put("id", "");
                            GetDATA(4, params);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


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

    private void GetDATA(int index, JSONObject params) {
        if (Helper.isNetworkAvailable(ClinicalServicesActivity2.this)) {
            AuthKey3.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    Helper.l(response_.toString());
                    try {
                        if (index == 1){
                            Government_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                Government_list.add(twoBean);
                            }
                            JSONObject params2 = new JSONObject();
                            params2.put("getClinicalPrivate", "true");
                            GetDATA(2, params2);
                        }else if (index == 2){
                            Private_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                Private_list.add(twoBean);
                            }
                            if (!beneficiaryid.equals("")) {
                                JSONObject params2 = new JSONObject();
                                params2.put("getSyphilis", "true");
                                params2.put("beneficiaryid",beneficiaryid);
                                GetDATA(3, params2);
                            }
                        }else if (index == 3){
                            JSONArray jsonArray = response_.getJSONArray("data");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            binding.TVDatePicker.setText(jsonObject.getString("date"));
                            binding.ETOPPIDNo.setText(jsonObject.getString("op_pid"));
                            result = jsonObject.getString("result");
                            if (jsonObject.getString("result").equals("Positive")){
                                binding.RBPrivate.setChecked(true);
                            }else{
                                binding.RBNegative.setChecked(true);
                            }
                            binding.ETCenterName.setText(jsonObject.getString("follow_up_date"));
                            binding.ETFacility.setText(jsonObject.getString("dew_date"));

                        } else if (index == 4){
                            if (response_.getString("result").equals("success")){
                                if (response_.getString("status").equals("updated successfully")){
                                    finish();
                                    startActivity(new Intent(ClinicalServicesActivity2.this, MainActivity.class));
                                    Helper.t(getApplicationContext(), "Data Updated successful");
                                }else {
                                    Helper.t(getApplicationContext(), "Data submit successful");
                                    finish();
                                    startActivity(new Intent(ClinicalServicesActivity2.this, HIVTestingActivity.class)
                                            .putExtra("beneficiaryid", beneficiaryid));
                                }
                            }else{
                                Helper.t(getApplicationContext(),"Data not submit successful");
                            }
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
            }, UrlBase.BASE_URL, params, ClinicalServicesActivity2.this, "show");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }

    private void updateDate(String str) {
        dateTime1 = Calendar.getInstance();
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(this, d, dateTime1.get(Calendar.YEAR), dateTime1.get(Calendar.MONTH), dateTime1.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        /*if (str.equalsIgnoreCase("fromdate")) {
            datePickerDialog.getDatePicker().setMinDate((long) (System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 14)));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        } else {
            try {
                Date d = formatter.parse(binding.TVFOFromdate.getText().toString());
                *//*int ys = d.getYear();
                d.setYear(ys + 16);
                Date ndob = new Date(String.valueOf(d));
                *//*//datePickerDialog.getDatePicker().setMinDate((long) (d.getTime() + (1000 * 60 * 60 * 24 * 365.25 * 16)));
                datePickerDialog.getDatePicker().setMinDate(d.getTime());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/
        datePickerDialog.show();

    }


    android.app.DatePickerDialog.OnDateSetListener d = new android.app.DatePickerDialog.OnDateSetListener() {
        @SuppressLint("InlinedApi")
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

            dateTime1.set(Calendar.YEAR, year);
            dateTime1.set(Calendar.MONTH, monthOfYear);
            dateTime1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel();

        }
    };

    private void updateTextLabel() {
        /*if (date_flag.equalsIgnoreCase("fromdate")) {
            binding.TVFOFromdate.setText(formatter.format(dateTime1.getTime()));
        } else if (date_flag.equalsIgnoreCase("todate")) {
            binding.TVFOTodate.setText(formatter.format(dateTime1.getTime()));
        }*/
        binding.TVDatePicker.setText(formatter.format(dateTime1.getTime()));
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

        Dialog _dialogg = new Dialog(ClinicalServicesActivity2.this, R.style.SuccessFailureDialogTheme);
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
            SelectionAdapter2 selectionAdapter = new SelectionAdapter2(list_, ClinicalServicesActivity2.this,selection, new CLickCallBack2() {
                @Override
                public void onClick(TwoBean result) {
                    _dialogg.dismiss();
                    tv_attach.setText(result.getName());
                    AttachDataDialog(selection, result);
                }
            });
            rv_Selection.setLayoutManager(new LinearLayoutManager(ClinicalServicesActivity2.this));
            rv_Selection.setAdapter(selectionAdapter);
            selectionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AttachDataDialog(String selection, TwoBean result) {
        try {
            if (selection.equalsIgnoreCase("Government")) {
                test_center = result.getId();
            }else if (selection.equalsIgnoreCase("Private")) {
                test_center = result.getId();
            }else if (selection.equalsIgnoreCase("facility")) {
                facility=result.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
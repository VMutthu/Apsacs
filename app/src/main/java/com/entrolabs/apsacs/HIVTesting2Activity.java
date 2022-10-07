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
import com.entrolabs.apsacs.databinding.ActivityHivtesting2Binding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class HIVTesting2Activity extends AppCompatActivity {
    ActivityHivtesting2Binding binding;
    SessionManager sessionManager;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    Calendar dateTime1 = Calendar.getInstance();
    String date_flage="",ACN_string="",Art_Initiated="";
    String beneficiaryid="";

    ArrayList<TwoBean> ACN_list= new ArrayList<>();
    final String[] perms = {Manifest.permission.INTERNET,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHivtesting2Binding.inflate(getLayoutInflater());
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
            startActivity(new Intent(HIVTesting2Activity.this, MainActivity.class));
        }
        return false;
    }

    private void InitView() {
        try {
            sessionManager = new SessionManager(HIVTesting2Activity.this);
            if (methodRequiresPermission(perms, 111)) {
                Helper.l("permissions granted");
                //Helper.showProgressDialog(AddBeneficiaryActivity.this);
            } else {
                Helper.t(getApplicationContext(), "Please Grant required app permissions!!");
            }
            Intent i = getIntent();
            if (i == null) {
                JSONObject params = new JSONObject();
                params.put("getArtCenters", "true");
                GetDATA(1, params);

            } else {
                beneficiaryid = i.getStringExtra("beneficiaryid");
                JSONObject params = new JSONObject();
                params.put("getArtCenters", "true");
                GetDATA(1, params);

            }
            binding.toolBar.ivBack.setOnClickListener(onClickListener);
            binding.toolBar.tvHeading.setText("Screening Activity");
            binding.TVPARD.setOnClickListener(onClickListener);
            binding.TVARD.setOnClickListener(onClickListener);
            binding.TVAID.setOnClickListener(onClickListener);
            binding.TVCD4TestingDate.setOnClickListener(onClickListener);
            binding.TVFTVLT.setOnClickListener(onClickListener);
            binding.TVLVLTD.setOnClickListener(onClickListener);
            binding.TVAAD.setOnClickListener(onClickListener);
            binding.TVTBTesting.setOnClickListener(onClickListener);
            binding.TVTBReferred.setOnClickListener(onClickListener);
            binding.TVTBTreated.setOnClickListener(onClickListener);
            binding.TVACN.setOnClickListener(onClickListener);
            binding.ftdSubmit.setOnClickListener(onClickListener);
            binding.rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_yes:
                            Art_Initiated = "1";
                            break;
                        case R.id.radio_no:
                            Art_Initiated = "2";
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
                    startActivity(new Intent(HIVTesting2Activity.this, MainActivity.class));
                    break;
                case R.id.TV_PARD:
                    updateDate();
                    date_flage="PARD";
                    break;
                case R.id.TV_ARD:
                    updateDate();
                    date_flage="ARD";
                    break;
                case R.id.TV_AID:
                    updateDate();
                    date_flage="AID";
                    break;

                case R.id.TV_CD4_testing_date:
                    updateDate();
                    date_flage="CD4_testing_date";
                    break;

                case R.id.TV_FTVLT:
                    updateDate();
                    date_flage="FTVLT";
                    break;
                case R.id.TV_LVLTD:
                    updateDate();
                    date_flage="LVLTD";
                    break;

                case R.id.TV_AAD:
                    updateDate();
                    date_flage="AAD";
                    break;
                case R.id.TV_TB_testing:
                    updateDate();
                    date_flage="TB_testing";
                    break;

                case R.id.TV_TB_Referred:
                    updateDate();
                    date_flage="TB_Referred";
                    break;

                case R.id.TV_TB_Treated:
                    updateDate();
                    date_flage="TB_Treated";
                    break;
                case R.id.TV_ACN:
                    if (ACN_list.size() > 0) {
                        SelectSelection(binding.TVACN, ACN_list, "ACN");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.ftd_submit:
                    try {
                        String HIV_CD = binding.ETHIVCD.getText().toString();
                        String HIV_p_no = binding.ETHIVPNo.getText().toString();
                        String ICTC_center = binding.ETICTCCenter.getText().toString();
                        String HIV_Remarks = binding.ETHIVRemarks.getText().toString();
                        String PARD = binding.TVPARD.getText().toString();
                        String PARN = binding.ETPARN.getText().toString();
                        String CD4_count = binding.ETCD4Count.getText().toString();
                        String Pre_Art_Remark = binding.ETPreArtRemark.getText().toString();
                        String ARD = binding.TVARD.getText().toString();
                        String ARN = binding.ETARN.getText().toString();
                        String AID = binding.TVAID.getText().toString();
                        String AR = binding.ETAR.getText().toString();
                        String CD4_testing_date = binding.TVCD4TestingDate.getText().toString();
                        String latest_cd4_count = binding.ETLatestCd4Count.getText().toString();
                        String FTVLT = binding.TVFTVLT.getText().toString();
                        String FTVLC = binding.ETFTVLC.getText().toString();
                        String LVLTD = binding.TVLVLTD.getText().toString();
                        String LVLC = binding.ETLVLC.getText().toString();
                        String AAD = binding.TVAAD.getText().toString();
                        String ART_R = binding.ETARTR.getText().toString();
                        String AFUR = binding.ETAFUR.getText().toString();
                        String GFNI = binding.ETGFNI.getText().toString();
                        String CFN = binding.ETCFN.getText().toString();
                        String DOLOA = binding.ETDOLOA.getText().toString();
                        String DOR = binding.ETDOR.getText().toString();
                        String TB_testing = binding.TVTBTesting.getText().toString();
                        String TB_Referred = binding.TVTBReferred.getText().toString();
                        String TB_Treated = binding.TVTBTreated.getText().toString();
                        String On_DOT = binding.ETOnDOT.getText().toString();
                        if (HIV_CD.equals("") && HIV_CD.length() == 0) {
                            Helper.t(getApplicationContext(), "Please enter the HIV confirematory date");
                        } else {
                            JSONObject param = new JSONObject();
                            param.put("addOrUpdateHIVPositive", "true");
                            param.put("beneficiaryid", beneficiaryid);
                            param.put("hiv_confirm_date", HIV_CD);
                            param.put("hiv_positive_pid", HIV_p_no);
                            param.put("ictc_name", ICTC_center);
                            param.put("hiv_remark", HIV_Remarks);
                            param.put("pre_art_registration_date", PARD);
                            param.put("pre_art_registration_no", PARN);
                            param.put("pre_art_cd4_count", CD4_count);
                            param.put("pre_art_remark", Pre_Art_Remark);
                            param.put("art_center_name", ACN_string);
                            param.put("on_art_registration_date", ARD);
                            param.put("on_art_registration_no", ARN);
                            param.put("on_art_initiated_date", AID);
                            param.put("art_regimen", AR);
                            param.put("latest_cd4_testing_date", CD4_testing_date);
                            param.put("latest_cd4_count", latest_cd4_count);
                            param.put("first_time_viral_load_testing_date", FTVLT);
                            param.put("first_time_viral_load_count", FTVLC);
                            param.put("latest_viral_load_testing_date", LVLTD);
                            param.put("latest_viral_load_count", LVLC);
                            param.put("second_line_art_initiated", Art_Initiated);
                            param.put("second_line_art_started", AAD);
                            param.put("second_line_art_regimen", ART_R);
                            param.put("second_time_art_follow_up_remark", AFUR);
                            param.put("navigation_gap_identified", GFNI);
                            param.put("navigation_contact", CFN);
                            param.put("navigation_linked_on_art_date", DOLOA);
                            param.put("navigation_date_of_retained_treatment", DOR);
                            param.put("tb_tested", TB_testing);
                            param.put("tb_referred", TB_Referred);
                            param.put("tb_treated", TB_Treated);
                            param.put("on_dot", On_DOT);
                            param.put("spouse_status", "1");
                            param.put("remark", "");
                            GetDATA(2, param);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void GetDATA(int index, JSONObject params) {
        if (Helper.isNetworkAvailable(HIVTesting2Activity.this)) {
            AuthKey3.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        if (index == 1){
                            ACN_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                ACN_list.add(twoBean);
                            }
                            if (!beneficiaryid.equals("")){
                                JSONObject params = new JSONObject();
                                params.put("getHIVPositive","true");
                                params.put("beneficiaryid",beneficiaryid);
                                GetDATA(3,params);
                            }
                        }else if (index == 2){
                            if (response_.getString("result").equals("success")){
                                if (response_.getString("status").equals("updated successfully")){
                                    finish();
                                    startActivity(new Intent(HIVTesting2Activity.this, MainActivity.class));
                                    Helper.t(getApplicationContext(), "Data updated successfully");
                                }else {
                                    finish();
                                    startActivity(new Intent(HIVTesting2Activity.this, RiskAssementActivity.class)
                                            .putExtra("beneficiaryid",beneficiaryid));
                                    Helper.t(getApplicationContext(), "Data submit successfully");
                                }
                            }else{

                            }
                        }else if (index == 3){
                            JSONArray jsonArray = response_.getJSONArray("data");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            binding.ETHIVCD.setText(jsonObject.getString("hiv_confirm_date"));
                            binding.ETHIVPNo.setText(jsonObject.getString("hiv_positive_pid"));
                            binding.ETICTCCenter.setText(jsonObject.getString("ictc_name"));
                            binding.ETHIVRemarks.setText(jsonObject.getString("hiv_remark"));
                            binding.TVPARD.setText(jsonObject.getString("pre_art_registration_date"));
                            binding.ETPARN.setText(jsonObject.getString("pre_art_registration_no"));
                            binding.ETCD4Count.setText(jsonObject.getString("pre_art_cd4_count"));
                            binding.ETPreArtRemark.setText(jsonObject.getString("pre_art_remark"));
                            for (TwoBean t:ACN_list) {
                                if (t.getId().equals(jsonObject.getString("art_center_name"))){
                                    ACN_string = t.getId();
                                    binding.TVACN.setText(t.getName());
                                }
                            }
                            binding.TVARD.setText(jsonObject.getString("on_art_registration_date"));
                            binding.ETARN.setText(jsonObject.getString("on_art_registration_no"));
                            binding.TVAID.setText(jsonObject.getString("on_art_initiated_date"));
                            binding.ETAR.setText(jsonObject.getString("art_regimen"));
                            binding.TVCD4TestingDate.setText(jsonObject.getString("latest_cd4_testing_date"));
                            binding.ETLatestCd4Count.setText(jsonObject.getString("latest_cd4_count"));
                            binding.TVFTVLT.setText(jsonObject.getString("first_time_viral_load_testing_date"));
                            binding.ETFTVLC.setText(jsonObject.getString("first_time_viral_load_count"));
                            binding.TVLVLTD.setText(jsonObject.getString("latest_viral_load_testing_date"));
                            binding.ETLVLC.setText(jsonObject.getString("latest_viral_load_count"));
                            //second_line_art_initiated
                            Art_Initiated = jsonObject.getString("second_line_art_initiated");
                            if (jsonObject.getString("second_line_art_initiated").equals("1")){
                                binding.radioYes.setChecked(true);
                            }else{
                                binding.radioNo.setChecked(true);
                            }
                            binding.TVAAD.setText(jsonObject.getString("second_line_art_started"));
                            binding.ETARTR.setText(jsonObject.getString("second_line_art_regimen"));
                            binding.ETAFUR.setText(jsonObject.getString("second_time_art_follow_up_remark"));
                            binding.ETGFNI.setText(jsonObject.getString("navigation_gap_identified"));
                            binding.ETCFN.setText(jsonObject.getString("navigation_contact"));
                            binding.ETDOLOA.setText(jsonObject.getString("navigation_linked_on_art_date"));
                            binding.ETDOR.setText(jsonObject.getString("navigation_date_of_retained_treatment"));
                            binding.TVTBTesting.setText(jsonObject.getString("tb_tested"));
                            binding.TVTBReferred.setText(jsonObject.getString("tb_referred"));
                            binding.TVTBTreated.setText(jsonObject.getString("tb_treated"));
                            binding.ETOnDOT.setText(jsonObject.getString("on_dot"));
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
            }, UrlBase.BASE_URL, params, HIVTesting2Activity.this, "show");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }

    private void updateDate() {
        dateTime1 = Calendar.getInstance();
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(this, d, dateTime1.get(Calendar.YEAR), dateTime1.get(Calendar.MONTH), dateTime1.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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
        if (date_flage.equals("PARD")){
            binding.TVPARD.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("ARD")){
            binding.TVARD.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("AID")){
            binding.TVAID.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("CD4_testing_date")){
            binding.TVCD4TestingDate.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("FTVLT")){
            binding.TVFTVLT.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("LVLTD")){
            binding.TVLVLTD.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("AAD")){
            binding.TVAAD.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("TB_testing")){
            binding.TVTBTesting.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("TB_Referred")){
            binding.TVTBReferred.setText(formatter.format(dateTime1.getTime()));
        }else if (date_flage.equals("TB_Treated")){
            binding.TVTBTreated.setText(formatter.format(dateTime1.getTime()));
        }
        //binding.TVDatePicker.setText(formatter.format(dateTime1.getTime()));
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

        Dialog _dialogg = new Dialog(HIVTesting2Activity.this, R.style.SuccessFailureDialogTheme);
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
            SelectionAdapter2 selectionAdapter = new SelectionAdapter2(list_, HIVTesting2Activity.this,selection, new CLickCallBack2() {
                @Override
                public void onClick(TwoBean result) {
                    _dialogg.dismiss();
                    tv_attach.setText(result.getName());
                    AttachDataDialog(selection, result);
                }
            });
            rv_Selection.setLayoutManager(new LinearLayoutManager(HIVTesting2Activity.this));
            rv_Selection.setAdapter(selectionAdapter);
            selectionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AttachDataDialog(String selection, TwoBean result) {
        try {
            if (selection.equalsIgnoreCase("ACN")) {
                ACN_string = result.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
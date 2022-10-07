package com.entrolabs.apsacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
import com.entrolabs.apsacs.Common.VerhoeffAlgorithm;
import com.entrolabs.apsacs.URL.UrlBase;
import com.entrolabs.apsacs.databinding.ActivityAddBeneficiaryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class AddBeneficiaryActivity extends AppCompatActivity {
    ActivityAddBeneficiaryBinding binding;
    SessionManager sessionManager;

    /*ArrayList<TwoBean> district_list = new ArrayList<>();
    ArrayList<TwoBean> mandal_list = new ArrayList<>();
    ArrayList<TwoBean> secretariat_list = new ArrayList<>();*/
    ArrayList<TwoBean> gender_list = new ArrayList<>();
    ArrayList<TwoBean> SubcatagoryCommunity_list = new ArrayList<>();
    ArrayList<TwoBean> EDULevel_list = new ArrayList<>();
    ArrayList<TwoBean> EMPStatus_list = new ArrayList<>();
    ArrayList<TwoBean> HostspotName_list = new ArrayList<>();
    ArrayList<TwoBean> Martial_Status_list = new ArrayList<>();
    ArrayList<TwoBean> Typology_list = new ArrayList<>();
    ArrayList<TwoBean> District_list = new ArrayList<>();
    ArrayList<TwoBean> Mandal_list = new ArrayList<>();
    ArrayList<TwoBean> Secretariat_list = new ArrayList<>();


    final String[] perms = {Manifest.permission.INTERNET,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String district="",mandal="",secretariat="",gender="",SubcatagoryCommunity="",EDULevel="",EMPStatus="",HostspotName="",Martial_Status="",Typology="",Spouse="",Regular="";
    String beneficiaryid="";
    String mobilepattern = "^[6-9]{1}[0-9]{9}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBeneficiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_blue));
        }
        binding.toolBar.tvHeading.setText("Add Beneficiary");
        InitView();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(AddBeneficiaryActivity.this, BeneficiaryScreenActivity.class));
        }
        return false;
    }

    private void InitView() {
        sessionManager = new SessionManager(AddBeneficiaryActivity.this);
        if (methodRequiresPermission(perms, 111)) {
            Helper.l("permissions granted");
            //Helper.showProgressDialog(AddBeneficiaryActivity.this);
        } else {
            Helper.t(getApplicationContext(), "Please Grant required app permissions!!");
        }

        TwoBean t1 = new TwoBean();
        t1.setId("1");
        t1.setName("Male");
        gender_list.add(t1);
        TwoBean t2 = new TwoBean();
        t2.setId("2");
        t2.setName("Female");
        gender_list.add(t2);
        TwoBean t3 = new TwoBean();
        t3.setId("3");
        t3.setName("Transgender");
        gender_list.add(t1);
        binding.toolBar.ivBack.setOnClickListener(onClickListener);
        binding.TVDistrict.setOnClickListener(onClickListener);
        binding.TVMandal.setOnClickListener(onClickListener);
        binding.TVSecretariat.setOnClickListener(onClickListener);
        binding.TVHRGGender.setOnClickListener(onClickListener);
        binding.TVSubcatagoryCommunity.setOnClickListener(onClickListener);
        binding.TVEDULevel.setOnClickListener(onClickListener);
        binding.TVEMPStatus.setOnClickListener(onClickListener);
        binding.TVHRGHostspotName.setOnClickListener(onClickListener);
        binding.TVMartialStatus.setOnClickListener(onClickListener);
        binding.TVTypology.setOnClickListener(onClickListener);
        binding.ftdSubmit.setOnClickListener(onClickListener);
        binding.ETHRGMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 10) {
                    if (!s.toString().matches(mobilepattern)) {
                        Helper.t(getApplicationContext(), "Please enter valid contact number");
                    }
                }else if (s.length() == 1){
                    if (!s.toString().matches("^[6-9]{1}$")) {
                        Helper.t(getApplicationContext(), "Please enter valid contact number");
                    }
                }else if (s.length() > 10){
                    Helper.t(getApplicationContext(),"Please check the mobile number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.ETHRGUID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if ( s.toString().length() < 12) {

                }else if (!VerhoeffAlgorithm.validateVerhoeff(s.toString()) && s.toString().length() == 12) {
                    Helper.t(getApplicationContext(), "Please enter valid aadhaar number");
                }else if (s.toString().length() > 12){
                    Helper.t(getApplicationContext(),"Please check the Aadher number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.rgSpouse.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radio_yes:
                        Spouse = "yes";
                        break;
                    case R.id.radio_no:
                        Spouse = "no";
                        break;
                }
            }
        });
        binding.rgRegular.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radio_R_yes:
                        Regular = "yes";
                        break;
                    case R.id.radio_R_no:
                        Regular ="No";
                        break;
                }
            }
        });
        Intent i = getIntent();
        if (i == null){
            try {
                JSONObject params2 = new JSONObject();
                params2.put("getMaritalStatus", "true");
                GetDATA(1, params2);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else{
            try {
                beneficiaryid = i.getStringExtra("beneficiaryid");
                JSONObject params2 = new JSONObject();
                params2.put("getMaritalStatus", "true");
                GetDATA(1, params2);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_back:
                    finish();
                    startActivity(new Intent(AddBeneficiaryActivity.this, BeneficiaryScreenActivity.class));
                    break;
                case R.id.TV_district:
                    mandal="";secretariat="";
                    binding.TVMandal.setText("");
                    binding.TVSecretariat.setText("");
                    if (District_list.size() > 0) {
                        SelectSelection(binding.TVDistrict, District_list, "district");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_mandal:
                    secretariat="";
                    binding.TVSecretariat.setText("");
                    if (Mandal_list.size() > 0) {
                        SelectSelection(binding.TVMandal, Mandal_list, "mandal");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_Secretariat:
                    if (Secretariat_list.size() > 0) {
                        SelectSelection(binding.TVSecretariat, Secretariat_list, "secretariat");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_HRG_Gender:
                    if (gender_list.size() > 0) {
                        SelectSelection(binding.TVHRGGender, gender_list, "gender");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_Subcatagory_community:
                    if (SubcatagoryCommunity_list.size() > 0) {
                        SelectSelection(binding.TVSubcatagoryCommunity, SubcatagoryCommunity_list, "SubcatagoryCommunity");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_EDU_level:
                    if (EDULevel_list.size() > 0) {
                        SelectSelection(binding.TVEDULevel, EDULevel_list, "EDULevel");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_EMP_status:
                    if (EMPStatus_list.size() > 0) {
                        SelectSelection(binding.TVEMPStatus, EMPStatus_list, "EMPStatus");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_HRG_Hostspot_Name:
                    if (HostspotName_list.size() > 0) {
                        SelectSelection(binding.TVHRGHostspotName, HostspotName_list, "HostspotName");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_Martial_Status:
                    if (Martial_Status_list.size() > 0) {
                        SelectSelection(binding.TVMartialStatus, Martial_Status_list, "Martial_Status");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.TV_Typology:
                    if (Typology_list.size() > 0) {
                        SelectSelection(binding.TVTypology, Typology_list, "Typology");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.ftd_submit:
                    String name = binding.ETHRGName.getText().toString();
                    String age = binding.ETHRGAge.getText().toString();
                    String aadher = binding.ETHRGUID.getText().toString();
                    String TIID = binding.ETHRGTIID.getText().toString();
                    String sochuid = binding.ETSochUID.getText().toString();
                    String mobile = binding.ETHRGMobile.getText().toString();
                    if (district.equals("") && district.length() == 0){
                    Helper.t(getApplicationContext(),"Please select the district");
                    }else if (mandal.equals("") && mandal.length() == 0){
                        Helper.t(getApplicationContext(),"Please select the mandal");
                    }else if (secretariat.equals("") && secretariat.length() == 0){
                        Helper.t(getApplicationContext(),"Please select the secretariat");
                    }else if (name.equals("") && name.length() == 0){
                        Helper.t(getApplicationContext(),"Please enter the name");
                    }else if (age.equals("") && age.length() == 0){
                        Helper.t(getApplicationContext(),"Please enter the age");
                    }else if (mobile.equals("") && mobile.length() == 0) {
                        Helper.t(getApplicationContext(), "Please enter the mobile");
                    }else if (!mobile.matches(mobilepattern)){
                        Helper.t(getApplicationContext(),"Please enter the vailed mobile number");
                    }else if (mobile.length() < 10){
                        Helper.t(getApplicationContext(),"Please check the mobile");
                    }else if (aadher.equals("") && aadher.length() == 0) {
                        Helper.t(getApplicationContext(), "Please enter the UID");
                    }else if (!VerhoeffAlgorithm.validateVerhoeff(aadher.toString()) && aadher.toString().length() == 12){
                        Helper.t(getApplicationContext(),"Please enter the vailed uid number");
                    }else if (aadher.length() < 12){
                        Helper.t(getApplicationContext(),"Please check the UID");
                    }else if (gender.equals("") && gender.length() == 0){
                        Helper.t(getApplicationContext(),"Please select the Gender");
                    }else if (sochuid.equals("") && sochuid.length() == 0){
                        Helper.t(getApplicationContext(),"Please select the sochuid");
                    }else{
                        try {
                            JSONObject params = new JSONObject();
                            params.put("addOREditBeneficiary", "true");
                            params.put("name", name);
                            params.put("age", age);
                            params.put("gender", gender);
                            params.put("aadhar", aadher);
                            params.put("typology", Typology);
                            params.put("community", SubcatagoryCommunity);
                            params.put("hotspot", HostspotName);
                            params.put("hrg_ti_id_no", TIID);
                            params.put("soch_uid", sochuid);
                            params.put("marital_status", Martial_Status);
                            params.put("hrg_contact_number", mobile);
                            params.put("have_spouse", Spouse);
                            params.put("regular_partner", Regular);
                            params.put("employment_status", EMPStatus);
                            params.put("education_level", EDULevel);
                            params.put("district", district);
                            params.put("mandal", mandal);
                            params.put("secretariat", secretariat);
                            params.put("beneficiaryid", beneficiaryid);
                            GetDATA(6, params);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    break;
            }
        }
    };




    private void GetDATA(int index, JSONObject params) {
        if (Helper.isNetworkAvailable(AddBeneficiaryActivity.this)) {
            AuthKey3.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        if (index == 1){
                            Martial_Status_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                Martial_Status_list.add(twoBean);
                            }
                            JSONObject params2 = new JSONObject();
                            params2.put("getCommunities", "true");
                            GetDATA(2, params2);
                        }else if (index == 2){
                            SubcatagoryCommunity_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                SubcatagoryCommunity_list.add(twoBean);
                            }
                            JSONObject params2 = new JSONObject();
                            params2.put("getEducationLevels", "true");
                            GetDATA(3, params2);
                        }else if (index == 3){
                            EDULevel_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                EDULevel_list.add(twoBean);
                            }
                            JSONObject params2 = new JSONObject();
                            params2.put("getEmploymentStatus", "true");
                            GetDATA(4, params2);
                        }else if (index == 4){
                            EMPStatus_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                EMPStatus_list.add(twoBean);
                            }
                            JSONObject params2 = new JSONObject();
                            params2.put("getTypologies", "true");
                            GetDATA(5, params2);
                        }else if (index == 5){
                            Typology_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                Typology_list.add(twoBean);
                            }

                            JSONObject params2 = new JSONObject();
                            params2.put("getHotspots", "true");
                            GetDATA(7, params2);
                        }else if (index == 6){
                            if (response_.getString("result").equals("success")){

                                if (response_.getString("status").equals("Updated successfully")){
                                    finish();
                                    startActivity(new Intent(AddBeneficiaryActivity.this, MainActivity.class));
                                    Helper.t(getApplicationContext(), "Data Updated successful");
                                }else {
                                    JSONArray jsonArray = response_.getJSONArray("data");
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    //sessionManager.storeVal(Helper.Apsacs_soch_uid,jsonObject.getString("beneficiaryid"));
                                    district = "";
                                    mandal = "";
                                    secretariat = "";
                                    gender = "";
                                    SubcatagoryCommunity = "";
                                    EDULevel = "";
                                    EMPStatus = "";
                                    HostspotName = "";
                                    Martial_Status = "";
                                    Typology = "";
                                    Spouse = "";
                                    Regular = "";
                                    binding.ETHRGName.setText("");
                                    binding.ETHRGAge.setText("");
                                    binding.ETHRGUID.setText("");
                                    binding.ETHRGTIID.setText("");
                                    binding.ETSochUID.setText("");
                                    binding.ETHRGMobile.setText("");
                                    binding.TVDistrict.setText("");
                                    binding.TVMandal.setText("");
                                    binding.TVSecretariat.setText("");
                                    binding.TVHRGGender.setText("");
                                    binding.TVSubcatagoryCommunity.setText("");
                                    binding.TVEDULevel.setText("");
                                    binding.TVEMPStatus.setText("");
                                    binding.TVHRGHostspotName.setText("");
                                    binding.TVMartialStatus.setText("");
                                    binding.TVTypology.setText("");
                                    finish();
                                    startActivity(new Intent(AddBeneficiaryActivity.this, ClinicalServicesActivity.class)
                                            .putExtra("beneficiaryid",jsonObject.getString("beneficiaryid")));
                                    Helper.t(getApplicationContext(), "Data submit successful");
                                }
                            }else {
                                Helper.t(getApplicationContext(),"Data not submit successful");
                            }
                        }else if (index == 7){
                            HostspotName_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                HostspotName_list.add(twoBean);
                            }
                            JSONObject params2 = new JSONObject();
                            params2.put("getDistrict", "true");
                            GetDATA(8, params2);
                        }else if (index == 8){
                            District_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                District_list.add(twoBean);
                            }
                            if (!beneficiaryid.equals("")){
                                JSONObject params2 = new JSONObject();
                                params2.put("getBeneficiary", "true");
                                params2.put("beneficiaryid",beneficiaryid);
                                GetDATA(11, params2);
                            }
                        }else if (index == 9){
                            Mandal_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                Mandal_list.add(twoBean);
                            }
                        }else if (index == 10){
                            Secretariat_list.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                Secretariat_list.add(twoBean);
                            }
                        }else if (index == 11){
                            JSONArray jsonArray = response_.getJSONArray("data");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            binding.ETHRGName.setText(jsonObject.getString("name_of_hrg"));
                            binding.ETHRGAge.setText(jsonObject.getString("age"));
                            binding.ETHRGUID.setText(jsonObject.getString("hrg_aadhar"));
                            binding.ETHRGTIID.setText(jsonObject.getString("hrg_ti_id_no"));
                            binding.ETSochUID.setText(jsonObject.getString("soch_uid"));
                            binding.ETHRGMobile.setText(jsonObject.getString("hrg_contact_number"));
                            district=jsonObject.getString("disrict_code");
                            setspID(District_list,binding.TVDistrict,jsonObject.getString("disrict_code"));
                            mandal=jsonObject.getString("site_mandal");
                            setspID(Mandal_list,binding.TVMandal,jsonObject.getString("site_mandal"));
                            secretariat=jsonObject.getString("secretariat");
                            setspID(Secretariat_list,binding.TVSecretariat,jsonObject.getString("secretariat"));
                            gender=jsonObject.getString("sex");
                            setspID(gender_list,binding.TVHRGGender,jsonObject.getString("sex"));
                            SubcatagoryCommunity=jsonObject.getString("sub_category");
                            setspID(SubcatagoryCommunity_list,binding.TVSubcatagoryCommunity,jsonObject.getString("sub_category"));
                            EDULevel=jsonObject.getString("education_level");
                            setspID(EDULevel_list,binding.TVEDULevel,jsonObject.getString("education_level"));
                            EMPStatus=jsonObject.getString("employment_status");
                            setspID(EMPStatus_list,binding.TVEMPStatus,jsonObject.getString("employment_status"));
                            HostspotName=jsonObject.getString("hotspot");
                            setspID(HostspotName_list,binding.TVHRGHostspotName,jsonObject.getString("hotspot"));
                            Martial_Status=jsonObject.getString("marital_status");
                            setspID(Martial_Status_list,binding.TVMartialStatus,jsonObject.getString("marital_status"));
                            Typology=jsonObject.getString("typology");
                            setspID(Typology_list,binding.TVTypology,jsonObject.getString("typology"));
                            if (jsonObject.getString("whether_having_spouse").equals("yes")){
                                binding.radioYes.setChecked(true);
                                Spouse="yes";
                            }else{
                                Spouse="no";
                                binding.radioNo.setChecked(true);
                            }
                            if (jsonObject.getString("regular_partner").equals("yes")){
                                binding.radioRYes.setChecked(true);
                                Regular="yes";
                            }else{
                                Regular="no";
                                binding.radioRNo.setChecked(true);
                            }
                            binding.ftdSubmit.setText("Update");

                            /*

                            have_spouse

                            regular_partner
                             */

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
            }, UrlBase.BASE_URL, params, AddBeneficiaryActivity.this, "show");
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

        Dialog _dialogg = new Dialog(AddBeneficiaryActivity.this, R.style.SuccessFailureDialogTheme);
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
            SelectionAdapter2 selectionAdapter = new SelectionAdapter2(list_, AddBeneficiaryActivity.this,selection, new CLickCallBack2() {
                @Override
                public void onClick(TwoBean result) {
                    _dialogg.dismiss();
                    tv_attach.setText(result.getName());
                    AttachDataDialog(selection, result);
                }
            });
            rv_Selection.setLayoutManager(new LinearLayoutManager(AddBeneficiaryActivity.this));
            rv_Selection.setAdapter(selectionAdapter);
            selectionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AttachDataDialog(String selection, TwoBean result) {
        try {
            if (selection.equalsIgnoreCase("district")) {
                district = result.getId();
                JSONObject params2 = new JSONObject();
                params2.put("getMandal", "true");
                params2.put("district",result.getId());
                GetDATA(9, params2);

            }else if (selection.equalsIgnoreCase("mandal")) {
                mandal= result.getId();
                JSONObject params2 = new JSONObject();
                params2.put("getSecretariat", "true");
                params2.put("mandal",result.getId());
                GetDATA(10, params2);

            }else if (selection.equalsIgnoreCase("secretariat")) {
                secretariat= result.getId();

            }else if (selection.equalsIgnoreCase("gender")) {
                gender= result.getId();

            }else if (selection.equalsIgnoreCase("SubcatagoryCommunity")) {
                SubcatagoryCommunity= result.getId();

            }else if (selection.equalsIgnoreCase("EDULevel")) {
                EDULevel= result.getId();

            }else if (selection.equalsIgnoreCase("EMPStatus")) {
                EMPStatus= result.getId();

            }else if (selection.equalsIgnoreCase("HostspotName")) {
                HostspotName= result.getId();

            }else if (selection.equalsIgnoreCase("Martial_Status")) {
                Martial_Status= result.getId();

            }else if (selection.equalsIgnoreCase("Typology")) {
                Typology= result.getId();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
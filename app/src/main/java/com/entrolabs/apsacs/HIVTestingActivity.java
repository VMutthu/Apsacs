package com.entrolabs.apsacs;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.entrolabs.apsacs.Adapter.BeneficiaryAdapter;
import com.entrolabs.apsacs.Adapter.CLickCallBack2;
import com.entrolabs.apsacs.Adapter.SelectionAdapter2;
import com.entrolabs.apsacs.Apikey.AuthKey;
import com.entrolabs.apsacs.Apikey.AuthKey2;
import com.entrolabs.apsacs.Apikey.AuthKey3;
import com.entrolabs.apsacs.Apikey.VolleyCallback;
import com.entrolabs.apsacs.Bean.TwoBean;
import com.entrolabs.apsacs.Common.AES;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;
import com.entrolabs.apsacs.URL.UrlBase;
import com.entrolabs.apsacs.databinding.ActivityHivtestingBinding;
import com.entrolabs.apsacs.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class HIVTestingActivity extends AppCompatActivity {
    ActivityHivtestingBinding binding;
    SessionManager sessionManager;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    Calendar dateTime1 = Calendar.getInstance();
    final String[] perms = {Manifest.permission.INTERNET,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int CAMERA_PICTURE = 100;
    private String image_name = "", video_name = "";
    private String mrtag = "";
    private String picturePath = "";
    String beneficiaryid="";
    String img_base64="";

    ArrayList<TwoBean> arrayList = new ArrayList<>();
    String status="",reactive_status="",refer_ictc="",living_status="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHivtestingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_blue));
        }
        binding.toolBar.tvHeading.setText("Screening Activity");

        InitView();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(HIVTestingActivity.this, MainActivity.class));
        }
        return false;
    }

    private void InitView() {
        try {
            sessionManager = new SessionManager(HIVTestingActivity.this);
            if (methodRequiresPermission(perms, 111)) {
                Helper.l("permissions granted");
                //Helper.showProgressDialog(AddBeneficiaryActivity.this);
            } else {
                Helper.t(getApplicationContext(), "Please Grant required app permissions!!");
            }
            Intent i = getIntent();
            if (i == null) {
                JSONObject params = new JSONObject();
                params.put("getLiveStatus", "true");
                GetDATA(1, params);

            } else {
                beneficiaryid = i.getStringExtra("beneficiaryid");
                JSONObject params = new JSONObject();
                params.put("getLiveStatus", "true");
                GetDATA(1, params);

            }
            binding.toolBar.ivBack.setOnClickListener(onClickListener);
            binding.TVDatePicker.setOnClickListener(onClickListener);
            binding.TVUpload.setOnClickListener(onClickListener);
            binding.TVStatus.setOnClickListener(onClickListener);
            binding.BTSubmit.setOnClickListener(onClickListener);
            binding.RGGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.RB_Reactive:
                            reactive_status = "1";
                            refer_ictc = "";
                            living_status = "";
                            binding.RGICTCGroup.clearCheck();
                            binding.RGLivingStatus.clearCheck();
                            binding.LTReactiveLayout.setVisibility(View.VISIBLE);
                            binding.submitLayout.setVisibility(View.GONE);
                            binding.LTPositiveLayout.setVisibility(View.GONE);
                            binding.TVStatus.setVisibility(View.GONE);
                            binding.TVStatus.setText("");
                            break;
                        case R.id.RB_Non_reactive:
                            reactive_status = "2";
                            refer_ictc = "";
                            living_status = "";
                            binding.RGICTCGroup.clearCheck();
                            binding.RGLivingStatus.clearCheck();
                            binding.LTPositiveLayout.setVisibility(View.GONE);
                            binding.TVStatus.setVisibility(View.GONE);
                            binding.TVStatus.setText("");
                            binding.LTReactiveLayout.setVisibility(View.GONE);
                            binding.submitLayout.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });
            binding.RGICTCGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.RB_Positive:
                            refer_ictc = "1";
                            living_status = "";
                            binding.LTPositiveLayout.setVisibility(View.VISIBLE);
                            binding.submitLayout.setVisibility(View.GONE);
                            binding.RGLivingStatus.clearCheck();
                            binding.TVStatus.setText("");
                            break;
                        case R.id.RB_Negative:
                            refer_ictc = "2";
                            living_status = "";
                            binding.LTPositiveLayout.setVisibility(View.GONE);
                            binding.submitLayout.setVisibility(View.VISIBLE);
                            binding.RGLivingStatus.clearCheck();
                            binding.TVStatus.setText("");
                            break;
                    }
                }
            });
            binding.RGLivingStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.RB_Live:
                            living_status = "1";
                            binding.TVStatus.setVisibility(View.VISIBLE);
                            binding.submitLayout.setVisibility(View.GONE);
                            binding.TVStatus.setText("");
                            break;
                        case R.id.RB_Death:
                            living_status = "2";
                            binding.TVStatus.setVisibility(View.GONE);
                            binding.submitLayout.setVisibility(View.VISIBLE);
                            binding.TVStatus.setText("");
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
                    startActivity(new Intent(HIVTestingActivity.this, MainActivity.class));
                    break;
                case R.id.TV_datePicker:
                    updateDate("");
                    break;
                case R.id.TV_Upload:
                    CaptureFromCamera("image");
                    break;
                case R.id.TV_status:
                    if (arrayList.size() > 0) {
                        SelectSelection(binding.TVStatus, arrayList, "status");
                    } else {
                        Helper.t(getApplicationContext(), "List is empty");
                    }
                    break;
                case R.id.BT_submit:
                    try {
                        String date = binding.TVDatePicker.getText().toString();
                        if (date.equals("") && date.length() == 0) {
                            Helper.t(getApplicationContext(), "Please select the date");
                        } else {
                            JSONObject params = new JSONObject();
                            params.put("addOrUpdateHIVTesting", "true");
                            params.put("beneficiaryid", beneficiaryid);
                            params.put("date", date);
                            params.put("image",image_name);
                            params.put("reactive_status", reactive_status);
                            params.put("refer_ictc", refer_ictc);
                            params.put("living_status", living_status);
                            params.put("live_status", status);
                            GetDATA(2, params);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void GetDATA(int index, JSONObject params) {
        if (Helper.isNetworkAvailable(HIVTestingActivity.this)) {
            AuthKey3.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        if (index == 1){
                            arrayList.clear();
                            JSONArray jsonArray = response_.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TwoBean twoBean = new TwoBean();
                                twoBean.setId(jsonObject.getString("id"));
                                twoBean.setName(jsonObject.getString("name"));
                                arrayList.add(twoBean);
                            }
                            if (!beneficiaryid.equals("")) {
                                JSONObject params2 = new JSONObject();
                                params2.put("getHIVTesting", "true");
                                params2.put("beneficiaryid",beneficiaryid);
                                GetDATA(3, params2);
                            }

                        }else if (index == 2){

                            if (response_.getString("status").equals("updated successfully")){
                                finish();
                                startActivity(new Intent(HIVTestingActivity.this, MainActivity.class));
                                Helper.t(getApplicationContext(), "Data Updated successful");
                            }else {
                                JSONArray jsonArray = response_.getJSONArray("data");
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject.has("refer_ictc")) {
                                    String refer_ictc = jsonObject.getString("refer_ictc");
                                    if (refer_ictc.equals("") || refer_ictc.equals("2")) {
                                        finish();
                                        startActivity(new Intent(HIVTestingActivity.this, RiskAssementActivity.class)
                                                .putExtra("beneficiaryid",beneficiaryid));
                                    } else {
                                        finish();
                                        startActivity(new Intent(HIVTestingActivity.this, HIVTesting2Activity.class)
                                                .putExtra("beneficiaryid",beneficiaryid));
                                    }
                                }
                            }
                        }else if (index == 3){
                            JSONArray jsonArray = response_.getJSONArray("data");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            binding.TVDatePicker.setText(jsonObject.getString("date"));
                            reactive_status = jsonObject.getString("reactive_status");
                            if (jsonObject.getString("reactive_status").equals("1")){
                                binding.RBReactive.setChecked(true);
                            }else{
                                binding.RBNonReactive.setChecked(true);
                            }
                            refer_ictc=jsonObject.getString("refer_ictc");
                            if (jsonObject.getString("refer_ictc").equals("1")){
                                binding.RBPositive.setChecked(true);
                            }else{
                                binding.RBNegative.setChecked(true);
                            }
                            living_status=jsonObject.getString("living_status");
                            if (jsonObject.getString("living_status").equals("1")){
                                binding.RBLive.setChecked(true);
                            }else{
                                binding.RBDeath.setChecked(true);
                            }
                            status = jsonObject.getString("live_status");
                            for (TwoBean t:arrayList) {
                                if (t.getId().equals(jsonObject.getString("live_status"))){
                                    binding.TVStatus.setText(t.getName());
                                }
                            }
                            binding.BTSubmit.setVisibility(View.VISIBLE);
                            binding.BTSubmit.setText("Update");

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
            }, UrlBase.BASE_URL, params, HIVTestingActivity.this, "show");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }

    private void updateDate(String str) {
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
        binding.TVDatePicker.setText(formatter.format(dateTime1.getTime()));
    }

    //camera
    private void CaptureFromCamera(String selection) {
        try {
            if (getAvailableSpaceInMB() >= 10) {

                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mrtag = Helper.getRandomString(8);
                sessionManager.storeVal("mrtag", String.valueOf(mrtag));

                //intent for camera
                Intent icapture;

                File photoFile = getPhotoFileUri(mrtag+ ".jpg");
                Uri fileProvider = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);

                String file_name = mrtag + ".jpg";
                sessionManager.storeVal("mrfile_name", file_name);
                sessionManager.storeVal("selection", "image");

                icapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                icapture.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(icapture, CAMERA_PICTURE);


            } else {
                sessionManager.storeVal("mrtag", "");
                sessionManager.storeVal("mrfile_name", "");
                Helper.t(getApplicationContext(), "Memory full kindly empty some space");
            }
        } catch (Exception e) {
            Helper.t(getApplicationContext(), e.getMessage());
        }

    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "APP_TAG");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("APP_TAG", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


    public static long getAvailableSpaceInMB() {
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        return availableSpace / SIZE_MB;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        switch (requestCode) {
            case CAMERA_PICTURE:
                if (resultCode == RESULT_OK) {
                    try {

                        final String[] file_name = {sessionManager.getStrVal("mrfile_name")};
                        Helper.l("filename" + file_name[0]);


                        File photoFile = getPhotoFileUri(mrtag+ ".jpg");
                        mrtag = sessionManager.getStrVal("mrtag");
                        String sel_ = sessionManager.getStrVal("selection");

                        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        img_base64 = Helper.getStringImage(bitmap);

                        String destPath = photoFile.getAbsolutePath();

                        //upload to server and attach to view
                        Map<String, String> params = new LinkedHashMap<String, String>();

                        /*params.put("filename", file_name[0]);
                        params.put("filepath", destPath);
                        params.put("image", img_base64);
                        params.put("username", sessionManager.getStrVal(Helper.Apsacs_Name));
                        params.put("uploadFileNew", "true");*/


                        if (Helper.isNetworkAvailable(HIVTestingActivity.this)) {
                            new uploadService(file_name[0], destPath, 2, sel_).execute(params);
                        } else {
                            file_name[0] = "";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Helper.t(getApplicationContext(), e.getMessage());
                    }
                }
                break;
            default:
                Helper.l("Reached default");
                break;
        }
    }

    private class uploadService extends AsyncTask<Map, Integer, String> {
        String local_image_name = "", localdestPath = "", local_image_video = "";
        int localindex = 0;

        public uploadService(String file_ff, String destPath, int i, String image_video) {
            local_image_name = file_ff;
            localdestPath = destPath;
            localindex = i;
            local_image_video = image_video;
            //if i is 1 gallery else camera
            //image_video is image or video
        }

        @Override
        protected String doInBackground(Map... params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return postData(params[0]);
            }else {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            Helper.showProgressDialog(HIVTestingActivity.this);
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected void onPostExecute(String response) {
            Helper.dismissProgressDialog();

            JSONObject result = null;

            Helper.l("HTTP Request Result: " + response);

            try {
                String dep = String.valueOf(AES.decrypt(sessionManager.getStrVal("android_id"),response));

                result = new JSONObject(dep);
                String res = result.getString("result");
                //Log.d("HTTP result filesync",response.toString());

                if (result.getString("result").equals("success")) {
                    Helper.l("image/video upload is successfull");
                    Helper.t(getApplicationContext(), "image/video upload is successfull");
                    if (local_image_video.equalsIgnoreCase("image")) {
                        String image = result.getString("filepath");
                        binding.imageDisplay.setBackground(getResources().getDrawable(R.drawable.rounded_green));
                        image_name = local_image_name;
                        binding.imageDisplay.setVisibility(View.VISIBLE);

                        Glide.with(HIVTestingActivity.this)
                                .load(image)
                                .centerCrop()
                                .placeholder(R.mipmap.newloading)
                                .into(binding.imageDisplay);
                    } else {
                        Helper.t(getApplicationContext(),result.getString("error"));
                    }
                } else {
                    Helper.t(getApplicationContext(),result.getString("error"));
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        protected void onProgressUpdate(Integer... progress) {
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public String postData(Map data) {

            String response = "default response";

            try {
                //mckgc.ap.gov.in/covid19_app/mobile.php?uploadFile=true&filename=&file=
                PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String versionName = pinfo.versionName;

                Map<String,String> headers = new LinkedHashMap<String, String>();
                headers.put("Auth-Key",sessionManager.getStrVal(Helper.Apsacs_Token));
                headers.put("username",sessionManager.getStrVal(Helper.Apsacs_Name));
                headers.put("Androidid", sessionManager.getStrVal("android_id"));
                headers.put("ver",versionName);

//                String destPath = Environment.getExternalStorageDirectory() + "/wm/" + local_image_name;
                HttpRequest request = HttpRequest.post(UrlBase.BASE_URL + "newapi.php?uploadFileNew=1")
                        .headers(headers);
                File file = new File(localdestPath);
                if (file.exists() || file.isFile()){
                    double lll = file.length();
                    Log.d("file size",String.valueOf(lll));
                }

                request.part("filename", local_image_name);
                request.part("username", sessionManager.getStrVal(Helper.Apsacs_Name));
                request.part("uploadFileNew", "true");
                request.part("image", img_base64);
                //request.part("file", local_image_name,file);
                String esp = String.valueOf(AES.encrypt(sessionManager.getStrVal("android_id"), new File(localdestPath).toString()));
                if (request.ok()) {
                    System.out.println("Status was updated");
                    response = request.body();
                    request.code();
                } else {

                    String code = String.valueOf(request.code());
                    response = request.body();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response.trim().replaceAll("\\s", "");

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

        Dialog _dialogg = new Dialog(HIVTestingActivity.this, R.style.SuccessFailureDialogTheme);
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
            SelectionAdapter2 selectionAdapter = new SelectionAdapter2(list_, HIVTestingActivity.this,selection, new CLickCallBack2() {
                @Override
                public void onClick(TwoBean result) {
                    _dialogg.dismiss();
                    tv_attach.setText(result.getName());
                    AttachDataDialog(selection, result);
                }
            });
            rv_Selection.setLayoutManager(new LinearLayoutManager(HIVTestingActivity.this));
            rv_Selection.setAdapter(selectionAdapter);
            selectionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AttachDataDialog(String selection, TwoBean result) {
        try {
            if (selection.equalsIgnoreCase("status")) {
                status = result.getId();
                binding.submitLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
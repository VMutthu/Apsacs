package com.entrolabs.apsacs.Apikey;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.entrolabs.apsacs.Common.AES;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;

/**
 * Created by kk on 4/10/2018.
 */


public class AuthKey3 {
    static SessionManager sessionManager;
    static VolleyCallback global_callback;
    static String global_url= "";
    static JSONObject global_params;
    static Context global_context;
    static String global_loader;
    static JSONObject global_json;

    public static void VolleyCallBack(final VolleyCallback callback, final String Volley_URL, JSONObject params, final Context context, String loader_index) {

        sessionManager = new SessionManager(context);
        if(loader_index.equalsIgnoreCase("show")){
            Helper.showProgressDialog(context);
        }
        global_callback =  callback;
        global_url = Volley_URL;
        global_params = params;
        global_context =  context;
        global_loader =  loader_index;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DataChannel();
        }else{
            Helper.t(global_context,"This mobile version will not supported for this app.");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void DataChannel() {
        try {
            String versionName = "";
            try {
                PackageInfo pinfo = global_context.getPackageManager().getPackageInfo(global_context.getPackageName(), 0);
                versionName = pinfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            Map<String, String> params = new HashMap<String, String>();
            params.put("Ver", versionName);
            //params.put("username", sessionManager.getStrVal(Helper. Apsacs_Username));
            String Token_esp = String.valueOf(AES.encrypt(sessionManager.getStrVal("android_id"),sessionManager.getStrVal(Helper. Apsacs_Token)));
            params.put("Auth-Key", Token_esp);
            params.put("App-Id", global_context.getPackageName());
            params.put("Androidid", sessionManager.getStrVal("android_id"));
            params.put("Content-Type", "application/json");

            RestService service = RetrofitClientService.getRetrofitInstance().create(RestService.class);
            String esp = String.valueOf(AES.encrypt(sessionManager.getStrVal("android_id"),global_params.toString()));

            Call<String> call = service.getLogin1(params,esp);
            call.enqueue(new Callback<String>() {


                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    Helper.l("response "+ response.toString());
                    Helper.dismissProgressDialog();
                    if (response.isSuccessful()) {
                        Helper.l("response "+ response.body());
                        String dep = String.valueOf(AES.decrypt(sessionManager.getStrVal("android_id"),response.body()));
                        try {
                            JSONObject jsonObject = new JSONObject(dep);
                            String apiresult = jsonObject.getString("result");

                            if (apiresult.equalsIgnoreCase("success")) {
                                ///sessionManager.storeVal(Helper.Drcare_token, response.getString("token"));
                                global_callback.onSuccess(jsonObject);
                            } else {
                                if (jsonObject.getString("result").equalsIgnoreCase("failed") &&
                                        ((jsonObject.getString("error").equalsIgnoreCase("Invalid or token expired"))
                                                || (jsonObject.getString("error").equalsIgnoreCase("No key generated for user")))) {
                                    RefreshToken();
                                }
                                else{
                                    global_callback.onFailure(jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(getApplicationContext(), "Success"+response.body().getUser_type(), Toast.LENGTH_SHORT).show();

                    }else if (response.code() == 400 || response.code() == 401 || response.code() == 404){
                        Helper.l("response "+ String.valueOf(response.errorBody()));
                        try{
                            ResponseBody spo = response.errorBody();
                           String msg =  new String(spo.bytes());
                            String dep = String.valueOf(AES.decrypt(sessionManager.getStrVal("android_id"),msg));
                            try {
                                JSONObject jsonObject = new JSONObject(dep);
                                String apiresult = jsonObject.getString("result");

                                if (apiresult.equalsIgnoreCase("success")) {
                                    ///sessionManager.storeVal(Helper.Drcare_token, response.getString("token"));
                                    global_callback.onSuccess(jsonObject);
                                } else {
                                    if (jsonObject.getString("result").equalsIgnoreCase("failed") &&
                                            ((jsonObject.getString("error").equalsIgnoreCase("Invalid or token expired"))
                                                    || (jsonObject.getString("error").equalsIgnoreCase("No key generated for user")))) {
                                        RefreshToken();
                                    }else if (jsonObject.getString("error").equalsIgnoreCase("logout")){
                                        global_callback.onLogout("Session Expired");
                                    }
                                    else{
                                        global_callback.onFailure(jsonObject);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //errMsg = json.getString("message");
                        }
                        catch(IOException e){
                             e.getMessage();
                        }

                    }
                    else {
                        Helper.l("the value is code error "+ String.valueOf(response.code()));
                        String dep = String.valueOf(AES.decrypt(sessionManager.getStrVal("android_id"),response.body()));


                        Toast.makeText(global_context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Helper.l("the value is code error "+ "error");
                    String message = t.getMessage();
                    Helper.l("failure "+ message);
                    Helper.dismissProgressDialog();
                    Helper.l("the value is code error "+ "error");
                    //Toast.makeText(global_context, "Invalid Email Id and Password", Toast.LENGTH_SHORT).show();
                    Toast.makeText(global_context, message, Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e){e.printStackTrace();}
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void RefreshToken() {
        try {
            String versionName = "";
            try {
                PackageInfo pinfo = global_context.getPackageManager().getPackageInfo(global_context.getPackageName(), 0);
                versionName = pinfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            Map<String, String> params = new HashMap<String, String>();
            params.put("Ver", versionName);
            //params.put("username", sessionManager.getStrVal(Helper. Apsacs_Username));
            String Token_esp = String.valueOf(AES.encrypt(sessionManager.getStrVal("android_id"),sessionManager.getStrVal(Helper. Apsacs_Token)));
            params.put("Auth-Key", Token_esp);
            params.put("App-Id", global_context.getPackageName());
            params.put("Androidid", sessionManager.getStrVal("android_id"));
            params.put("Content-Type", "application/json");

            JSONObject localparams = new JSONObject();
            localparams.put("getToken", "true");
            localparams.put("session", sessionManager.getStrVal(Helper.Apsacs_Username));

            RestService service = RetrofitClientService.getRetrofitInstance().create(RestService.class);
            String esp = String.valueOf(AES.encrypt(sessionManager.getStrVal("android_id"),localparams.toString()));

            Call<String> call = service.getLogin1(params,esp);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    Helper.l("response "+ response.toString());
                    if (response.isSuccessful()) {
                        Helper.l("response "+ response.toString());
                        String dep = String.valueOf(AES.decrypt(sessionManager.getStrVal("android_id"),response.toString()));

                        try{
                            JSONObject apiresult = new JSONObject(dep);
                            if (apiresult.getString("result").equalsIgnoreCase("success")) {
                                sessionManager.storeVal(Helper. Apsacs_Token, apiresult.getString("token"));
                                DataChannel();
                            } else {
                                if (apiresult.getString("error").equalsIgnoreCase("logout")) {
                                    global_callback.onLogout("Session Expired");
                                } else {
                                    //global_callback.onFailure(response.body().getAsJsonObject());
                                    String jsonss = response.body().toString();
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonss);
                                        global_callback.onFailure(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //Toast.makeText(getApplicationContext(), "Success"+response.body().getUser_type(), Toast.LENGTH_SHORT).show();

                    }else if (response.code() == 400 || response.code() == 401 || response.code() == 404){
                        Helper.l("response "+ String.valueOf(response.errorBody()));
                        try{
                            ResponseBody spo = response.errorBody();
                            String msg =  new String(spo.bytes());
                            String dep = String.valueOf(AES.decrypt(sessionManager.getStrVal("android_id"),msg));
                            try {
                                JSONObject jsonObject = new JSONObject(dep);
                                String apiresult = jsonObject.getString("result");

                                if (apiresult.equalsIgnoreCase("success")) {
                                    ///sessionManager.storeVal(Helper.Drcare_token, response.getString("token"));
                                    DataChannel();
                                } else {
                                    if (jsonObject.getString("error").equalsIgnoreCase("logout")) {
                                        global_callback.onLogout("logout");
                                    }
                                    else{
                                        global_callback.onFailure(jsonObject);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //errMsg = json.getString("message");
                        }
                        catch(IOException e){
                            e.getMessage();
                        }

                    } else {
                        Helper.l("the value is code error "+ String.valueOf(response.code()));

                        Toast.makeText(global_context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Helper.l("the value is code error "+ "error");
                    String message = t.getMessage();
                    Helper.l("failure "+ message);

                    Helper.l("the value is code error "+ "error");
                    Toast.makeText(global_context, "Invalid Email Id and Password", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e){e.printStackTrace();}
    }
}


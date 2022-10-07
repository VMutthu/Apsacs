package com.entrolabs.apsacs.Apikey;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.entrolabs.apsacs.Common.AES;
import com.entrolabs.apsacs.Common.Helper;
import com.entrolabs.apsacs.Common.SessionManager;
import com.entrolabs.apsacs.URL.UrlBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kk on 4/10/2018.
 */


public class AuthKey2 {
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
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void DataChannel() {
        try{
            RequestQueue queue = Volley.newRequestQueue(global_context);
            String esp = String.valueOf(AES.encrypt(sessionManager.getStrVal("android_id"),global_params.toString()));

            final String requestBody = esp;
            StringRequest req_obj = new StringRequest(Request.Method.POST, UrlBase.BASE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String sresponse) {
                    Helper.l("response : " + sresponse.toString());

                    Helper.dismissProgressDialog();
                    String dep = String.valueOf(AES.decrypt(sessionManager.getStrVal("android_id"),sresponse));
                    try {
                        JSONObject response = new JSONObject(dep);
                        Helper.l("response : " + response.toString());
                        String apiresult = response.getString("result");

                        if (apiresult.equalsIgnoreCase("success")) {
                            global_callback.onSuccess(response);
                        } else {
                            if (response.getString("result").equalsIgnoreCase("failed") &&
                                    ((response.getString("error").equalsIgnoreCase("Invalid or token expired"))
                                            || (response.getString("error").equalsIgnoreCase("No key generated for user")))) {
                                RefreshToken();
                            }
                            else{
                                global_callback.onFailure(response);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        global_callback.onException(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Helper.dismissProgressDialog();
                    global_callback.onError(error.getMessage());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                            /*@Override
                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                String responseString = "";
                                if (response != null) {
                                    responseString = String.valueOf(response.data);
                                    // can get more details such as response.headers
                                }
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                            }*/

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String versionName = "";
                    try {
                        PackageInfo pinfo = global_context.getPackageManager().getPackageInfo(global_context.getPackageName(), 0);
                        versionName = pinfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Ver", versionName);
                    params.put("username", sessionManager.getStrVal(Helper. Apsacs_Username));
                    params.put("Auth-Key", sessionManager.getStrVal(Helper. Apsacs_Token));
                    params.put("App-Id", global_context.getPackageName());
                    params.put("Androidid", sessionManager.getStrVal("android_id"));
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            int socketTimeout = 0;//1 min - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req_obj.setRetryPolicy(policy);
            req_obj.setShouldCache(false);
            queue.getCache().clear();
            // Adding request to request queue
            queue.add(req_obj);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void RefreshToken() {
        try{

            JSONObject localparams = new JSONObject();
            localparams.put("getToken", "true");
            localparams.put("session", sessionManager.getStrVal(Helper. Apsacs_Token));

            RequestQueue queue = Volley.newRequestQueue(global_context);

            String esp = String.valueOf(AES.encrypt(sessionManager.getStrVal("android_id"),localparams.toString()));

            final String requestBody = esp;
            StringRequest req_obj = new StringRequest(Request.Method.POST, UrlBase.BASE_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String sresponse) {
                    String dep = String.valueOf(AES.decrypt(sessionManager.getStrVal("android_id"),sresponse));
                    try {
                        JSONObject response = new JSONObject(dep);
                        String apiresult = response.getString("result");

                        if (apiresult.equalsIgnoreCase("success")) {
                            sessionManager.storeVal(Helper. Apsacs_Token, response.getString("token"));
                            DataChannel();
                        } else {
                            if(response.getString("error").equalsIgnoreCase("logout")){
                                global_callback.onLogout("logout");
                            }

                            else{
                                global_callback.onFailure(response);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        global_callback.onException(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Helper.dismissProgressDialog();
                    global_callback.onError(error.getMessage());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                            /*@Override
                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                String responseString = "";
                                if (response != null) {
                                    responseString = String.valueOf(response.data);
                                    // can get more details such as response.headers
                                }
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                            }*/

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String versionName = "";
                    try {
                        PackageInfo pinfo = global_context.getPackageManager().getPackageInfo(global_context.getPackageName(), 0);
                        versionName = pinfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Ver", versionName);
                    params.put("username", sessionManager.getStrVal(Helper. Apsacs_Username));
                    params.put("Auth-Key", sessionManager.getStrVal(Helper. Apsacs_Token));
                    params.put("App-Id", global_context.getPackageName());
                    params.put("Androidid", sessionManager.getStrVal("android_id"));
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            int socketTimeout = 0;//1 min - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req_obj.setRetryPolicy(policy);
            req_obj.setShouldCache(false);
            queue.getCache().clear();
            // Adding request to request queue
            queue.add(req_obj);


        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}

